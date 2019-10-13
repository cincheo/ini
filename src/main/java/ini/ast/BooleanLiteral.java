package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class BooleanLiteral extends AstElement implements Expression {

	public boolean value;
	
	public BooleanLiteral(IniParser parser, Token token, boolean value) {
		super(parser, token);
		this.value=value;
		this.type = parser.types.BOOLEAN;
		this.nodeTypeId=AstNode.BOOLEAN_LITERAL;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(value?"true":"false");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitBooleanLiteral(this);
	}
	
}
