package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class FunctionLiteral extends NamedElement implements Expression {

	public FunctionLiteral(IniParser parser, Token token, String name) {
		super(parser, token, name);
		this.nodeTypeId = AstNode.FUNCTION_LITERAL;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("function(" + name + ")");
	}
	
	@Override
	public String toString() {
		return "function(" + name + ")";
	}
	
}
