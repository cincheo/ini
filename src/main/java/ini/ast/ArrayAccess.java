package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class ArrayAccess extends AstElement implements VariableAccess {

	public Expression targetExpression;
	public Expression indexExpression;

	public ArrayAccess(IniParser parser, Token token, Expression targetExpression, Expression indexExpression) {
		super(parser, token);
		this.targetExpression = targetExpression;
		this.indexExpression = indexExpression;
		this.nodeTypeId = AstNode.ARRAY_ACCESS;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		targetExpression.prettyPrint(out);
		out.print("[");
		indexExpression.prettyPrint(out);
		out.print("]");
	}

	@Override
	public boolean isDeclaration() {
		if (targetExpression instanceof VariableAccess) {
			return ((VariableAccess) targetExpression).isDeclaration();
		} else {
			return false;
		}
	}

	@Override
	public void setDeclaration(boolean declaration) {
		if (targetExpression instanceof VariableAccess) {
			((VariableAccess) targetExpression).setDeclaration(declaration);
		} else {
			// ignore
		}
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitArrayAccess(this);
	}
}
