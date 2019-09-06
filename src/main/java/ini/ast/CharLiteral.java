package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class CharLiteral extends AstElement implements Expression {

	public char value;

	public CharLiteral(IniParser parser, Token token, char value) {
		super(parser, token);
		this.value = value;
		this.type = parser.types.CHAR;
		this.nodeTypeId = AstNode.CHAR_LITERAL;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("'" + value + "'");
	}

}
