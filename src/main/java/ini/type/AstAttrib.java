package ini.type;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Stack;

import ini.ast.ArrayAccess;
import ini.ast.Assignment;
import ini.ast.AstNode;
import ini.ast.BinaryOperator;
import ini.ast.Binding;
import ini.ast.CaseStatement;
import ini.ast.Constructor;
import ini.ast.ConstructorMatchExpression;
import ini.ast.Executable;
import ini.ast.Expression;
import ini.ast.Field;
import ini.ast.FieldAccess;
import ini.ast.Function;
import ini.ast.Import;
import ini.ast.Invocation;
import ini.ast.ListExpression;
import ini.ast.NamedElement;
import ini.ast.NumberLiteral;
import ini.ast.Process;
import ini.ast.ReturnStatement;
import ini.ast.Rule;
import ini.ast.Sequence;
import ini.ast.SetConstructor;
import ini.ast.SetDeclaration;
import ini.ast.SetExpression;
import ini.ast.Statement;
import ini.ast.SubArrayAccess;
import ini.ast.TypeVariable;
import ini.ast.UnaryOperator;
import ini.ast.UserType;
import ini.ast.Variable;
import ini.parser.IniParser;
import ini.type.TypingConstraint.Kind;

public class AstAttrib {

	public IniParser parser;
	public Stack<AttrContext> invocationStack = new Stack<AttrContext>();
	public Stack<AstNode> evaluationStack = new Stack<AstNode>();
	public Type result;
	public boolean forceVariableDeclaration = false;
	boolean hadReturnStatement = false;

	List<Function> attributedFunctions = new ArrayList<Function>();

	private List<TypingConstraint> constraints = new ArrayList<>();
	public List<TypingError> errors = new ArrayList<TypingError>();

	public AstAttrib(IniParser parser) {
		this.parser = parser;
		AttrContext rootContext = new AttrContext(parser.types, (Type) null);
		for (Executable e : parser.builtInExecutables) {
			Type t = parser.types.createType();
			t.executable = e;
			rootContext.bind(e.name, t);
		}
		invocationStack.push(rootContext);
	}

	public void addTypingConstraint(TypingConstraint.Kind kind, Type leftType, Type rightType, AstNode leftOrigin,
			AstNode rightOrigin) {
		if (leftType == parser.types.ANY || rightType == parser.types.ANY) {
			return;
		}
		constraints.add(new TypingConstraint(kind, leftType, rightType, leftOrigin, rightOrigin));
	}

	/*
	 * public void invoke(Executable executable) {
	 * evaluationStack.push(executable); invocationStack.push(new
	 * AttrContext(parser.types, executable));
	 * executable.getFunctionalType(this); //
	 * addTypingConstraint(TypingConstraint.Kind.EQ, //
	 * getOrCreateNodeTypeVariable(f), f.getFunctionType(), f));
	 * execute(executable); invocationStack.pop(); evaluationStack.pop(); }
	 */

	private Type lookup(NamedElement element) {
		if (invocationStack.peek().hasBinding(element.name)) {
			return invocationStack.peek().get(element.name);
		} else {
			if (getRootContext().hasBinding(element.name)) {
				return getRootContext().get(element.name);
			} else {
				addError(new TypingError(element, "undefined symbol '" + element.name + "'"));
				return null;
			}
		}
	}

	private AttrContext getRootContext() {
		return invocationStack.get(0);
	}

	public void invoke(Executable executable, Type executableType) {
		if (executable == null) {
			throw new RuntimeException("cannot invoke null exutable");
		}
		invocationStack.push(new AttrContext(parser.types, executableType));
		evaluationStack.push(executable);
		evalExecutable(executable, executableType);
		evaluationStack.pop();
		invocationStack.pop();
	}

	private void evalExecutable(Executable executable, Type executableType) {
		if (executable instanceof Process) {
			evalProcess((Process) executable, executableType);
		} else if (executable instanceof Function) {
			evalFunction((Function) executable, executableType);
		} else {
			// external executable
		}
	}

