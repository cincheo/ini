package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class Variable extends NamedElement implements VariableAccess {

	public Variable(IniParser parser, Token token, String name) {
		super(parser, token, name);
		this.nodeTypeId=AstNode.VARIABLE;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(name);
	}
	
}
