package ini.analysis.spin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import ini.ast.Assignment;
import ini.ast.AstNode;
import ini.ast.AtPredicate;
import ini.ast.BinaryOperator;
import ini.ast.BooleanLiteral;
import ini.ast.CaseStatement;
import ini.ast.Channel;
import ini.ast.CharLiteral;
import ini.ast.Expression;
import ini.ast.Invocation;
import ini.ast.Parameter;
import ini.ast.Predicate;
import ini.ast.Process;
import ini.ast.Rule;
import ini.ast.Sequence;
import ini.ast.Statement;
import ini.ast.StringLiteral;
import ini.ast.UnaryOperator;
import ini.ast.Variable;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.AttrContext;
import ini.type.Type;

public class Ini2Pml {

	public IniParser parser;
	public AstAttrib attrib;
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

	private StringBuilder header = new StringBuilder();
	private StringBuilder body = new StringBuilder();
	private StringBuilder footer = new StringBuilder();

	private StringBuilder out = body;
	private int indent = 0;
	private static final String INDENT = "  ";
	private List<String> checkpoints = new ArrayList<>();

	private List<String> channels = new ArrayList<>();

	private int getOrCreateChannelId(String name) {
		int index = channels.indexOf(name);
		if (index >= 0) {
			return index;
		} else {
			channels.add(name);
			return channels.size() - 1;
		}
	}

	private int getChannelId(String name) {
		return channels.indexOf(name);
	}

	protected final Ini2Pml print(String string) {
		out.append(string);
		return this;
	}

	protected final Ini2Pml printLine(String string) {
		indent();
		out.append(string);
		return this;
	}

	protected final Ini2Pml endLine() {
		out.append("\n");
		return this;
	}

	protected final Ini2Pml startIndent() {
		indent++;
		return this;
	}

	protected final Ini2Pml remove(int numberOfChars) {
		out.delete(out.length() - numberOfChars, out.length());
		return this;
	}

	protected final Ini2Pml endIndent() {
		indent--;
		return this;
	}

	protected final Ini2Pml indent() {
		for (int i = 0; i < indent; i++) {
			print(INDENT);
		}
		return this;
	}

	private final Ini2Pml selectHeader() {
		out = header;
		return this;
	}

	private final Ini2Pml selectBody() {
		out = body;
		return this;
	}

	private final Ini2Pml selectFooter() {
		out = footer;
		return this;
	}

	public String getOutput() {
		return header.toString() + body.toString() + footer.toString();
	}

	public Ini2Pml(IniParser parser, AstAttrib attrib) {
		this.parser = parser;
		this.attrib = attrib;
	}

	public Ini2Pml beforeGenerate() {
		return this;
	}

	public Ini2Pml afterGenerate() {
		selectHeader();
		// printLine("chan channels[" + channels.size() + "]=[10] of
		// {byte}").endLine();
		printLine("int _step_count = 0").endLine();
		printLine("int _step_max = 1000").endLine();
		for (String checkpoint : checkpoints) {
			printLine("bool " + checkpoint + "=false").endLine();
		}
		selectBody();
		return this;
	}

	private String toPmlType(Type type) {
		if (type == null) {
			return "byte";
		}
		type = attrib.getResolvedType(type);
		if (type == null) {
			return "byte";
		}
		if (type.isChannel()) {
			return "chan";
		} else {
			return "byte";
		}
	}