	private void evalProcess(Process process, Type executableType) {
		// if (process.functionType == null) {
		// invoke(process);
		// }

		hadReturnStatement = false;
		//Type typeVar = process.getType();

		for (int i = 0; i < process.parameters.size(); i++) {
			// handle default values?
			invocationStack.peek().bind(process.parameters.get(i).name, executableType.getTypeParameters().get(i));
		}

		for (Rule rule : process.initRules) {
			eval(rule);
		}

		for (Rule rule : process.atRules) {
			eval(rule);
		}

		for (Rule rule : process.rules) {
			eval(rule);
		}

		for (Rule rule : process.endRules) {
			eval(rule);
		}

		if (!hadReturnStatement) {
			addTypingConstraint(TypingConstraint.Kind.EQ, executableType.getReturnType(), parser.types.VOID, process, process);
		}

		// result = typeVar;
		// evaluationStack.pop();

	}

	private void evalFunction(Function function, Type executableType) {
		// evaluationStack.push(function);
		// if (function.functionType == null) {
		// invoke(function);
		// }

		hadReturnStatement = false;

		for (int i = 0; i < function.parameters.size(); i++) {
			// handle default values?
			invocationStack.peek().bind(function.parameters.get(i).name, executableType.getTypeParameters().get(i));
		}

		Sequence<Statement> s = ((Function) function).statements;
		while (s != null) {
			eval(s.get());
			s = s.next();
		}

		if (!hadReturnStatement) {
			addTypingConstraint(TypingConstraint.Kind.EQ, executableType.getReturnType(), parser.types.VOID, function,
					function);
		}

		// result = typeVar;
		// evaluationStack.pop();
	}

