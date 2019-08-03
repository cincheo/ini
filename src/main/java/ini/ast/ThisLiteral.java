package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class ThisLiteral extends AstElement implements Expression {

	public ThisLiteral(IniParser parser, Token token) {
		super(parser, token);
		this.type = parser.ast.THREAD;
		this.nodeTypeId=AstNode.THIS_LITERAL;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("this");
	}

}
