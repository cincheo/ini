package ini.eval;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import ini.Main;
import ini.ast.ArrayAccess;
import ini.ast.Assignment;
import ini.ast.AstElement;
import ini.ast.AstNode;
import ini.ast.AtBinding;
import ini.ast.BinaryOperator;
import ini.ast.Binding;
import ini.ast.BooleanLiteral;
import ini.ast.CaseStatement;
import ini.ast.ChannelDeclaration;
import ini.ast.CharLiteral;
import ini.ast.ConditionalExpression;
import ini.ast.ConstructorMatchExpression;
import ini.ast.Executable;
import ini.ast.Expression;
import ini.ast.FieldAccess;
import ini.ast.Import;
import ini.ast.Invocation;
import ini.ast.ListExpression;
import ini.ast.NumberLiteral;
import ini.ast.Process;
import ini.ast.ReturnStatement;
import ini.ast.Rule;
import ini.ast.Sequence;
import ini.ast.SetConstructor;
import ini.ast.SetDeclaration;
import ini.ast.SetExpression;
import ini.ast.Statement;
import ini.ast.StringLiteral;
import ini.ast.SubArrayAccess;
import ini.ast.TypeVariable;
import ini.ast.UnaryOperator;
import ini.ast.Variable;
import ini.ast.VariableAccess;
import ini.broker.FetchRequest;
import ini.broker.SpawnRequest;
import ini.eval.at.At;
import ini.eval.data.Data;
import ini.eval.data.DataReference;
import ini.eval.data.FutureData;
import ini.eval.data.RawData;
import ini.eval.data.TypeInfo;
import ini.eval.function.BoundExecutable;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;
import ini.type.TypeChecker;
import ini.type.TypingConstraint;
import ini.type.TypingConstraint.Kind;
import ini.type.TypingError;

public class IniEval {

	public IniParser parser;
	public AstAttrib attrib;
	public Stack<Context> invocationStack = new Stack<Context>();
	public Stack<AstNode> evaluationStack = new Stack<AstNode>();
	public Data result;
	public boolean error = false;
	public List<IniEval> forkedEvals = new ArrayList<IniEval>();
	boolean rulePassed = false;
	public boolean kill = false;
	public static final String PROCESS_RESULT = "__process_result";
	public Set<String> importedFiles = new HashSet<>();

	public IniEval(IniParser parser, AstAttrib attrib, Context rootContext) {
		this.parser = parser;
		this.attrib = attrib;
		if (rootContext == null) {
			throw new RuntimeException("root context cannot be null");
		}
		for (Executable executable : parser.builtInExecutables) {
			rootContext.bind(executable.name, new RawData(executable));
		}
		this.invocationStack.push(rootContext);
	}

	private final String getTargetNode(AstElement element) {
		String targetNode = null;
		if (element.annotations != null && !element.annotations.isEmpty()) {
			for (Expression e : element.annotations) {
				if (e instanceof Assignment) {
					Assignment a = (Assignment) e;
					String name = a.assignee.toString();
					if ("node".equals(name)) {
						targetNode = eval(a.assignment).getValue();
					}
				}
			}
		}
		return targetNode;
	}

	public Context getRootContext() {
		return invocationStack.get(0);
	}

