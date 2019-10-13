package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class Variable extends NamedElement implements VariableAccess {

	private boolean declaration = false;

	/**
	 * If this variable is an access to a global channel declaration, it will
	 * return the channel after the attribution phase (null otherwise or before
	 * the attribution phase).
	 */
	public Channel channelLiteral;

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

	@Override
	public void accept(Visitor visitor) {
		visitor.visitVariable(this);
	}
	
}
