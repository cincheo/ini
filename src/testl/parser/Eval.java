package testl.parser;

import testl.parser.ast.BinaryOperator;
import testl.parser.ast.Expression;
import testl.parser.ast.Num;

public class Eval {

	public Double eval(Expression expression) {
		
		if(expression instanceof Num) {
			return ((Num)expression).value;
		}
		if(expression instanceof BinaryOperator) {
			switch (((BinaryOperator)expression).type) {
			case PLUS:
				return eval(((BinaryOperator)expression).left) + eval(((BinaryOperator)expression).right);
			case MINUS:
				return eval(((BinaryOperator)expression).left) - eval(((BinaryOperator)expression).right);
			case MULT:
				return eval(((BinaryOperator)expression).left) * eval(((BinaryOperator)expression).right);
			case DIV:
				return eval(((BinaryOperator)expression).left) / eval(((BinaryOperator)expression).right);
			}
			
		}
		
		throw new RuntimeException("unsuported node");
	
	}
	
	
}
