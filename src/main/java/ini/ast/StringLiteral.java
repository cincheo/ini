package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class StringLiteral extends AstElement implements Expression {

	public String value;

	public StringLiteral(IniParser parser, Token token, String value) {
		super(parser, token);
		this.value = value;
		this.value = this.value.replace("\\n", "\n");
		this.value = this.value.replace("\\r", "\r");
		this.value = this.value.replace("\\\"", "\"");
		this.value = this.value.replace("\\\\", "\\");
		this.type = parser.types.STRING;
		this.nodeTypeId = AstNode.STRING_LITERAL;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("\"" + value + "\"");
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitStringLiteral(this);
	}
}
