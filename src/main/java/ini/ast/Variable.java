package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class Variable extends NamedElement implements VariableAccess {

	private boolean declaration = false;

	public Variable(IniParser parser, Token token, String name) {
		super(parser, token, name);
		this.nodeTypeId = AstNode.VARIABLE;
	}

	public final void setDeclaration(boolean declaration) {
		this.declaration = declaration;
	}

	public final boolean isDeclaration() {
		return declaration;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(name);
	}

}
