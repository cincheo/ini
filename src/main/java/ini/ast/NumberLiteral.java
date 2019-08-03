package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class NumberLiteral extends AstElement implements Expression {

	public Number value;
	
	public NumberLiteral(IniParser parser, Token token, Number value) {
		super(parser, token);
		this.value=value;
		if(value instanceof Byte) {
			this.type = parser.ast.BYTE;
		} if(value instanceof Integer) {
			this.type = parser.ast.INT;
		} else if(value instanceof Float) {
			this.type = parser.ast.FLOAT;
		} else if(value instanceof Double) {
			this.type = parser.ast.FLOAT;
		}
		this.nodeTypeId=AstNode.NUMBER_LITERAL;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(value);
	}
	
}
