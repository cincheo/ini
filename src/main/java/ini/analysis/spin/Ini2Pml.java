package ini.analysis.spin;

import ini.ast.Assignment;
import ini.ast.AstNode;
import ini.ast.AtPredicate;
import ini.ast.BinaryOperator;
import ini.ast.BooleanLiteral;
import ini.ast.CaseStatement;
import ini.ast.CharLiteral;
import ini.ast.Function;
import ini.ast.Rule;
import ini.ast.Sequence;
import ini.ast.Statement;
import ini.ast.StringLiteral;
import ini.ast.UnaryOperator;
import ini.ast.Variable;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.AttrContext;
import ini.type.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class Ini2Pml {

	public IniParser parser;
	public Stack<AttrContext> invocationStack = new Stack<AttrContext>();
	public Stack<AstNode> evaluationStack = new Stack<AstNode>();
	public Map<String, Data> variables = new HashMap<String, Data>();
	public Map<String, Type> variableTypes = new HashMap<String, Type>();
	public StringBuffer variableDeclaration = new StringBuffer();
	public StringBuffer initPromelaCode = new StringBuffer();
	public List<String> activeProctypes = new ArrayList<String>();
	public List<String> rendezvousChannels = new ArrayList<String>();
	public List<String> bufferedChannels = new ArrayList<String>();
	public List<String> observedVariables = new ArrayList<String>();
	public List<AstNode> exclusiveNodes = new ArrayList<AstNode>();

	public Ini2Pml(IniParser parser) {
		this.parser = parser;
	}

	public void generate(AstNode node, StringBuffer out) {

		evaluationStack.push(node);

		switch (node.nodeTypeId()) {

		case AstNode.ARRAY_ACCESS:

			break;

		case AstNode.ASSIGNMENT:
			Assignment a = (Assignment) node;
			// System.out.println(">>>>>>>>>>>>" + observedVariables +
			// a.assignee.toString());
			if (!exclusiveNodes.contains(a)
					&& observedVariables.contains(a.assignee.toString())) {
				out.append("atomic {\n");
			}
			generate(a.assignee, out);
			out.append("=");
			generate(a.assignment, out);
			out.append(";\n");
			if (!exclusiveNodes.contains(a)
					&& observedVariables.contains(a.assignee.toString())) {
				out.append("chan_" + a.assignee.toString() + "!"
						+ a.assignee.toString() + "\n");
				out.append("}\n");
			}
			// System.out.println("check type:" + a.assignee +
			// a.assignment.getType());
			variableTypes.put(a.assignee.toString(), a.assignment.getType());
			// System.out.println(variableTypes);

			break;

		case AstNode.BINARY_OPERATOR:
			BinaryOperator b = (BinaryOperator) node;
			generate(b.left, out);
			switch (b.kind) {
			case AND:
				out.append(" && ");
				break;
			case OR:
				out.append(" || ");
				break;
			case DIV:
				out.append(" / ");
				break;
			case MULT:
				out.append(" * ");
				break;
			case MINUS:
				out.append(" - ");
				break;
			case PLUS:
				out.append(" + ");
				break;
			case EQUALS:
				out.append(" == ");
				break;
			case NOTEQUALS:
				out.append(" != ");
				break;
			case GT:
				out.append(" > ");
				break;
			case GTE:
				out.append(" >= ");
				break;
			case LT:
				out.append(" < ");
				break;
			case LTE:
				out.append(" <= ");
				break;
			case MATCHES:

				break;
			case CONCAT:

				break;
			default:
				throw new RuntimeException("unsuported operator: " + b);
			}
			generate(b.right, out);
			break;

		case AstNode.BOOLEAN_LITERAL:
			BooleanLiteral bl = (BooleanLiteral) node;
			if (bl.value == true) {
				out.append(" true ");
			} else {
				out.append(" false ");
			}
			break;

		case AstNode.CHAR_LITERAL:
			CharLiteral cl = (CharLiteral) node;
			out.append(cl.toString());
			break;

		case AstNode.CONSTRUCTOR_MATCH_EXPRESSION:

			break;

		case AstNode.FIELD_ACCESS:

			break;

		case AstNode.FUNCTION:
			Function f = (Function) node;
			if (f.rules.size() > 0) {
				if (f.name.equals("main")) {
					activeProctypes.add("main");
				}
				out.append("proctype ");
				out.append(f.name);
				out.append("(");
				// TODO Add parameters
				out.append(")");
				out.append("{\n");
				// out.append("endM:\n");
				out.append("do\n");
				for (Rule r : f.rules) {
					generate(r, out);
				}
				out.append("od;\n");
				out.append("}\n");
			}

			for (Rule r : f.atRules) {
				switch (r.atPredicate.kind) {
				case EVERY:
					out.append("proctype ");
					out.append("every" + r.hashCode());
					activeProctypes.add("every" + r.hashCode());
					out.append("(");
					// TODO Add parameters
					out.append(")");
					out.append("{\n");
					// TODO
					// out.append("endE:\n");
					out.append("do\n");
					out.append(":: true ->\n");
					generateStatements(r.statements, out);
					out.append("od;\n");
					out.append("}\n");
					break;
				case UPDATE:
					// Generate monitor proctype for the variable
					String observedVariable = r.atPredicate.inParameters.get(0)
							.toString().split("=")[1];
					// observedVariables.add(observedVariable);
					rendezvousChannels.add("chan_" + observedVariable);
					String tempVariable = "temp_" + observedVariable;
					// generateTypeDeclaration(variableTypes.get(observedVariable));
					// variableDeclaration.append(tempVariable + "\n");
					activeProctypes.add("update_" + observedVariable);
					out.append("proctype update_" + observedVariable + "()\n");
					out.append("{\n");
					// out.append("endU:\n");
					out.append("do\n");
					out.append(":: atomic {\n");
					out.append("chan_" + observedVariable + " ? "
							+ tempVariable + "->\n");
					out.append("if\n");
					out.append("::(" + tempVariable + " != " + observedVariable
							+ ")->\n");
					generateStatements(r.statements, out);
					out.append(":: else -> skip;\n");
					out.append("fi;\n");
					out.append("}\n");
					out.append("od;\n");
					out.append("}\n");
					break;
				}
			}

			initPromelaCode.append("init ");
			initPromelaCode.append("{\n");
			if (!f.initRules.isEmpty()) {
				// initPromelaCode.append("atomic {\n");
				for (Rule initRule : f.initRules) {
					Sequence<Statement> exclusiveStatements = initRule.statements;
					while (exclusiveStatements != null) {
						exclusiveNodes.add((AstNode) exclusiveStatements.get());
						exclusiveStatements = exclusiveStatements.next();
					}
					generateStatements(initRule.statements, initPromelaCode);
				}
				// initPromelaCode.append("}\n");
			}
			if (!activeProctypes.isEmpty()) {
				if (activeProctypes.size() > 1) {
					initPromelaCode.append("atomic {\n");
				}
				for (String p : activeProctypes) {
					initPromelaCode.append("run " + p + "();\n");
				}
				if (activeProctypes.size() > 1) {
					initPromelaCode.append("}\n");
				}
			}
			if (!f.endRules.isEmpty()) {
				// initPromelaCode.append("atomic {\n");
				for (Rule endRule : f.endRules) {
					Sequence<Statement> exclusiveStatements = endRule.statements;
					while (exclusiveStatements != null) {
						exclusiveNodes.add((AstNode) exclusiveStatements.get());
						exclusiveStatements = exclusiveStatements.next();
					}
					generateStatements(endRule.statements, initPromelaCode);
				}
				// initPromelaCode.append("}\n");
			}
			initPromelaCode.append("}\n");
			// System.out.println(">>>>>" + exclusiveNodes);
			generateVariables(variableTypes);
			// Generate temp variables
			for (String v : observedVariables) {
				generateTypeDeclaration(variableTypes.get(v));
				variableDeclaration.append("temp_" + v + "\n");
			}
			generateRendezvousChannels();
			break;

		case AstNode.FUNCTION_LITERAL:

			break;

		case AstNode.INVOCATION:

			break;

		case AstNode.LIST_EXPRESSION:

			break;

		case AstNode.NUMBER_LITERAL:
			out.append(node.toString());
			break;

		case AstNode.RETURN_STATEMENT:

			break;

		case AstNode.RULE:
			Rule rule = (Rule) node;
			out.append(":: (");
			// TODO Generate a guard
			out.append(rule.guard.toString());
			out.append(") -> accept" + rule.hashCode() + ": progress" + rule.hashCode() + ":\n");
			generateStatements(((Rule) node).statements, out);
			break;

		case AstNode.CASE_STATEMENT:
			CaseStatement cs = (CaseStatement) node;
			out.append("do\n");
			boolean printDefault = (cs.defaultStatements != null);
			for (Rule r : cs.cases) {
				generate(r, out);
			}
			if (printDefault) {
				out.append(":: else ->");
				generateStatements(cs.defaultStatements, out);
			}
			out.append("od;\n");
			break;

		case AstNode.SET_CONSTRUCTOR:

			break;

		case AstNode.SET_DECLARATION:

			break;

		case AstNode.SET_EXPRESSION:

			break;

		case AstNode.STRING_LITERAL:
			StringLiteral sl = (StringLiteral) node;
			out.append(sl.toString());
			break;

		case AstNode.SUB_ARRAY_ACCESS:

			break;

		case AstNode.THIS_LITERAL:

			break;

		case AstNode.UNARY_OPERATOR:
			UnaryOperator uo = (UnaryOperator) node;
			out.append(uo.toString() + ";\n");
			break;

		case AstNode.VARIABLE:
			Variable v = (Variable) node;
			out.append(v.name);
			break;

		default:
			throw new RuntimeException("unsuported syntax node: " + node + " ("
					+ node.getClass() + ")");

		}

		evaluationStack.pop();

	}

	public void generateStatements(Sequence<Statement> s, StringBuffer out) {
		while (s != null) {
			generate(s.get(), out);
			s = s.next();
			// out.append(";\n");
		}
	}

	public void generateVariables(Map<String, Type> variableTypes) {
		Set<String> varibles = (Set<String>) variableTypes.keySet();
		for (String v : varibles) {
			generateTypeDeclaration(variableTypes.get(v));
			variableDeclaration.append(v + "\n");
		}
	}

	public void generateTypeDeclaration(Type type) {
		if (type == parser.ast.INT || type == parser.ast.LONG) {
			variableDeclaration.append("byte ");
		} else if (type == parser.ast.BOOLEAN) {
			variableDeclaration.append("bool ");
		}
	}
	
	public String convertType(Type type) {
		if (type == parser.ast.INT || type == parser.ast.LONG) {
			return "byte";
		} else if (type == parser.ast.BOOLEAN) {
			return "bool";
		}
		return null;
	}

	public void generateRendezvousChannels() {
		for (String s : observedVariables) {
			variableDeclaration.append("chan chan_" + s
					+ "= [0] of {" + convertType(variableTypes.get(s)) + "}\n");
		}
	}

	public void generateBufferedChannels(int capacity) {
		for (String s : observedVariables) {
			variableDeclaration.append("chan chan_" + s
					+ "= [" + capacity +"] of {" + convertType(variableTypes.get(s)) + "}\n");
		}
	}

	public void generateNotification(Sequence<Statement> s, String variable,
			StringBuffer out) {
		if (observedVariables.contains(variable)) {
			out.append("atomic {\n");
		}
		generateStatements(s, out);
		if (observedVariables.contains(variable)) {
			out.append("chan_" + variable + "!" + variable + "\n");
			out.append("}\n");
		}
	}

	public void generateObserverdVariabsles(AstNode node) {
		switch (node.nodeTypeId()) {
		case AstNode.FUNCTION:
			Function f = (Function) node;
			for (Rule r : f.atRules) {
				if (r.atPredicate.kind.equals(AtPredicate.Kind.UPDATE)) {
					String observedVariable = r.atPredicate.inParameters.get(0)
							.toString().split("=")[1];
					observedVariables.add(observedVariable);
				}
			}

		}
	}

}
