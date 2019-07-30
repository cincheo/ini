package testl.parser.ast;

public class BinaryOperator implements Expression {

	public enum OperatorType {
		PLUS, MINUS, MULT, DIV
	}
	
	public OperatorType type;
	public Expression left;
	public Expression right;
	
	public BinaryOperator(OperatorType type, Expression left, Expression right) {
		this.type = type;
		this.left = left;
		this.right = right;
	}
	
	@Override
	public String toString() {
		String s = "(";
		s+=left.toString();
		switch(type) {
		case PLUS:
			s+="+";
			break;
		case MINUS:
			s+="-";
			break;
		case DIV:
			s+="/";
			break;
		case MULT:
			s+="*";
			break;
		}
		s+=right.toString();
		s+=")";
		return s;
	}
	
	
}
