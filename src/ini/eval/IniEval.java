package ini.eval;

import ini.ast.ArrayAccess;
import ini.ast.Assignment;
import ini.ast.AstNode;
import ini.ast.BinaryOperator;
import ini.ast.BooleanLiteral;
import ini.ast.CaseStatement;
import ini.ast.CharLiteral;
import ini.ast.Constructor;
import ini.ast.ConstructorMatchExpression;
import ini.ast.Expression;
import ini.ast.Field;
import ini.ast.FieldAccess;
import ini.ast.Function;
import ini.ast.FunctionLiteral;
import ini.ast.Invocation;
import ini.ast.ListExpression;
import ini.ast.NumberLiteral;
import ini.ast.ReturnStatement;
import ini.ast.Rule;
import ini.ast.Sequence;
import ini.ast.SetConstructor;
import ini.ast.SetDeclaration;
import ini.ast.SetExpression;
import ini.ast.Statement;
import ini.ast.StringLiteral;
import ini.ast.SubArrayAccess;
import ini.ast.UnaryOperator;
import ini.ast.Variable;
import ini.ast.VariableAccess;
import ini.eval.at.At;
import ini.eval.data.Data;
import ini.eval.data.DataReference;
import ini.eval.data.RawData;
import ini.eval.function.IniFunction;
import ini.parser.IniParser;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.martiansoftware.jsap.JSAPResult;

public class IniEval {

	public IniParser parser;
	public Stack<Context> invocationStack = new Stack<Context>();
	public Stack<AstNode> evaluationStack = new Stack<AstNode>();
	public Data result;
	public boolean error = false;
	JSAPResult config;
	public List<IniEval> forkedEvals = new ArrayList<IniEval>();
	boolean rulePassed = false;
	public boolean kill = false;

	public IniEval(IniParser parser, Context context, JSAPResult config) {
		this.parser = parser;
		this.invocationStack.push(context);
		this.config = config;
	}

