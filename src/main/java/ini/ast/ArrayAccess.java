package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class ArrayAccess extends AstElement implements VariableAccess {

	public VariableAccess variableAccess;
	public Expression indexExpression;

	public ArrayAccess(IniParser parser, Token token, VariableAccess variableAccess, Expression indexExpression) {
		super(parser, token);
		this.variableAccess = variableAccess;
		this.indexExpression = indexExpression;
		this.nodeTypeId = AstNode.ARRAY_ACCESS;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		variableAccess.prettyPrint(out);
		out.print("[");
		indexExpression.prettyPrint(out);
		out.print("]");
	}

	@Override
	public boolean isDeclaration() {
		return variableAccess.isDeclaration();
	}

	@Override
	public void setDeclaration(boolean declaration) {
		variableAccess.setDeclaration(declaration);
	}

}