	@SuppressWarnings("unchecked")
	public Data eval(AstNode node) {
		if (kill) {
			throw new KilledException();
		}
		String name;
		Executable f;
		Data d;
		int i;

		try {
			evaluationStack.push(node);

			switch (node.nodeTypeId()) {

			case AstNode.PREDICATE:
				// ignore
				break;

			case AstNode.IMPORT:
				try {
					if (importedFiles.contains(((Import) node).filePath)) {
						// import files only once
						Main.LOGGER.debug("already imported file '" + ((Import) node).filePath + "'");
						break;
					}
					IniParser localParser = ((Import) node).importParser;
					if (localParser == null) {
						localParser = IniParser.createParserForFile(parser.env, parser,
								((Import) node).filePath.toString());
						localParser.parse();
					}
					if (localParser.hasErrors()) {
						localParser.printErrors(parser.err);
						throw new EvalException(this, "Error while importing file '" + ((Import) node).filePath);
					} else {
						importedFiles.add(((Import) node).filePath);
						for (AstNode n : localParser.topLevels) {
							result = eval(n);
						}
					}
				} catch (java.io.FileNotFoundException e) {
					throw new RuntimeException("Cannot import file '" + ((Import) node).filePath + "'"
							+ (((Import) node).token != null ? " at " + ((Import) node).token.getLocation() : ""));
				}
				break;

			case AstNode.ARRAY_ACCESS:
				result = eval(((ArrayAccess) node).targetExpression);
				if (result.getTypeInfo() == TypeInfo.CHANNEL) {
					if (((ChannelDeclaration) result.getValue()).indexed) {
						result = new RawData(((ChannelDeclaration) result.getValue())
								.getComponent((int) eval(((ArrayAccess) node).indexExpression).getValue()));
					} else {
						throw new EvalException(this, "cannot access indexed channel on regular channel");
					}
				} else {
					result = result.get(eval(((ArrayAccess) node).indexExpression).getValue());
				}
				break;

			case AstNode.ASSIGNMENT:
				d = eval(((Assignment) node).assignee);
				if (d instanceof DataReference) {
					Data d2 = eval(((Assignment) node).assignment);
					if (!(d2 instanceof FutureData) && d2.isPrimitive()) {
						d.copyData(d2);
					} else {
						((DataReference) d).setReferencedData(d2);
					}
				} else {
					d.copyData(eval(((Assignment) node).assignment));
				}
				result = d;
				break;

			case AstNode.CONDITIONAL:
				d = eval(((ConditionalExpression) node).condition);
				if (d.getBoolean()) {
					result = eval(((ConditionalExpression) node).trueExpression);
				} else {
					result = eval(((ConditionalExpression) node).falseExpression);
				}
				break;

			case AstNode.BOUND_EXECUTABLE:
				getRootContext().bind(((BoundExecutable) node).name, new RawData(node));
				break;

			case AstNode.BINDING:
				if (((Binding) node).name != null) {
					d = getRootContext().get(((Binding) node).name);
					if (d != null) {
						if (d.getValue() instanceof BoundExecutable) {
							((BoundExecutable) d.getValue()).addBindingOverload((Binding) node);
						} else if (d.getValue() instanceof Executable) {
							throw new EvalException(this, "binding cannot overload/override  executable");
						}
					} else {
						getRootContext().bind(((Binding) node).name,
								new RawData(new BoundExecutable(((Binding) node))));
					}
				}
				break;

			case AstNode.CHANNEL:
				getRootContext().bind(((ChannelDeclaration) node).name, new RawData((ChannelDeclaration) node));
				break;

			case AstNode.AT_BINDING:
				getRootContext().bind(((AtBinding) node).name, new RawData(node));
				try {
					// TODO: not required
					At.atPredicates.put(((AtBinding) node).name,
							(Class<? extends At>) Class.forName(((AtBinding) node).className));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				break;

			case AstNode.BINARY_OPERATOR:
				BinaryOperator b = (BinaryOperator) node;
				switch (b.kind) {
				case AND:
					d = eval(b.left);
					if (!d.isTrueOrDefined()) {
						result = new RawData(false);
					} else {
						result = new RawData(eval(b.right).isTrueOrDefined());
					}
					break;
				case OR:
					d = eval(b.left);
					if (d.isTrueOrDefined()) {
						result = new RawData(true);
					} else {
						result = new RawData(eval(b.right).isTrueOrDefined());
					}
					break;
				case DIV:
					result = new RawData(
							eval(b.left).getNumber().doubleValue() / eval(b.right).getNumber().doubleValue());
					break;
				case MULT:
					result = new RawData(mult(eval(b.left).getNumber(), eval(b.right).getNumber()));
					break;
				case EQUALS:
					result = new RawData(eval(b.left).equals(eval(b.right)));
					break;
				case NOTEQUALS:
					try {
						result = new RawData(!eval(b.left).getValue().equals(eval(b.right).getValue()));
					} catch (NullPointerException e) {
						result = new RawData(true);
					}
					break;
				case GT:
					try {
						result = new RawData(((Comparable<Object>) eval(b.left).getValue())
								.compareTo(((Comparable<Object>) eval(b.right).getValue())) > 0);
					} catch (NullPointerException e) {
						result = new RawData(false);
					}
					break;
				case GTE:
					try {
						result = new RawData(((Comparable<Object>) eval(b.left).getValue())
								.compareTo(((Comparable<Object>) eval(b.right).getValue())) >= 0);
					} catch (NullPointerException e) {
						result = new RawData(false);
					}
					break;
				case LT:
					try {
						result = new RawData(((Comparable<Object>) eval(b.left).getValue())
								.compareTo(((Comparable<Object>) eval(b.right).getValue())) < 0);
					} catch (NullPointerException e) {
						result = new RawData(false);
					}
					break;
				case LTE:
					try {
						result = new RawData(((Comparable<Object>) eval(b.left).getValue())
								.compareTo(((Comparable<Object>) eval(b.right).getValue())) <= 0);
					} catch (NullPointerException e) {
						result = new RawData(false);
					}
					break;
				case MINUS:
					result = new RawData(minus(eval(b.left).getNumber(), eval(b.right).getNumber()));
					break;
				case PLUS:
					d = eval(b.left);
					Object o = d.getValue();
					if (o instanceof String) {
						result = new RawData((String) o + eval(b.right).toPrettyString());
					} else {
						result = new RawData(plus(eval(b.left).getNumber(), eval(b.right).getNumber()));
					}
					break;
				case MATCHES:
					evalMatches(b);
					break;
				case CONCAT:
					result = eval(b.left).concat(eval(b.right));
					break;
				default:
					throw new RuntimeException("unsuported operator: " + b);
				}
				break;

			case AstNode.BOOLEAN_LITERAL:
				result = new RawData(((BooleanLiteral) node).value);
				break;

			case AstNode.CHAR_LITERAL:
				result = new RawData(((CharLiteral) node).value);
				break;

			case AstNode.FIELD_ACCESS:
				result = eval(((FieldAccess) node).targetExpression).get(((FieldAccess) node).fieldName);
				break;

			case AstNode.FUNCTION:
			case AstNode.PROCESS:
				f = (Executable) node;
				if (f.name != null) {
					result = getRootContext().get(f.name);
					if (result != null) {
						if ((result.getValue() instanceof BoundExecutable)
								&& ((BoundExecutable) result.getValue()).binding.isLocal()) {
							throw new EvalException(this, "cannot override existing binding");
						} else if (result.getValue() instanceof Executable) {
							if (((Executable) result.getValue()).parameters.size() == f.parameters.size()) {
								// override case
								result = new RawData(f);
								getRootContext().bind(f.name, result);
							} else {
								((Executable) result.getValue()).addOverload(f);
							}
						}
					} else {
						result = new RawData(f);
						getRootContext().bind(f.name, result);
					}
				} else {
					// lambda
					result = new RawData(f);
					f.accessibleContext = invocationStack.peek();
				}
				break;

			case AstNode.INVOCATION:
				Invocation invocation = (Invocation) node;

				f = lookupExecutable(invocation);

				if (f == null) {
					throw new EvalException(this, "cannot find exectuable '" + invocation.name + "'");
				} else {
					f = f.resolveOverload(attrib, invocation);
					String targetNode = null;
					if (parser.env.deamon) {
						targetNode = getTargetNode(invocation);
					}

					if (!StringUtils.isEmpty(targetNode) && !targetNode.equals(parser.env.node)) {
						spawnExecutable(invocation, f, targetNode);
						// TODO: wait for result?
						this.result = null;
					} else {
						if (f.parameters.size() < invocation.arguments.size()) {
							throw new RuntimeException("wrong number of parameters at " + node);
						}
						Context ctx = new Context(f, invocation);
						for (i = 0; i < f.parameters.size(); i++) {
							if (i > invocation.arguments.size() - 1) {
								if (f.parameters.get(i).defaultValue == null) {
									throw new RuntimeException("no value or default value given for parameter '"
											+ f.parameters.get(i).name + "' at " + node);
								} else {
									invocationStack.push(ctx);
									ctx.bind(f.parameters.get(i).name, eval(f.parameters.get(i).defaultValue));
									invocationStack.pop();
								}
							} else {
								ctx.bind(f.parameters.get(i).name, eval(invocation.arguments.get(i)));
							}
						}

						ctx.merge(f.accessibleContext);

						invocationStack.push(ctx);
						try {
							if (f instanceof Process) {
								final Process process = (Process) f;
								result = new FutureData();
								ctx.bind(PROCESS_RESULT, result);
								IniEval child = fork();
								new Thread(new Runnable() {
									@Override
									public void run() {
										process.eval(child);
									}
								}).start();
							} else {
								f.eval(this);
							}
						} finally {
							invocationStack.pop();
						}
					}
				}

				break;

			case AstNode.LIST_EXPRESSION:
				i = 0;
				d = new RawData(null);
				d.setKind(Data.Kind.INT_SET);
				for (Expression e : ((ListExpression) node).elements) {
					d.set(i++, eval(e));
				}
				result = d;
				break;

			case AstNode.NUMBER_LITERAL:
				result = new RawData(((NumberLiteral) node).value);
				break;

			case AstNode.RETURN_STATEMENT:
				if (((ReturnStatement) node).expression != null) {
					result = eval(((ReturnStatement) node).expression);
				} else {
					result = new RawData();
				}
				Context ctx = invocationStack.peek();
				Data r = ctx.get(PROCESS_RESULT);
				if (r != null) {
					r.copyData(result);
				}
				throw new ReturnException();

			case AstNode.USER_TYPE:
				// ignore for evaluation (used by attribution)
				break;

			case AstNode.CASE_STATEMENT:
				List<Rule> caseRules = ((CaseStatement) node).cases;
				Rule matchedRule = null;
				for (Rule rule : caseRules) {
					if (((Rule) rule).guard != null && eval(((Rule) rule).guard).isTrueOrDefined()) {
						matchedRule = rule;
						eval(matchedRule);
						break;
					}
				}
				if (matchedRule == null) {
					Sequence<Statement> s = ((CaseStatement) node).defaultStatements;
					while (s != null) {
						eval(s.get());
						s = s.next();
					}
				}
				break;

			case AstNode.RULE:
				if (((Rule) node).guard == null || eval(((Rule) node).guard).isTrueOrDefined()) {
					invocationStack.peek().noRulesApplied = false;
					// invocationStack.push(new
					// Context(invocationStack.peek()));
					Sequence<Statement> s = ((Rule) node).statements;
					while (s != null) {
						eval(s.get());
						s = s.next();
					}
					// invocationStack.pop();
				}
				break;

			case AstNode.SET_CONSTRUCTOR:
				String constructorName = ((SetConstructor) node).name;
				d = new RawData(null);
				TypeVariable constructor = TypeVariable.create(constructorName, new ArrayList<>());
				constructor.parser = parser;
				if (((SetConstructor) node).fieldAssignments != null) {
					for (Assignment a : ((SetConstructor) node).fieldAssignments) {
						d.set(((Variable) a.assignee).name, eval(a.assignment));
						constructor.fields.add(((Variable) a.assignee).name);
					}
				}
				d.setConstructor(constructor);
				if (constructorName != null) {
					Data set = invocationStack.peek().getOrCreate(constructorName);
					set.set(d, d);
					// set.copyData(d);
				}
				result = d;
				break;

			case AstNode.SET_DECLARATION:
				d = new RawData(null);
				d.setKind(Data.Kind.INT_SET);
				d.set(Data.LOWER_BOUND_KEY, eval(((SetDeclaration) node).lowerBound));
				d.set(Data.UPPER_BOUND_KEY, eval(((SetDeclaration) node).upperBound));
				if (d.get(Data.UPPER_BOUND_KEY).getNumber().intValue() < d.get(Data.LOWER_BOUND_KEY).getNumber()
						.intValue()) {
					throw new RuntimeException("invalid set bounds");
				}
				i = 0;
				for (int j = d.get(Data.LOWER_BOUND_KEY).getNumber().intValue(); j <= d.get(Data.UPPER_BOUND_KEY)
						.getNumber().intValue(); j++) {
					d.set(i++, new RawData(j));
				}

				result = d;
				break;

			case AstNode.SET_EXPRESSION:
				SetIterator it = new SetIterator(invocationStack.peek(), (SetExpression) node,
						eval(((SetExpression) node).set));
				result = new RawData(false);
				while (it.nextElement()) {
					d = eval(((SetExpression) node).expression);
					if (d.getBoolean()) {
						result = d;
						break;
					}
				}
				break;

			case AstNode.STRING_LITERAL:
				/*
				 * Matcher m = stringPlaceHolderPattern.matcher(((StringLiteral)
				 * node).value); StringBuffer substitutedString = new
				 * StringBuffer(); while(m.find()) {
				 * m.appendReplacement(substitutedString,
				 * invocationStack.peek().get(m.group(2)).toString()); } d = new
				 * RawData(substitutedString.length()>0?substitutedString.
				 * toString():((StringLiteral) node).value);
				 */
				d = new RawData(((StringLiteral) node).value);
				d.setKind(Data.Kind.INT_SET);
				result = d;
				break;

			case AstNode.SUB_ARRAY_ACCESS:
				SubArrayAccess sub = (SubArrayAccess) node;
				d = eval(sub.targetExpression);
				result = d.subArray((Integer) eval(sub.minExpression).getValue(),
						(Integer) eval(sub.maxExpression).getValue());
				break;

			case AstNode.THIS_LITERAL:
				result = new RawData(Thread.currentThread());
				break;

			case AstNode.UNARY_OPERATOR:
				UnaryOperator u = (UnaryOperator) node;
				switch (u.kind) {
				case MINUS:
					result = new RawData(minus(eval(u.operand).getNumber()));
					break;
				case OPT:
					// TODO
					result = eval(u.operand);
					break;
				case POST_DEC:
					result = eval(u.operand);
					if (!u.expanded) {
						expandPostDecrement((UnaryOperator) node);
					}
					break;
				case POST_INC:
					result = eval(u.operand);
					if (!u.expanded) {
						expandPostIncrement((UnaryOperator) node);
					}
					break;
				case NOT:
					result = new RawData(!eval(u.operand).isTrueOrDefined());
					break;
				default:
					throw new RuntimeException("unsupported operator " + u);
				}
				break;

			case AstNode.VARIABLE:
			case AstNode.TYPE_VARIABLE:
				name = ((Variable) node).name;
				if (((Variable) node).channelLiteral != null) {
					result = new RawData(((Variable) node).channelLiteral);
					break;
				}
				Assignment a = getParentNode(Assignment.class);
				if (a != null && a.assignee == node) {
					result = invocationStack.peek().getOrCreate(name);
				} else {
					if (!invocationStack.peek().hasBinding(name) && getRootContext().hasBinding(name)) {
						result = getRootContext().get(name);
					} else {
						result = invocationStack.peek().getOrCreate(name);
					}
				}
				break;

			default:
				throw new RuntimeException("unsuported syntax node: " + node);

			}

		} catch (ReturnException e) {
			throw e;
		} catch (EvalException e) {
			throw e;
		} catch (Exception e) {
			throw new EvalException(this, e);
		} finally {
			evaluationStack.pop();
		}
		return result;
	}

	public static void printEvaluationStackTrace(Stack<AstNode> evaluationStack, PrintStream out) {
		for (int i = evaluationStack.size() - 1; i >= 0; i--) {
			AstNode node = evaluationStack.get(i);
			out.print("    ");
			printNode(out, node);
			out.println();
		}
	}

	public static void printInvocationStackTrace(Stack<Context> invocationStack, PrintStream out) {
		for (int i = invocationStack.size() - 1; i >= 0; i--) {
			Context context = invocationStack.get(i);
			out.print("    ");
			out.print(context.toString());
			out.println();
		}
	}

	public static void printNode(PrintStream out, AstNode node) {
		out.print("'" + node + "'" + (node != null && node.token() != null ? " at " + node.token().getLocation() : ""));
	}

	@SuppressWarnings("unchecked")
	<T> T getFirstEnclosingNode(Class<T> nodeType) {
		for (int i = evaluationStack.size() - 1; i >= 0; i--) {
			if (nodeType.isAssignableFrom(evaluationStack.get(i).getClass())) {
				return (T) evaluationStack.get(i);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	<T> T getParentNode(Class<T> nodeType) {
		if (evaluationStack.size() > 1
				&& nodeType.isAssignableFrom(evaluationStack.get(evaluationStack.size() - 2).getClass())) {
			return (T) evaluationStack.get(evaluationStack.size() - 2);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	<T1, T2> T1 getFirstEnclosingNode(Class<T1> nodeType, Class<T2> parentNodeType) {
		for (int i = evaluationStack.size() - 1; i >= 0; i--) {
			if (nodeType.isAssignableFrom(evaluationStack.get(i).getClass())) {
				if (i > 0 && parentNodeType.isAssignableFrom(evaluationStack.get(i - 1).getClass())) {
					return (T1) evaluationStack.get(i);
				}
			}
		}
		return null;
	}

	void expandPostIncrement(UnaryOperator postIncrement) {
		Assignment a = new Assignment(parser, null, (VariableAccess) postIncrement.operand,
				new BinaryOperator(null, postIncrement.token(), BinaryOperator.Kind.PLUS, postIncrement.operand,
						new NumberLiteral(parser, postIncrement.token(), 1)));
		postIncrement.expanded = true;
		Rule r = getFirstEnclosingNode(Rule.class);
		Statement s = getFirstEnclosingNode(Statement.class, Rule.class);
		r.statements.find(s).insertNext(a);
	}

	void expandPostDecrement(UnaryOperator postDecrement) {
		Assignment a = new Assignment(parser, null, (VariableAccess) postDecrement.operand,
				new BinaryOperator(null, postDecrement.token(), BinaryOperator.Kind.MINUS, postDecrement.operand,
						new NumberLiteral(parser, postDecrement.token(), 1)));
		postDecrement.expanded = true;
		Rule r = getFirstEnclosingNode(Rule.class);
		Statement s = getFirstEnclosingNode(Statement.class, Rule.class);
		r.statements.find(s).insertNext(a);
	}

	public IniEval fork() {
		IniEval forkedEval = new IniEval(this.parser, this.attrib, getRootContext());
		forkedEval.invocationStack.push(new Context(invocationStack.peek()));
		forkedEvals.add(forkedEval);
		return forkedEval;
	}

	public void evalMatches(BinaryOperator matches) {
		Data dataToMatch = eval(matches.left);
		Expression matchExpression = matches.right;
		if (matchExpression instanceof Invocation) {
			Invocation i = (Invocation) matchExpression;
			if (i.name.equals("regexp")) {
				if (dataToMatch.getValue() == null) {
					result = new RawData(false);
					return;
				}
				Pattern p = Pattern.compile(eval(i.arguments.get(0)).getValue().toString());
				Matcher m = p.matcher(dataToMatch.getValue().toString());
				boolean b = m.matches();
				if (b) {
					for (int index = 1; index < i.arguments.size(); index++) {
						if (index <= m.groupCount()) {
							invocationStack.peek().bind(((Variable) i.arguments.get(index)).name,
									new RawData(m.group(index)));
						} else {
							invocationStack.peek().bind(((Variable) i.arguments.get(index)).name, new RawData(null));
						}
					}
				}
				result = new RawData(b);
				return;
			}
		} else if (matchExpression instanceof ConstructorMatchExpression) {
			Data d = new RawData(false);
			result = d;
			ConstructorMatchExpression constructorMatchExpression = (ConstructorMatchExpression) matchExpression;
			if (constructorMatchExpression.type != null) {
				Type typeToMatch = dataToMatch.getRuntimeType(parser.types);
				if (typeToMatch == null) {
					// should not happen...
					d.setValue(false);
					return;
				}
				TypeChecker typeChecker = new TypeChecker(true, parser.types, new TypingConstraint(Kind.EQ, typeToMatch, constructorMatchExpression.type.getType(),
						matchExpression, null));
				List<TypingError> errors = new ArrayList<>();
				typeChecker.unify(error -> errors.add(error));
				
/*				List<TypingConstraint> constraints = new ArrayList<>();
				constraints.add(new TypingConstraint(Kind.EQ, typeToMatch, constructorMatchExpression.type.getType(),
						matchExpression, null));
				while (!constraints.isEmpty() && errors.isEmpty()) {
					List<TypingConstraint> result = new ArrayList<>();
					for (TypingConstraint c : constraints) {
						c.normalize();
						for (TypingConstraint tc : c.reduce(parser.types, errors)) {
							if (!tc.left.equals(tc.right)) {
								result.add(tc);
							}
						}
					}
					constraints = result;
				}*/
				d.setValue(errors.isEmpty());
				return;
			}
			TypeVariable constructor = dataToMatch.getConstructor();
			d.setValue(false);
			if (constructor == null) {
				return;
			}
			if (!constructor.name.equals(constructorMatchExpression.name)) {
				return;
			}
			Context c = new Context(invocationStack.peek());
			if (!dataToMatch
					.isArray()/* && dataToMatch.getReferences() != null */) {
				if (dataToMatch.getReferences() != null) {
					for (Object field : dataToMatch.getReferences().keySet()) {
						c.bind((String) field, dataToMatch.get(field));
					}
				}
				invocationStack.push(c);
				d.setValue(true);
				if (constructorMatchExpression.fieldMatchExpressions != null) {
					for (Expression fe : constructorMatchExpression.fieldMatchExpressions) {
						if (!eval(fe).isTrueOrDefined()) {
							// System.out.println("2: "+fe);
							d.setValue(false);
							break;
						}
					}
				}
				invocationStack.pop();
				result = d;
			} else {
				throw new RuntimeException("constructor '" + constructorMatchExpression.name + "' does not exist");
			}
			/*
			 * System.out.println("===> "+c); System.out.println("===>
			 * "+toMatch.getType()); System.out.println("===> "+result);
			 * System.exit(0);
			 */
			return;
		}
		throw new RuntimeException("invalid match expression");

	}

	public Object invoke(String executableName, Object[] params) {
		Executable executable = getRootContext().get(executableName).getValue();
		if (executable == null) {
			throw new RuntimeException("undefined function " + executableName);
		}
		return invoke(executable, params);
	}

	public Object invoke(Executable executable, Object[] params) {
		if (executable.parameters.size() < params.length) {
			throw new RuntimeException("wrong number of parameters for " + executable.name);
		}
		Context ctx = new Context(executable, null);
		for (int i = 0; i < executable.parameters.size(); i++) {
			if (i > params.length - 1) {
				if (executable.parameters.get(i).defaultValue == null) {
					throw new RuntimeException("no value or default value given for parameter '"
							+ executable.parameters.get(i).name + "' for " + executable.name);
				} else {
					invocationStack.push(ctx);
					ctx.bind(executable.parameters.get(i).name, eval(executable.parameters.get(i).defaultValue));
					invocationStack.pop();
				}
			} else {
				ctx.bind(executable.parameters.get(i).name,
						(params[i] instanceof Data) ? (Data) params[i] : new RawData(params[i]));
			}
		}
		invocationStack.push(ctx);
		executeProcessOrFunction(executable);
		invocationStack.pop();
		// TODO: handle collections

		return result == null ? null : (result.isAvailable() ? result.getValue() : null);
	}

	private Executable lookupExecutable(Invocation invocation) {
		Data d = invocationStack.peek().get(invocation.name);
		if (d == null || !d.isExecutable()) {
			d = getRootContext().get(invocation.name);
		}
		String targetNode = null;
		Executable e = null;
		if (d != null && d.isExecutable()) {
			e = d.getValue();
			if (e instanceof BoundExecutable) {
				e = ((BoundExecutable) e).resolveOverload(attrib, invocation);
				if (!((BoundExecutable) e).binding.isLocal()) {
					targetNode = getTargetNode(((BoundExecutable) e).binding);
				} else {
					return e;
				}
			} else {
				return e;
			}
		}
		if (targetNode == null) {
			if (!parser.env.node.equals(invocation.owner)) {
				targetNode = invocation.owner;
			}
		}
		if (targetNode != null) {
			e = fetchExectuable(targetNode, invocation.name);
		}
		return e;
	}

	private void executeProcessOrFunction(Executable executable) {
		evaluationStack.push(executable);
		executable.eval(this);
		evaluationStack.pop();
	}

	public void printError(PrintStream out, Exception e) {
		printError(out, e, false);
	}

	public void printError(PrintStream out, Exception e, boolean thread) {
		out.println("Error: " + e);

		out.println("Evaluation stack:");
		printEvaluationStackTrace(this.evaluationStack, out);
		out.println("Context:");
		invocationStack.peek().prettyPrint(out);
		if (!thread) {
			if (invocationStack.peek() != getRootContext()) {
				out.println("Root context:");
				getRootContext().prettyPrint(out);
			}
		}
		int i = 1;
		for (IniEval eval : forkedEvals) {
			out.println("==== THREAD #" + i + " ====");
			eval.printError(out, e, true);
			i++;
		}
	}

	@SuppressWarnings("serial")
	public class ReturnException extends RuntimeException {
	}

	@SuppressWarnings("serial")
	public class KilledException extends RuntimeException {
	}

	Number plus(Number n1, Number n2) {
		if (n1 instanceof Byte && n1 instanceof Byte) {
			return n1.byteValue() + n2.byteValue();
		}
		if (n1 instanceof Integer && n1 instanceof Integer) {
			return n1.intValue() + n2.intValue();
		}
		return n1.doubleValue() + n2.doubleValue();
	}

	Number mult(Number n1, Number n2) {
		if (n1 instanceof Byte && n1 instanceof Byte) {
			return n1.byteValue() * n2.byteValue();
		}
		if (n1 instanceof Integer && n1 instanceof Integer) {
			return n1.intValue() * n2.intValue();
		}
		return n1.doubleValue() * n2.doubleValue();
	}

	Number minus(Number n1, Number n2) {
		if (n1 instanceof Byte && n1 instanceof Byte) {
			return n1.byteValue() - n2.byteValue();
		}
		if (n1 instanceof Integer && n1 instanceof Integer) {
			return n1.intValue() - n2.intValue();
		}
		return n1.doubleValue() - n2.doubleValue();
	}

	Number minus(Number n) {
		if (n instanceof Byte) {
			return -n.byteValue();
		}
		if (n instanceof Integer) {
			return -n.intValue();
		}
		return -n.doubleValue();
	}

	final private Executable fetchExectuable(String node, String executableName) {
		if (parser.env.coreBrokerClient == null) {
			throw new EvalException(this, "cannot fetch missing executable '" + executableName + "' from node '" + node
					+ "' - no core broker initialized");
		}
		Executable result = null;
		parser.env.coreBrokerClient.sendFetchRequest(node, new FetchRequest(parser.env.node, executableName));
		Main.LOGGER.debug("waiting for executable '" + executableName + "' to be deployed...");
		do {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				Main.LOGGER.error("failed", e);
			}
			result = getRootContext().get(executableName) == null ? null
					: getRootContext().get(executableName).getValue();
		} while (result == null
				|| (result instanceof BoundExecutable && !((BoundExecutable) result).binding.isLocal()));
		Main.LOGGER.debug("fetched: " + result);
		return result;
	}

	final private void spawnExecutable(Invocation invocation, Executable executable, String targetNode) {
		List<Data> arguments = new ArrayList<>();
		Data argument = null;
		for (int i = 0; i < executable.parameters.size(); i++) {
			if (i > invocation.arguments.size() - 1) {
				if (executable.parameters.get(i).defaultValue == null) {
					throw new RuntimeException("no value or default value given for parameter '"
							+ executable.parameters.get(i).name + "' at " + parser.env.node);
				} else {
					argument = eval(executable.parameters.get(i).defaultValue);
				}
			} else {
				argument = eval(invocation.arguments.get(i));
			}
			arguments.add(argument);
		}
		Main.LOGGER.debug("spawn request to " + targetNode + " / " + executable.name + " - " + arguments);
		// TODO: RESULT???
		parser.env.coreBrokerClient.sendSpawnRequest(targetNode,
				new SpawnRequest(parser.env.node, executable, arguments));
	}

	public void evalCode(AstAttrib attrib, String code) {
		IniParser parser = null;
		try {
			parser = IniParser.createParserForCode(this.parser.env, this.parser, code);
			parser.parse();
		} catch (Exception e) {
			if (parser.hasErrors()) {
				parser.printErrors(System.err);
				return;
			}
		}

		try {
			attrib.rollback();
			attrib.attrib(parser);
			attrib.unify();
		} catch (Exception e) {
			if (attrib != null)
				attrib.printError(System.err, e);
			System.err.println("Java stack:");
			e.printStackTrace(System.err);
			return;
		} finally {
			if (attrib != null && attrib.hasErrors()) {
				attrib.printErrors(System.err);
				return;
			}
		}

		for (AstNode node : parser.topLevels) {
			eval(node);
		}
	}

	public void evalCode(AstAttrib attrib, AstNode node) {
		try {
			attrib.rollback();
			attrib.attrib(node);
			attrib.unify();
		} catch (Exception e) {
			if (attrib != null)
				attrib.printError(System.err, e);
			System.err.println("Java stack:");
			e.printStackTrace(System.err);
			return;
		} finally {
			if (attrib != null && attrib.hasErrors()) {
				attrib.printErrors(System.err);
				return;
			}
		}

		eval(node);
	}

	/*
	 * public void evalNode2(AstAttrib attrib, AstNode node) throws Exception {
	 * IniParser parser = new IniParser(); parser.env = this.parser.env;
	 * parser.types = this.parser.types; parser.builtInExecutables =
	 * this.parser.builtInExecutables; parser.topLevels.add(node);
	 * evalParsedCode(attrib, parser); }
	 * 
	 * public void evalParsedCode2(AstAttrib attrib, IniParser
	 * parserContainingCode) throws Exception { if
	 * (parserContainingCode.hasErrors()) {
	 * parserContainingCode.printErrors(System.err); return; }
	 * 
	 * try { attrib.rollback(); attrib.attrib(parserContainingCode);
	 * attrib.unify(); } catch (Exception e) { if (attrib != null)
	 * attrib.printError(System.err, e); System.err.println("Java stack:");
	 * e.printStackTrace(System.err); return; } finally { if (attrib != null &&
	 * attrib.hasErrors()) { attrib.printErrors(System.err); return; } }
	 * 
	 * for (AstNode node : parserContainingCode.topLevels) { eval(node); } }
	 */

}
