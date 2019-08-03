package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class UnaryOperator extends AstElement implements Expression, Statement {

	public enum Kind {
		MINUS, NOT, OPT, PRE_INC, POST_INC, PRE_DEC, POST_DEC
	}

	public Kind kind;
	public Expression operand;
	public boolean expanded = false;

	public UnaryOperator(IniParser parser, Token token, Kind kind, Expression operand) {
		super(parser, token);
		this.kind = kind;
		this.operand = operand;
		this.nodeTypeId = AstNode.UNARY_OPERATOR;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		switch (kind) {
		case MINUS:
			out.print("-");
			break;
		case NOT:
			out.print("!");
			break;
		case OPT:
			out.print("?");
			break;
		case PRE_DEC:
			out.print("--");
			break;
		case PRE_INC:
			out.print("++");
			break;
		default:
		}

		operand.prettyPrint(out);

		switch (kind) {
		case POST_DEC:
			out.print("--");
			break;
		case POST_INC:
			out.print("++");
			break;
		default:
		}
	}

}