	public Ini2Pml generate(AstNode node) {

		evaluationStack.push(node);

		switch (node.nodeTypeId()) {

		case AstNode.ARRAY_ACCESS:
			// TODO
			break;

		case AstNode.IMPORT:
			// TODO
			break;

		case AstNode.BINDING:
			break;

		case AstNode.ASSIGNMENT:
			Assignment a = (Assignment) node;
			// System.out.println(">>>>>>>>>>>>" + observedVariables +
			// a.assignee.toString());
			if (!exclusiveNodes.contains(a) && observedVariables.contains(a.assignee.toString())) {
				out.append("atomic {\n");
			}
			generate(a.assignee);
			out.append("=");
			generate(a.assignment);
			out.append(";\n");
			if (!exclusiveNodes.contains(a) && observedVariables.contains(a.assignee.toString())) {
				out.append("chan_" + a.assignee.toString() + "!" + a.assignee.toString() + "\n");
				out.append("}\n");
			}
			// System.out.println("check type:" + a.assignee +
			// a.assignment.getType());
			variableTypes.put(a.assignee.toString(), a.assignment.getType());
			// System.out.println(variableTypes);

			break;

		case AstNode.BINARY_OPERATOR:
			BinaryOperator b = (BinaryOperator) node;
			generate(b.left);
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
			generate(b.right);
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

		case AstNode.CHANNEL:
			// getOrCreateChannelId(((Channel) node).name);
			printLine("chan " + ((Channel) node).name + "=[10] of {byte}").endLine();
			break;

		case AstNode.USER_TYPE:
			break;

		case AstNode.FUNCTION:
			break;

		case AstNode.PROCESS:
			Process process = (Process) node;
			if (process.name.equals("main")) {
				activeProctypes.add("main");
			}
			print(process.name.equals("main") ? "active " : "").printLine("proctype ").print(process.name).print("(");
			for (Parameter p : process.parameters) {
				print(toPmlType(p.getType()) + " " + p.name + "; ");
			}
			if (!process.parameters.isEmpty()) {
				remove(2);
			}
			// TODO Add parameters
			print(") {").endLine().startIndent();
			// out.append("endM:\n");
			// printLine("do").startIndent();
			for (Rule r : process.initRules) {
				if (r.guard == null) {
					generateStatements(r.statements);
				} else {
					generate(r);
				}
			}
			// endIndent().indent().print("od").endIndent().endLine();
			// printLine("START: if\n").startIndent();

			for (Rule r : process.atRules) {
				switch (r.atPredicate.kind) {
				case CONSUME:
					// activeProctypes.add("consume" + r.hashCode());
					// printLine("proctype ").print("consume" +
					// r.hashCode()).print("(");
					// TODO Add parameters
					// print(") {").endLine().startIndent();
					printLine(toPmlType(r.atPredicate.outParameters.get(0).getType()) + " "
							+ r.atPredicate.outParameters.get(0).toString()).endLine();
					AstNode channelNode = r.atPredicate.getAnnotationNode("channel", "from");
					// int channelId = getChannelId(channelNode.toString());
					// printLine("CONSUME" + r.hashCode() + ": ");
					printLine("do").endLine().startIndent();

					printLine(":: ").generate(channelNode)
							.print("?" + r.atPredicate.outParameters.get(0).toString() + " ->").endLine().startIndent();
					// if (channelId >= 0) {
					// printLine(":: channels[" + channelId + "]?" +
					// r.atPredicate.outParameters.get(0).toString()
					// + " ->").endLine().startIndent();
					// } else {
					// printLine(":: channels[" + channelNode.toString() + "]?"
					// + r.atPredicate.outParameters.get(0).toString() + "
					// ->").endLine().startIndent();
					// }
					// print(":: " + channel + "?" +
					// r.atPredicate.outParameters.get(0).toString() + "
					// ->").endLine()
					// .startIndent();
					printLine("_step_count++").endLine();
					generateStatements(r.statements).endIndent();
					printLine(":: _step_count > _step_max -> break").endLine().endIndent();
					printLine("od").endLine();
					// printLine("goto CONSUME" + r.hashCode()).endLine();
					// endIndent().printLine("}").endLine();
					break;

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
					generateStatements(r.statements);
					out.append("od;\n");
					out.append("}\n");
					break;
				case UPDATE:
					// Generate monitor proctype for the variable
					String observedVariable = r.atPredicate.annotations.get(0).toString().split("=")[1];
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
					out.append("chan_" + observedVariable + " ? " + tempVariable + "->\n");
					out.append("if\n");
					out.append("::(" + tempVariable + " != " + observedVariable + ")->\n");
					generateStatements(r.statements);
					out.append(":: else -> skip;\n");
					out.append("fi;\n");
					out.append("}\n");
					out.append("od;\n");
					out.append("}\n");
					break;
				default:
				}
			}

			// endIndent().printLine("fi").endLine();
			// printLine("goto START").endLine();
			//printLine("END:").endLine();
			endIndent().printLine("}").endLine();

			/*
			 * initPromelaCode.append("init "); initPromelaCode.append("{\n");
			 * if (!process.initRules.isEmpty()) { // initPromelaCode.append(
			 * "atomic {\n"); for (Rule initRule : process.initRules) {
			 * Sequence<Statement> exclusiveStatements = initRule.statements;
			 * while (exclusiveStatements != null) {
			 * exclusiveNodes.add((AstNode) exclusiveStatements.get());
			 * exclusiveStatements = exclusiveStatements.next(); }
			 * generateStatements(initRule.statements, initPromelaCode); } //
			 * initPromelaCode.append("}\n"); } if (!activeProctypes.isEmpty())
			 * { if (activeProctypes.size() > 1) { initPromelaCode.append(
			 * "atomic {\n"); } for (String p : activeProctypes) {
			 * initPromelaCode.append("run " + p + "();\n"); } if
			 * (activeProctypes.size() > 1) { initPromelaCode.append("}\n"); } }
			 * if (!process.endRules.isEmpty()) { // initPromelaCode.append(
			 * "atomic {\n"); for (Rule endRule : process.endRules) {
			 * Sequence<Statement> exclusiveStatements = endRule.statements;
			 * while (exclusiveStatements != null) {
			 * exclusiveNodes.add((AstNode) exclusiveStatements.get());
			 * exclusiveStatements = exclusiveStatements.next(); }
			 * generateStatements(endRule.statements, initPromelaCode); } //
			 * initPromelaCode.append("}\n"); } initPromelaCode.append("}\n");
			 * // System.out.println(">>>>>" + exclusiveNodes);
			 * generateVariables(variableTypes); // Generate temp variables for
			 * (String v : observedVariables) {
			 * generateTypeDeclaration(variableTypes.get(v));
			 * variableDeclaration.append("temp_" + v + "\n"); }
			 * generateRendezvousChannels();
			 */
			break;

		case AstNode.INVOCATION:
			String checkpoint = node.getAnnotationValue("checkpoint");
			if (checkpoint != null) {
				//checkpoint = "_checkpoint_" + checkpoint;
				printLine(checkpoint + " = true").endLine();
				if (!checkpoints.contains(checkpoint)) {
					checkpoints.add(checkpoint);
				}
			}
			if ("produce".equals(((Invocation) node).name)) {
				printLine(((Invocation) node).arguments.get(0) + "!" + ((Invocation) node).arguments.get(1)).endLine();
			} else if ("stop".equals(((Invocation) node).name)) {
				printLine("break").endLine();
			} else {
				for (AstNode n : parser.topLevels) {
					if (n instanceof Process && ((Process) n).name.equals(((Invocation) node).name)) {
						printLine("run " + ((Invocation) node).name + "(");
						for (Expression e : ((Invocation) node).arguments) {
							generate(e);
							print(", ");
						}
						if (!((Invocation) node).arguments.isEmpty()) {
							remove(2);
						}
						print(")").endLine();
					}
				}
			}
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
			generateStatements(((Rule) node).statements);
			break;

		case AstNode.CASE_STATEMENT:
			CaseStatement cs = (CaseStatement) node;
			out.append("do\n");
			boolean printDefault = (cs.defaultStatements != null);
			for (Rule r : cs.cases) {
				generate(r);
			}
			if (printDefault) {
				out.append(":: else ->");
				generateStatements(cs.defaultStatements);
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
			int channelId = getChannelId(v.name);
			if (channelId >= 0) {
				out.append("channels[" + channelId + "]");
			} else {
				out.append(v.name);
			}
			break;

		case AstNode.PREDICATE:
			selectFooter();
			printLine("ltl " + ((Predicate) node).name + " { ").print(((Predicate) node).expression + " } ");
			selectBody();
			break;

		default:
			throw new RuntimeException("unsuported syntax node: " + node + " (" + node.getClass() + ")");

		}

		evaluationStack.pop();

		return this;
	}

	public Ini2Pml generateStatements(Sequence<Statement> s) {
		while (s != null) {
			generate(s.get());
			s = s.next();
			// out.append(";\n");
		}
		return this;
	}

	public void generateVariables(Map<String, Type> variableTypes) {
		Set<String> varibles = (Set<String>) variableTypes.keySet();
		for (String v : varibles) {
			generateTypeDeclaration(variableTypes.get(v));
			variableDeclaration.append(v + "\n");
		}
	}

	public void generateTypeDeclaration(Type type) {
		if (type == parser.types.INT || type == parser.types.LONG) {
			variableDeclaration.append("byte ");
		} else if (type == parser.types.BOOLEAN) {
			variableDeclaration.append("bool ");
		}
	}

	public String convertType(Type type) {
		if (type == parser.types.INT || type == parser.types.LONG) {
			return "byte";
		} else if (type == parser.types.BOOLEAN) {
			return "bool";
		}
		return null;
	}

	public void generateRendezvousChannels() {
		for (String s : observedVariables) {
			variableDeclaration.append("chan chan_" + s + "= [0] of {" + convertType(variableTypes.get(s)) + "}\n");
		}
	}

	public void generateBufferedChannels(int capacity) {
		for (String s : observedVariables) {
			variableDeclaration
					.append("chan chan_" + s + "= [" + capacity + "] of {" + convertType(variableTypes.get(s)) + "}\n");
		}
	}

	public void generateNotification(Sequence<Statement> s, String variable, StringBuffer out) {
		if (observedVariables.contains(variable)) {
			out.append("atomic {\n");
		}
		generateStatements(s);
		if (observedVariables.contains(variable)) {
			out.append("chan_" + variable + "!" + variable + "\n");
			out.append("}\n");
		}
	}

	public void generateObserverdVariabsles(AstNode node) {
		switch (node.nodeTypeId()) {
		case AstNode.PROCESS:
			Process p = (Process) node;
			for (Rule r : p.atRules) {
				if (r.atPredicate.kind.equals(AtPredicate.Kind.UPDATE)) {
					String observedVariable = r.atPredicate.annotations.get(0).toString().split("=")[1];
					observedVariables.add(observedVariable);
				}
			}

		}
	}

}