	// @SuppressWarnings("unchecked")
	public Type eval(AstNode node) {
		Executable executable;
		Sequence<Statement> s;
		result = null;
		Type t1 = null;
		Type t2 = null;
		Type typeVar = null;

		evaluationStack.push(node);

		if (node == null) {
			System.out.println("");
		}

		switch (node.nodeTypeId()) {

		case AstNode.IMPORT:
			for (AstNode n : ((Import) node).importParser.topLevels) {
				result = eval(n);
			}
			break;

		case AstNode.USER_TYPE:
			// TODO: should register and constructs types in parser.types
			// (instead of implicit registration)
			// IGNORE FOR NOW
			break;

		case AstNode.BINDING:
			// TODO: register here?
			Binding binding = ((Binding) node);
			result = binding.getType();
			// TODO: change
			if (binding.typeParameters != null) {
				for (TypeVariable tv : binding.typeParameters) {
					if (tv.superType != null) {
						// add constraints for supertypes
						addTypingConstraint(Kind.LTE, tv.getType(), tv.getType().superType, tv, tv.superType);
					}
				}
			}
			getRootContext().bind(binding.name, result);
			break;

		case AstNode.ARRAY_ACCESS:
			t1 = eval(((ArrayAccess) node).variableAccess);
			t2 = eval(((ArrayAccess) node).indexExpression);
			if (!t1.isVariable() && !t1.isMap()) {
				addError(new TypingError(node, "invalid type for map access"));
				result = parser.types.createType();
				break;
			}
			if (!t1.isVariable() && !t2.isVariable()) {
				if (t1.getTypeParameters().get(0) != t2) {
					addError(new TypingError(node, "incompatible type for map access"));
				}
				result = t1.getTypeParameters().get(1);
				break;
			}
			if (t1.isVariable()) {
				Type map = parser.types.createType("Map");
				map.addTypeParameter(t2);
				Type val = parser.types.createType();
				map.addTypeParameter(val);
				addTypingConstraint(TypingConstraint.Kind.EQ, t1, map, ((ArrayAccess) node).variableAccess, node);
				result = val;
				break;
			}
			throw new RuntimeException("Should not happen");
			// break;

		case AstNode.ASSIGNMENT:
			t1 = eval(((Assignment) node).assignee);
			t2 = eval(((Assignment) node).assignment);

			// if (t2 == parser.ast.VOID) { addError(new TypingError(node,
			// "cannot
			// assign void")); }
			//
			// if (t1.isVariable() || t2.isVariable()) { constraints.add(new
			// TypingConstraint(TypingConstraint.Kind.EQ, t1, t2, ((Assignment)
			// node).assignee, ((Assignment) node).assignment)); }

			if (t2 != null && (!t2.isVariable() && !t2.hasFields() || t2.isFunctional())) {
				addTypingConstraint(TypingConstraint.Kind.EQ, t1, t2, ((Assignment) node).assignee,
						((Assignment) node).assignment);
			} else {
				addTypingConstraint(TypingConstraint.Kind.LTE, t2, t1, ((Assignment) node).assignee,
						((Assignment) node).assignment);
			}

			// if (!t1.isVariable() && !t2.isVariable() && t1 != t2) {
			// addError(new TypingError(node, "incompatible types"));
			// }
			break;

		case AstNode.BINARY_OPERATOR:
			BinaryOperator b = (BinaryOperator) node;

			t1 = eval(b.left);
			t2 = eval(b.right);

			switch (b.kind) {
			case AND:
			case OR:
				result = parser.types.BOOLEAN;
				break;
			case DIV:
				addTypingConstraint(TypingConstraint.Kind.LTE, t1, parser.types.DOUBLE, b, b);
				addTypingConstraint(TypingConstraint.Kind.EQ, t1, t2, b.left, b.right);
				result = parser.types.FLOAT;
				break;
			case MULT:
			case MINUS:
				result = parser.types.createType();
				addTypingConstraint(TypingConstraint.Kind.EQ, t1, t2, b.left, b.right);
				addTypingConstraint(TypingConstraint.Kind.LTE, t1, parser.types.DOUBLE, b, b);
				addTypingConstraint(TypingConstraint.Kind.EQ, result, t1, b, b);
				break;
			case EQUALS:
			case NOTEQUALS:
			case GT:
			case GTE:
			case LT:
			case LTE:
				addTypingConstraint(TypingConstraint.Kind.EQ, t1, t2, b.left, b.right);
				result = parser.types.BOOLEAN;
				break;
			case PLUS:
				if (t1 == parser.types.STRING || t2 == parser.types.STRING) {
					result = parser.types.STRING;
				} else {
					result = parser.types.createType();
					addTypingConstraint(TypingConstraint.Kind.EQ, t1, t2, b.left, b.right);
					addTypingConstraint(TypingConstraint.Kind.LTE, t1, parser.types.DOUBLE, b, b);
					addTypingConstraint(TypingConstraint.Kind.EQ, result, t1, b, b);
				}
				break;
			case MATCHES:

				/*
				 * if (!(b.left instanceof Variable)) { addError(new
				 * TypingError(b, "match operator shall always apply to a
				 * variable")); break; }
				 */

				if (b.left instanceof Variable) {
					Variable v = (Variable) b.left;
					invocationStack.peek().bind(v.name, t2);
				}

				break;
			case CONCAT:
				result = parser.types.createType();
				addTypingConstraint(TypingConstraint.Kind.EQ, t1, t2, b.left, b.right);
				addTypingConstraint(TypingConstraint.Kind.EQ, result, t1, b, b);
				break;
			default:
				throw new RuntimeException("unsuported operator: " + b);
			}
			break;

		case AstNode.BOOLEAN_LITERAL:
			result = parser.types.BOOLEAN;
			break;

		case AstNode.CHAR_LITERAL:
			result = parser.types.CHAR;
			break;

		case AstNode.CONSTRUCTOR_MATCH_EXPRESSION:
			Constructor constr = parser.types.getConstructor(((ConstructorMatchExpression) node).name);
			if (constr == null) {
				addError(new TypingError(node,
						"undeclared constructor '" + ((ConstructorMatchExpression) node).name + "'"));
				result = parser.types.createType();
				break;
			}
			if (constr.fields != null && !constr.fields.isEmpty()) {
				AttrContext context = new AttrContext(invocationStack.peek());
				for (Field field : constr.fields) {
					context.bind(field.name, field.constructor.type);
				}
				forceVariableDeclaration = true;
				invocationStack.push(context);
				for (Expression e : ((ConstructorMatchExpression) node).fieldMatchExpressions) {
					eval(e);
				}
				invocationStack.pop();
				forceVariableDeclaration = false;
			}
			result = constr.type;
			// printConstraints(System.out);
			break;

		case AstNode.FIELD_ACCESS:

			t1 = eval(((FieldAccess) node).variableAccess);

			if (t1.hasFields()) {
				t2 = t1.fields.get(((FieldAccess) node).fieldName);
			}

			if (t2 == null) {
				t2 = parser.types.createType();
				if (t1.isVariable()) {
					t1.addField(((FieldAccess) node).fieldName, t2);
				} else {
					addError(new TypingError(node, "undeclared field '" + ((FieldAccess) node).fieldName + "'"));
				}
			}
			result = t2;

			// typeVar = new Type();
			// typeVar.addField(((FieldAccess) node).fieldName, result);
			//
			//
			// addTypingConstraint(TypingConstraint.Kind.EQ,
			// t1, typeVar, ((FieldAccess) node).variableAccess, node));

			break;

		case AstNode.FUNCTION:
		case AstNode.PROCESS:

			result = parser.types.createType();
			result.executable = (Executable) node;
			if (((NamedElement) node).name != null) {
				getRootContext().bind(((NamedElement) node).name, result);
			}
			break;

		case AstNode.INVOCATION:
			Invocation invocation = (Invocation) node;
			if (invocation.name.equals("regexp")) {
				for (Expression e : invocation.arguments) {
					result = eval(e);
					addTypingConstraint(TypingConstraint.Kind.EQ, result, parser.types.STRING, e, e);
				}
				result = null;
				break;
			}
			typeVar = lookup(invocation);

			if (typeVar != null) {

				executable = typeVar.executable;
				typeVar = executable.getFunctionalType(this);
				
				// TODO: check number of arguments against default values
				if (invocation.arguments.size() > typeVar.getTypeParameters().size()) {
					addError(new TypingError(invocation, "wrong number of arguments"));
				}

				for (int i = 0; i < invocation.arguments.size(); i++) {
					if (i < typeVar.getTypeParameters().size()) {
						addTypingConstraint(TypingConstraint.Kind.EQ, typeVar.getTypeParameters().get(i),
								eval(invocation.arguments.get(i)), invocation, invocation);
					}
				}
				invoke(executable, typeVar);
				result = typeVar.getReturnType();
			}
			// System.out.println("====> " + typeVar);
			break;

		case AstNode.LIST_EXPRESSION:
			List<Type> types = new ArrayList<Type>();
			t1 = null;
			for (Expression e : ((ListExpression) node).elements) {
				t2 = eval(e);
				types.add(t2);
				if (t2 != null && !t2.isVariable()) {
					if (t1 == null) {
						t1 = t2;
					} else {
						if (t1 != t2) {
							addError(new TypingError(node, "type mismatch '" + e + "'"));
						}
					}
				}
			}
			if (t1 == null) {
				// TODO: build cross equality constraints
				result = parser.types.createType();
			} else {
				for (Type t : types) {
					if (t.isVariable()) {
						addTypingConstraint(TypingConstraint.Kind.EQ, t1, t, node, node);
					}
				}
				result = parser.types.getDependentType("Map", parser.types.INT, t1);
			}
			break;

		case AstNode.NUMBER_LITERAL:
			result = ((NumberLiteral) node).type;
			break;

		case AstNode.RETURN_STATEMENT:

			hadReturnStatement = true;
			if (((ReturnStatement) node).expression != null) {
				result = eval(((ReturnStatement) node).expression);
			} else {
				result = parser.types.VOID;
			}

			typeVar = invocationStack.peek().getExecutableType();

			addTypingConstraint(TypingConstraint.Kind.EQ, result, typeVar.getReturnType(), node, typeVar.executable);

			break;

		case AstNode.RULE:
			Rule r = ((Rule) node);

			// invocationStack.push(new AttrContext(invocationStack.peek()));

			// if(r.atPredicate!=null) {
			// eval(r.atPredicate);
			// }
			if (r.guard != null) {
				eval(r.guard);
			}
			s = r.statements;
			while (s != null) {
				eval(s.get());
				s = s.next();
			}

			// invocationStack.pop();

			break;

		case AstNode.CASE_STATEMENT:
			for (Rule rl : ((CaseStatement) node).cases) {
				if (rl.guard != null) {
					eval(rl.guard);
				}
				Sequence<Statement> sq = rl.statements;
				while (sq != null) {
					eval(sq.get());
					sq = sq.next();
				}
			}
			Sequence<Statement> ds = ((CaseStatement) node).defaultStatements;
			while (ds != null) {
				eval(ds.get());
				ds = ds.next();
			}
			break;

		case AstNode.SET_CONSTRUCTOR:
			if (((SetConstructor) node).name != null) {
				Constructor c = parser.types.constructors.get(((SetConstructor) node).name);
				// System.out.println("CONSTRUCTORS2="+parser.ast.constructors);
				// System.out.println("TYPE2="+parser.ast.types);
				// System.out.println("======>"+c);
				// System.out.println("======>"+c.type);
				if (c == null) {
					addError(new TypingError(node, "undeclared constructor '" + ((SetConstructor) node).name + "'"));
					break;
				}
				for (Assignment a : ((SetConstructor) node).fieldAssignments) {
					Variable fieldVariable = ((Variable) a.assignee);
					if (!c.type.getFields().containsKey(fieldVariable.name)) {
						addError(new TypingError(a, "undeclared field '" + fieldVariable.name + "'"));
					} else {
						t2 = eval(a.assignment);
						addTypingConstraint(TypingConstraint.Kind.EQ, t2, c.type.getFields().get(fieldVariable.name),
								a.assignment, fieldVariable);
					}
				}
				result = c.type;
			} else {
				typeVar = parser.types.createType();
				for (Assignment a : ((SetConstructor) node).fieldAssignments) {
					t2 = eval(a.assignment);
					typeVar.addField(((Variable) a.assignee).name, t2);
				}
				result = typeVar;
			}
			break;

		case AstNode.SET_DECLARATION:
			t1 = eval(((SetDeclaration) node).lowerBound);
			addTypingConstraint(TypingConstraint.Kind.EQ, t1, parser.types.INT, ((SetDeclaration) node).lowerBound,
					((SetDeclaration) node).lowerBound);
			t2 = eval(((SetDeclaration) node).upperBound);
			addTypingConstraint(TypingConstraint.Kind.EQ, t2, parser.types.INT, ((SetDeclaration) node).upperBound,
					((SetDeclaration) node).upperBound);
			result = parser.types.getDependentType("Set", parser.types.INT);
			break;

		case AstNode.SET_EXPRESSION:
			Expression set = ((SetExpression) node).set;

			Type t = parser.types.createType("Set");
			if (set instanceof Variable && invocationStack.peek().get(((Variable) set).name) == null) {
				if ((typeVar = parser.types.types.get(((Variable) set).name)) != null) {
					t.addTypeParameter(typeVar);
				} else {
					addError(new TypingError(set, "undefined set variable or type"));
					break;
				}
			} else {
				t.addTypeParameter(parser.types.createType());
			}
			t1 = eval(set);

			addTypingConstraint(TypingConstraint.Kind.EQ, t1, t, ((SetExpression) node).set,
					((SetExpression) node).set);
			for (Variable v : ((SetExpression) node).variables) {
				t2 = eval(v);
				addTypingConstraint(TypingConstraint.Kind.EQ, t2, t.getTypeParameters().get(0), v, v);
			}
			eval(((SetExpression) node).expression);
			result = parser.types.BOOLEAN;
			break;

		case AstNode.STRING_LITERAL:
			result = parser.types.STRING;
			break;

		case AstNode.SUB_ARRAY_ACCESS:
			t1 = eval(((SubArrayAccess) node).minExpression);
			addTypingConstraint(TypingConstraint.Kind.EQ, t1, parser.types.INT, ((SubArrayAccess) node).minExpression,
					((SubArrayAccess) node).minExpression);
			t2 = eval(((SubArrayAccess) node).maxExpression);
			addTypingConstraint(TypingConstraint.Kind.EQ, t2, parser.types.INT, ((SubArrayAccess) node).maxExpression,
					((SubArrayAccess) node).maxExpression);
			t = eval(((SubArrayAccess) node).variableAccess);
			result = parser.types.createType("Map");
			result.addTypeParameter(parser.types.INT);
			result.addTypeParameter(parser.types.createType());
			addTypingConstraint(TypingConstraint.Kind.EQ, t, result, node, node);
			break;

		case AstNode.THIS_LITERAL:
			result = parser.types.THREAD;
			break;

		case AstNode.UNARY_OPERATOR:
			UnaryOperator u = (UnaryOperator) node;
			t1 = eval(u.operand);
			switch (u.kind) {
			case MINUS:
			case POST_DEC:
			case POST_INC:
				result = parser.types.createType();
				addTypingConstraint(TypingConstraint.Kind.LTE, t1, parser.types.DOUBLE, u, u);
				addTypingConstraint(TypingConstraint.Kind.EQ, t1, result, u.operand, node);
				break;
			case NOT:
				result = parser.types.BOOLEAN;
				break;
			case OPT:
			default:
				throw new RuntimeException("unsupported operator " + u);
			}
			break;

		case AstNode.VARIABLE:
			if (forceVariableDeclaration) {
				result = invocationStack.peek().get(((Variable) node).name);
				if (result == null) {
					addError(new TypingError(node, "undeclared field or variable"));
				}
			} else {
				result = invocationStack.peek().getOrCreate((Variable) node);
			}
			break;

		default:
			throw new RuntimeException("unsuported syntax node: " + node + " (" + node.getClass() + ")");

		}

		if (result != null) {
			node.setType(result);
		}
		evaluationStack.pop();
		return result;
	}

