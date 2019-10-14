package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class BinaryOperator extends AstElement implements Expression {

	public enum Kind {
		PLUS, MINUS, MULT, DIV, EQUALS, NOTEQUALS, LT, LTE, GT, GTE, AND, OR, MATCHES, CONCAT, IMPLIES
	}

	public Kind kind;
	public Expression left;
	public Expression right;

	public BinaryOperator(IniParser parser, Token token, Kind kind,
			Expression left, Expression right) {
		super(parser, token);
		this.kind = kind;
		this.left = left;
		this.right = right;
		this.nodeTypeId = AstNode.BINARY_OPERATOR;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		left.prettyPrint(out);
		switch (kind) {
		case PLUS:
			out.print("+");
			break;
		case MINUS:
			out.print("-");
			break;
		case MULT:
			out.print("*");
			break;
		case DIV:
			out.print("/");
			break;
		case EQUALS:
			out.print("==");
			break;
		case NOTEQUALS:
			out.print("!=");
			break;
		case LT:
			out.print("<");
			break;
		case GT:
			out.print(">");
			break;
		case LTE:
			out.print("<=");
			break;
		case GTE:
			out.print(">=");
			break;
		case AND:
			out.print("&&");
			break;
		case OR:
			out.print("||");
			break;
		case MATCHES:
			out.print("~");
			break;
		case CONCAT:
			out.print("&");
			break;
		case IMPLIES:
			out.print("=>");
			break;
		}
		right.prettyPrint(out);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitBinaryOperator(this);
	}
	
}