	@SuppressWarnings("unchecked")
	public Data eval(AstNode node) {
		if (kill) {
			throw new KilledException();
		}
		Function f;
		Data d;

		try {
			evaluationStack.push(node);

			switch (node.nodeTypeId()) {

			case AstNode.ARRAY_ACCESS:
				result = eval(((ArrayAccess) node).variableAccess).get(
						eval(((ArrayAccess) node).indexExpression).getValue());
				break;

			case AstNode.ASSIGNMENT:
				d = eval(((Assignment) node).assignee);
				if (d instanceof DataReference) {
					Data d2 = eval(((Assignment) node).assignment);
					if (d2.isPrimitive()) {
						d.copyData(d2);
					} else {
						((DataReference) d).setReferencedData(d2);
					}
				} else {
					d.copyData(eval(((Assignment) node).assignment));
				}
				result = d;
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
					result = new RawData(eval(b.left).getNumber().doubleValue()
							/ eval(b.right).getNumber().doubleValue());
					break;
				case MULT:
					result = new RawData(mult(eval(b.left).getNumber(),
							eval(b.right).getNumber()));
					break;
				case EQUALS:
					result = new RawData(eval(b.left).equals(eval(b.right)));
					break;
				case NOTEQUALS:
					try {
						result = new RawData(!eval(b.left).getValue().equals(
								eval(b.right).getValue()));
					} catch (NullPointerException e) {
						result = new RawData(true);
					}
					break;
				case GT:
					try {
						result = new RawData(((Comparable) eval(b.left)
								.getValue()).compareTo(((Comparable) eval(
								b.right).getValue())) > 0);
					} catch (NullPointerException e) {
						result = new RawData(false);
					}
					break;
				case GTE:
					try {
						result = new RawData(((Comparable) eval(b.left)
								.getValue()).compareTo(((Comparable) eval(
								b.right).getValue())) >= 0);
					} catch (NullPointerException e) {
						result = new RawData(false);
					}
					break;
				case LT:
					try {
						result = new RawData(((Comparable) eval(b.left)
								.getValue()).compareTo(((Comparable) eval(
								b.right).getValue())) < 0);
					} catch (NullPointerException e) {
						result = new RawData(false);
					}
					break;
				case LTE:
					try {
						result = new RawData(((Comparable) eval(b.left)
								.getValue()).compareTo(((Comparable) eval(
								b.right).getValue())) <= 0);
					} catch (NullPointerException e) {
						result = new RawData(false);
					}
					break;
				case MINUS:
					result = new RawData(minus(eval(b.left).getNumber(),
							eval(b.right).getNumber()));
					break;
				case PLUS:
					d = eval(b.left);
					Object o = d.getValue();
					if (o instanceof String) {
						result = new RawData((String) o
								+ eval(b.right).toPrettyString());
					} else {
						result = new RawData(plus(eval(b.left).getNumber(),
								eval(b.right).getNumber()));
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
				result.setConstructor(parser.ast
						.getOrCreatePrimitive("Boolean"));
				break;

			case AstNode.CHAR_LITERAL:
				result = new RawData(((CharLiteral) node).value);
				result.setConstructor(parser.ast.getOrCreatePrimitive("Char"));
				break;

			case AstNode.FIELD_ACCESS:
				result = eval(((FieldAccess) node).variableAccess).get(
						((FieldAccess) node).fieldName);
				break;

			case AstNode.FUNCTION:
				f = (Function) node;
				try {
					for (Rule rule : f.initRules) {
						eval(rule);
					}
					List<At> ats = null;
					if (!f.atRules.isEmpty()) {
						ats = new ArrayList<At>();
					}
					Map<Rule, At> atMap = new HashMap<Rule, At>();
					for (Rule rule : f.atRules) {
						// At at = At.atPredicates.get(rule.atPredicate.name);
						Class<? extends At> c = At.atPredicates
								.get(rule.atPredicate.name);
						At at = null;
						try {
							at = c.newInstance();
							at.setRule(rule);
							at.setAtPredicate(rule.atPredicate);
							ats.add(at);
							if (rule.atPredicate.identifier != null) {
								invocationStack.peek().bind(
										rule.atPredicate.identifier,
										new RawData(at));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (at == null) {
							throw new RuntimeException("unknown @ predicate '"
									+ rule.atPredicate.name + "'");
						}
						atMap.put(rule, at);
					}
					Iterator<Rule> itr = atMap.keySet().iterator();
					while (itr.hasNext()) {
						Rule evalRule = itr.next();
						At evalAt = atMap.get(evalRule);
						List<Expression> synchronizedAtsNames = evalRule.synchronizedAtsNames;
						if(synchronizedAtsNames != null) {
							for (Expression e : synchronizedAtsNames) {
								evalAt.synchronizedAts.add((At) this.eval(e)
										.getValue());
							}	
						}
						
						evaluationStack.push(evalRule.atPredicate);
						evalAt.parseInParameters(this,evalRule.atPredicate.inParameters);
						evalAt.eval(this);
						evaluationStack.pop();
					}
					do {
						invocationStack.peek().noRulesApplied = false;
						while (!invocationStack.peek().noRulesApplied) {
							invocationStack.peek().noRulesApplied = true;
							for (Rule rule : f.rules) {
								eval(rule);
							}
						}
					} while (!At.checkAllTerminated(ats));
					At.destroyAll(ats);
					for (Rule rule : f.endRules) {
						eval(rule);
					}
				} catch (ReturnException e) {
					// swallow
				} catch (RuntimeException e) {
					boolean caught = false;
					for (Rule rule : f.errorRules) {
						if (rule.guard == null
								|| eval(rule.guard).isTrueOrDefined()) {
							invocationStack
									.peek()
									.bind(((Variable) rule.atPredicate.inParameters
											.get(0)).name, new RawData(e));
							Sequence<Statement> s = rule.statements;
							while (s != null) {
								eval(s.get());
								s = s.next();
							}
							caught = true;
						}
					}
					if (!caught) {
						throw e;
					}
				}
				break;

			case AstNode.FUNCTION_LITERAL:
				if (!parser.parsedFunctionMap
						.containsKey(((FunctionLiteral) node).name)) {
					throw new RuntimeException("'"
							+ ((FunctionLiteral) node).name
							+ "' is not a declared function");
				}
				result = new RawData(((FunctionLiteral) node).name);
				break;

			case AstNode.INVOCATION:
				Invocation invocation = (Invocation) node;
				// try built-in INI functions first
				if (IniFunction.functions.containsKey(invocation.name)) {
					result = IniFunction.functions.get(invocation.name).eval(
							this, invocation.arguments);
				} else {
					f = parser.parsedFunctionMap.get(invocation.name);
					if (f == null) {
						throw new RuntimeException("undefined function at "
								+ node);
					}
					if (f.parameters.size() < invocation.arguments.size()) {
						throw new RuntimeException(
								"wrong number of parameters at " + node);
					}
					Context ctx = new Context(f);
					for (int i = 0; i < f.parameters.size(); i++) {
						if (i > invocation.arguments.size() - 1) {
							if (f.parameters.get(i).defaultValue == null) {
								throw new RuntimeException(
										"no value or default value given for parameter '"
												+ f.parameters.get(i).name
												+ "' at " + node);
							} else {
								invocationStack.push(ctx);
								ctx.bind(f.parameters.get(i).name,
										eval(f.parameters.get(i).defaultValue));
								invocationStack.pop();
							}
						} else {
							ctx.bind(f.parameters.get(i).name,
									eval(invocation.arguments.get(i)));
						}
					}
					invocationStack.push(ctx);
					if(f.isProcess()) {
						IniEval child = fork();
						new Thread(new Runnable() {
							@Override
							public void run() {
								child.eval(f);
							}
						}).start();
					} else {
						eval(f);
					}
					invocationStack.pop();
				}
				break;

			case AstNode.LIST_EXPRESSION:
				int i = 0;
				d = new RawData(null);
				d.setKind(Data.Kind.INT_SET);
				for (Expression e : ((ListExpression) node).elements) {
					d.set(i++, eval(e));
				}
				result = d;
				break;

			case AstNode.NUMBER_LITERAL:
				result = new RawData(((NumberLiteral) node).value);
				if (((NumberLiteral) node).value instanceof Integer) {
					result.setConstructor(parser.ast
							.getOrCreatePrimitive("Int"));
				} else if (((NumberLiteral) node).value instanceof Float) {
					result.setConstructor(parser.ast
							.getOrCreatePrimitive("Float"));
				} else if (((NumberLiteral) node).value instanceof Double) {
					result.setConstructor(parser.ast
							.getOrCreatePrimitive("Float"));
				} else if (((NumberLiteral) node).value instanceof Byte) {
					result.setConstructor(parser.ast
							.getOrCreatePrimitive("Byte"));
				} else if (((NumberLiteral) node).value instanceof Long) {
					result.setConstructor(parser.ast
							.getOrCreatePrimitive("Long"));
				}
				break;

			case AstNode.RETURN_STATEMENT:
				if (((ReturnStatement) node).expression != null) {
					result = eval(((ReturnStatement) node).expression);
				} else {
					result = null;
				}
				throw new ReturnException();

			case AstNode.CASE_STATEMENT:
				List<Rule> caseRules = ((CaseStatement) node).cases;
				Rule matchedRule = null;
				for (Rule rule : caseRules) {
					if (((Rule) rule).guard != null
							&& eval(((Rule) rule).guard).isTrueOrDefined()) {
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
				if (((Rule) node).guard == null
						|| eval(((Rule) node).guard).isTrueOrDefined()) {
					invocationStack.peek().noRulesApplied = false;
					Sequence<Statement> s = ((Rule) node).statements;
					while (s != null) {
						eval(s.get());
						s = s.next();
					}
				}
				break;

			case AstNode.SET_CONSTRUCTOR:
				String constructorName = ((SetConstructor) node).name;
				d = new RawData(null);
				for (Assignment a : ((SetConstructor) node).fieldAssignments) {
					d.set(((Variable) a.assignee).name, eval(a.assignment));
				}
				if (constructorName != null) {
					Data set = invocationStack.peek().getOrCreate(
							constructorName);
					d.setConstructor(parser.ast
							.getFirstLevelConstructor(constructorName));
					set.set(d, d);
				}
				result = d;
				break;

			case AstNode.SET_DECLARATION:
				d = new RawData(null);
				d.setKind(Data.Kind.INT_SET);
				d.set(Data.LOWER_BOUND_KEY,
						eval(((SetDeclaration) node).lowerBound));
				d.set(Data.UPPER_BOUND_KEY,
						eval(((SetDeclaration) node).upperBound));
				if (d.get(Data.UPPER_BOUND_KEY).getNumber().intValue() < d
						.get(Data.LOWER_BOUND_KEY).getNumber().intValue()) {
					throw new RuntimeException("invalid set bounds");
				}
				result = d;
				break;

			case AstNode.SET_EXPRESSION:
				SetIterator it = new SetIterator(invocationStack.peek(),
						(SetExpression) node, eval(((SetExpression) node).set));
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
				d = new RawData(((StringLiteral) node).value);
				d.setKind(Data.Kind.INT_SET);
				result = d;
				result.setConstructor(parser.ast.getOrCreatePrimitive("String"));
				break;

			case AstNode.SUB_ARRAY_ACCESS:
				SubArrayAccess sub = (SubArrayAccess) node;
				d = eval(sub.variableAccess);
				result = d.subArray((Integer) eval(sub.minExpression)
						.getValue(), (Integer) eval(sub.maxExpression)
						.getValue());
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
				result = invocationStack.peek().getOrCreate(
						((Variable) node).name);
				break;

			default:
				throw new RuntimeException("unsuported syntax node: " + node);

			}

			evaluationStack.pop();

		} catch (ReturnException e) {
			evaluationStack.pop();
			throw e;
		} catch(Exception e) {
			//printNode(System.err, evaluationStack.peek());
			//System.err.println();
			//printInvocationStackTrace(System.err);
			throw new RuntimeException(evaluationStack.peek().toString(), e);
		}
		return result;
	}

	public void printEvaluationStackTrace(PrintStream out) {
		for (int i = evaluationStack.size() - 1; i >= 0; i--) {
			AstNode node = evaluationStack.get(i);
			out.print("    ");
			printNode(out, node);
			out.println();
		}
	}

	public void printInvocationStackTrace(PrintStream out) {
		for (int i = invocationStack.size() - 1; i >= 0; i--) {
			Context context = invocationStack.get(i);
			out.print("    ");
			out.print(context.toString());
			out.println();
		}
	}
	
	public void printNode(PrintStream out, AstNode node) {
		out.print("'"
				+ node
				+ "'"
				+ (node != null && node.token() != null ? " at "
						+ node.token().getLocation() : ""));
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
	<T1, T2> T1 getFirstEnclosingNode(Class<T1> nodeType,
			Class<T2> parentNodeType) {
		for (int i = evaluationStack.size() - 1; i >= 0; i--) {
			if (nodeType.isAssignableFrom(evaluationStack.get(i).getClass())) {
				if (i > 0
						&& parentNodeType.isAssignableFrom(evaluationStack.get(
								i - 1).getClass())) {
					return (T1) evaluationStack.get(i);
				}
			}
		}
		return null;
	}

	void expandPostIncrement(UnaryOperator postIncrement) {
		Assignment a = new Assignment(parser, null,
				(VariableAccess) postIncrement.operand, new BinaryOperator(
						null, postIncrement.token(), BinaryOperator.Kind.PLUS,
						postIncrement.operand, new NumberLiteral(parser,
								postIncrement.token(), 1)));
		postIncrement.expanded = true;
		Rule r = getFirstEnclosingNode(Rule.class);
		Statement s = getFirstEnclosingNode(Statement.class, Rule.class);
		r.statements.find(s).insertNext(a);
	}

	void expandPostDecrement(UnaryOperator postDecrement) {
		Assignment a = new Assignment(parser, null,
				(VariableAccess) postDecrement.operand, new BinaryOperator(
						null, postDecrement.token(), BinaryOperator.Kind.MINUS,
						postDecrement.operand, new NumberLiteral(parser,
								postDecrement.token(), 1)));
		postDecrement.expanded = true;
		Rule r = getFirstEnclosingNode(Rule.class);
		Statement s = getFirstEnclosingNode(Statement.class, Rule.class);
		r.statements.find(s).insertNext(a);
	}

	public IniEval fork() {
		IniEval forkedEval = new IniEval(this.parser, new Context(
				invocationStack.peek()), this.config);
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
				Pattern p = Pattern.compile(eval(i.arguments.get(0)).getValue()
						.toString());
				Matcher m = p.matcher(dataToMatch.getValue().toString());
				boolean b = m.matches();
				if (b) {
					for (int index = 1; index < i.arguments.size(); index++) {
						if (index <= m.groupCount()) {
							invocationStack.peek().bind(
									((Variable) i.arguments.get(index)).name,
									new RawData(m.group(index)));
						} else {
							invocationStack.peek().bind(
									((Variable) i.arguments.get(index)).name,
									new RawData(null));
						}
					}
				}
				result = new RawData(b);
				return;
			}
		} else if (matchExpression instanceof ConstructorMatchExpression) {
			ConstructorMatchExpression e = (ConstructorMatchExpression) matchExpression;
			Constructor toMatch = dataToMatch.getConstructor();
			Constructor constructor = null;
			if (e.name != null) {
				constructor = parser.ast.getFirstLevelConstructor(e.name);
			}
			if (toMatch == null) {
				result = new RawData(false);
				return;
			}
			if (constructor == null) {
				throw new RuntimeException("type '" + e.name
						+ "' does not exist");
			}
			if (toMatch != null && constructor != null
					&& constructor != toMatch) {
				result = new RawData(false);
				return;
			}
			result = new RawData(false);
			Context c = new Context(invocationStack.peek());
			if (toMatch.fields != null) {
				for (Field f : toMatch.fields) {
					c.bind(f.name, dataToMatch.get(f.name));
				}
				invocationStack.push(c);
				result.setValue(true);
				for (Expression fe : e.fieldMatchExpressions) {
					if (!eval(fe).isTrueOrDefined()) {
						result.setValue(false);
						break;
					}
				}
				invocationStack.pop();
			} else {
				throw new RuntimeException("constructor '" + e.name
						+ "' does not exist");
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

	public Object invoke(String function, Object... params) {
		Function f = parser.parsedFunctionMap.get(function);
		if (f == null) {
			throw new RuntimeException("undefined function " + function);
		}
		if (f.parameters.size() < params.length) {
			throw new RuntimeException("wrong number of parameters for "
					+ function);
		}
		Context ctx = new Context(f);
		for (int i = 0; i < f.parameters.size(); i++) {
			if (i > params.length - 1) {
				if (f.parameters.get(i).defaultValue == null) {
					throw new RuntimeException(
							"no value or default value given for parameter '"
									+ f.parameters.get(i).name + "' for "
									+ function);
				} else {
					invocationStack.push(ctx);
					ctx.bind(f.parameters.get(i).name,
							eval(f.parameters.get(i).defaultValue));
					invocationStack.pop();
				}
			} else {
				ctx.bind(f.parameters.get(i).name, new RawData(params[i]));
			}
		}
		invocationStack.push(ctx);
		eval(f);
		invocationStack.pop();
		// TODO: handle collections
		return result == null ? null : result.getValue();
	}

	public void printError(PrintStream out, Exception e) {
		out.println("Error: " + e);
		out.println("Evaluation stack:");
		printEvaluationStackTrace(out);
		out.println("Context:");
		invocationStack.peek().prettyPrint(out);
		int i = 1;
		for (IniEval eval : forkedEvals) {
			out.println("THREAD #" + i);
			eval.printError(out, e);
			i++;
		}
	}

	@SuppressWarnings("serial")
	class ReturnException extends RuntimeException {
	}

	@SuppressWarnings("serial")
	class KilledException extends RuntimeException {
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

}