	Type getOrCreateNodeTypeVariable(AstNode node) {
		if (node.getType() == null) {
			node.setType(parser.types.createType());
		}
		return node.getType();
	}

	boolean isNumber(Type type) {
		return type == parser.types.BYTE || type == parser.types.FLOAT || type == parser.types.INT
				|| type == parser.types.DOUBLE || type == parser.types.LONG;
	}

	public void printErrors(PrintStream out) {
		for (TypingError error : errors) {
			out.println(error.toString());
		}
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	public void printConstraints(String indent, PrintStream out) {
		int i = 0;
		for (TypingConstraint constraint : constraints) {
			out.println(indent + (i++) + ". " + constraint.toString());
		}
	}

	public void printError(PrintStream out, Exception e) {
		out.println("Error: " + e);
		out.println("Evaluation stack:");
		printEvaluationStackTrace(out);
		if (!invocationStack.isEmpty()) {
			out.println("Context:");
			invocationStack.peek().prettyPrint(out);
		}
	}

	public void printEvaluationStackTrace(PrintStream out) {
		for (int i = evaluationStack.size() - 1; i >= 0; i--) {
			AstNode node = evaluationStack.get(i);
			out.print("    ");
			printNode(out, node);
			out.println();
		}
	}

	public void printNode(PrintStream out, AstNode node) {
		out.print("'" + node + "'" + (node != null && node.token() != null ? " at " + node.token().getLocation() : ""));
	}

	public void unify() {
		// remove wrong constraints
		for (TypingConstraint c : new ArrayList<TypingConstraint>(constraints)) {
			if (c.left == null || c.right == null) {
				constraints.remove(c);
			}
		}

		// do substitution
		boolean allUsed = false;
		while (!allUsed) {
			allUsed = true;
			for (TypingConstraint substitution : constraints) {
				substitution.normalize();
				if (substitution.kind != TypingConstraint.Kind.EQ) {
					continue;
				}
				if ((!substitution.left.isVariable() && !substitution.right.isVariable()) || substitution.used) {
					continue;
				}
				for (TypingConstraint current : constraints) {
					current.substitute(substitution);
				}
				allUsed = false;
				substitution.used = true;
			}
			// System.out.println("===> after substitution");
			// printConstraints(System.out);
			simplify();
			// System.out.println("===> after simplification");
			// printConstraints(System.out);
		}
		for (TypingConstraint c : constraints) {
			if (!c.left.isVariable() && !c.right.isVariable() && c.left != c.right) {
				addError(new TypingError(c.leftOrigin,
						"type mismatch: '" + c.left + "' is not compatible with '" + c.right + "'"));
				if (c.leftOrigin != c.rightOrigin) {
					addError(new TypingError(c.rightOrigin,
							"type mismatch: '" + c.left + "' is not compatible with '" + c.right + "'"));
				}
			}
		}

	}

	public void simplify() {
		boolean simplified = true;
		while (simplified) {
			simplified = false;
			for (TypingConstraint c : new ArrayList<TypingConstraint>(constraints)) {
				// remove tautologies
				if (c.left == c.right) {
					constraints.remove(c);
					simplified = true;
					continue;
				}
				if (c.kind != TypingConstraint.Kind.EQ) {
					if (!c.left.isVariable() && !c.left.isVariable()) {
						// System.out.println("===> " + c + "===> "
						// + c.left.isLTE(c.right) + "===> "
						// + c.left.isGTE(c.right));
						// System.out.println("===> " + c.left.superType);
						if (c.kind == TypingConstraint.Kind.LTE && c.left.isLTE(c.right)) {
							constraints.remove(c);
						}
						if (c.kind == TypingConstraint.Kind.GTE && c.left.isGTE(c.right)) {
							constraints.remove(c);
						}
					}
					if (c.left.hasFields() || c.right.hasFields()) {
						c.kind = TypingConstraint.Kind.EQ;
					}
					continue;
				}

				// skip
				if (((c.left.getFields() == null || c.left.getFields().isEmpty())
						&& (c.right.getFields() == null || c.right.getFields().isEmpty()))
						&& (c.left.isVariable() && c.left.getName().startsWith("_")
								|| c.left.isVariable() && c.left.getName().startsWith("_"))) {
					continue;
				}
				List<TypingError> errors = new ArrayList<TypingError>();
				List<TypingConstraint> subConstraints = c.reduce(parser.types, errors);
				if (!errors.isEmpty()) {
					for (TypingError e : errors) {
						addError(e);
					}
				} else if (!subConstraints.isEmpty()) {
					constraints.remove(c);
					constraints.addAll(subConstraints);
					simplified = true;
				}
			}
		}
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

	private void addError(TypingError error) {
		// System.err.println(error);
		// new Exception().printStackTrace();
		for (TypingError e : errors) {
			if (e.level == error.level && e.message.equals(error.message) && e.origin == error.origin) {
				return;
			}
		}
		errors.add(error);
	}

	public void createTypes() {
		Type type;

		// pass -1: create anonymous constructors
		/*
		 * for (UserType t : parser.ast.userTypes) { for (Constructor c :
		 * t.constructors) { if(c.name.startsWith("_C")) { new
		 * Constructor(parser,c.token,t.name,new ArrayList<Field>()); } } }
		 */

		// pass 0: create user types for unreferenced constructors
		/*
		 * List<Constructor> constructors = new ArrayList<Constructor>(
		 * parser.ast.constructors.values()); for (UserType t :
		 * parser.ast.userTypes) { for (Constructor c : t.constructors) {
		 * constructors.remove(c); } } for (Constructor c : constructors) { if
		 * (c.fields == null || c.fields.isEmpty()) { continue; }
		 * //System.out.println("create user type for "+c); new UserType(null,
		 * c.token, null, Arrays .asList(new Constructor[] { c })); }
		 */

		// pass 1: register types for user types
		for (UserType t : parser.types.userTypes) {
			type = new Type(t);
			if (parser.types.types.containsKey(t.name)) {
				addError(new TypingError(t, "duplicate type name '" + t.name + "'"));
			} else {
				parser.types.register(t.name, type);
			}
			t.type = type;
		}

		// pass 2: register types for constructors
		for (UserType t : parser.types.userTypes) {
			for (Constructor c : t.constructors) {
				type = parser.types.createType(c.name);
				type.superType = t.type;
				t.type.addSubType(type);
				type.variable = false;
				type.constructorType = true;
				c.type = type;
				if (parser.types.types.containsKey(c.name)) {
					if (c.name.equals(t.name)) {
						if (t.constructors.size() == 1) {
							// the constructor type will override the user type
							// type T = [x:Int]
							type.constructorType = false;
							parser.types.types.put(c.name, type);
						} else {
							// constructor cannot be named after the type name
							// or anonymous when it is not the sole constructor
							addError(new TypingError(c, "illegal constructor '" + c.name + "'"));
						}
					} else {
						addError(new TypingError(c, "duplicate type name '" + c.name + "'"));
					}
				} else {
					parser.types.register(c.name, type);
				}
			}
		}

		// pass 3: create field types
		for (UserType t : parser.types.userTypes) {
			for (Constructor c : t.constructors) {
				if (c.fields != null) {
					for (Field f : c.fields) {
						type = getFieldType(f.constructor);
						if (type == null || type.constructorType) {
							addError(new TypingError(f.constructor, "illegal type reference"));
						}
						c.type.addField(f.name, type);
					}
				}
			}
		}

		// pass 4: create type fields
		for (UserType t : parser.types.userTypes) {
			if (t.constructors == null || t.constructors.isEmpty()) {
				continue;
			}
			type = t.constructors.get(0).type;
			if (!type.hasFields()) {
				continue;
			}
			for (Entry<String, Type> field : type.getFields().entrySet()) {
				t.type.addField(field.getKey(), field.getValue());
			}
			for (int i = 1; i < t.constructors.size(); i++) {
				type = t.constructors.get(i).type;
				if (!type.hasFields()) {
					t.type.fields.clear();
				} else {
					for (Entry<String, Type> field : type.getFields().entrySet()) {
						Type fieldType = t.type.fields.get(field.getKey());
						if (fieldType != null && fieldType != field.getValue()) {
							t.type.fields.remove(field.getKey());
						}
					}
				}

			}
		}

		// System.out.println("CONSTRUCTORS1="+parser.ast.constructors);
		// System.out.println("TYPE1="+parser.ast.types);
	}

	private Type getFieldType(Constructor c) {
		if (c.type != null) {
			return c.type;
		} else {
			Type type = null;
			if (c.element == null) {
				type = parser.types.aliases.get(c.name);
				if (type == null) {
					type = parser.types.types.get(c.name);
				}
				if (type != null && type.superType != null && type.name.startsWith("_C")) {
					type = type.superType;
					throw new RuntimeException("????");
				}
			} else {
				type = parser.types.getDependentType("Map", parser.types.INT, getFieldType(c.element));
			}
			if (type == null) {
				addError(new TypingError(c, "undeclared type"));
			} else {
				c.type = type;
			}
			return type;
		}
	}

}
