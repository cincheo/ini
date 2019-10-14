package ini.analysis.spin;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import ini.ast.BinaryOperator;
import ini.ast.BooleanLiteral;
import ini.ast.NumberLiteral;
import ini.ast.Scanner;
import ini.ast.UnaryOperator;
import ini.ast.Variable;

public class LTLPromelaPrinter extends Scanner {

	private PrintStream out;
	private ByteArrayOutputStream baos;

	public LTLPromelaPrinter() {
		try {
			out = new PrintStream(baos = new ByteArrayOutputStream(), true, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return new String(baos.toByteArray(), StandardCharsets.UTF_8);
	}

	@Override
	public void visitBinaryOperator(BinaryOperator binaryOperator) {
		out.print("(");
		scan(binaryOperator.left);
		switch (binaryOperator.kind) {
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
		case IMPLIES:
			out.print("->");
			break;
		default:
			throw new RuntimeException("unsupported operator " + binaryOperator.kind);
		}
		scan(binaryOperator.right);
		out.print(")");
	}

	@Override
	public void visitUnaryOperator(UnaryOperator unaryOperator) {
		switch (unaryOperator.kind) {
		case MINUS:
			out.print("-");
			break;
		case NOT:
			out.print("!");
			break;
		case ALWAYS:
			out.print("[]");
			break;
		case EVENTUALLY:
			out.print("<>");
			break;
		default:
			throw new RuntimeException("unsupported operator " + unaryOperator.kind);
		}
		out.print("(");
		scan(unaryOperator.operand);
		out.print(")");
	}

	@Override
	public void visitNumberLiteral(NumberLiteral numberLiteral) {
		out.print(numberLiteral.toString());
	}

	@Override
	public void visitVariable(Variable variable) {
		out.print(Ini2Pml.VAR_PREFIX + variable.toString());
	}

	@Override
	public void visitBooleanLiteral(BooleanLiteral booleanLiteral) {
		out.print(booleanLiteral.toString());
	}
}
