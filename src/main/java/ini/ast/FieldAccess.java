package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class FieldAccess extends AstElement implements VariableAccess {

	public Expression targetExpression;
	public String fieldName;

	public FieldAccess(IniParser parser, Token token, Expression targetExpression, String fieldName) {
		super(parser, token);
		this.targetExpression = targetExpression;
		this.fieldName = fieldName;
		this.nodeTypeId = AstNode.FIELD_ACCESS;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		targetExpression.prettyPrint(out);
		out.print("." + fieldName);
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
		visitor.visitFieldAccess(this);
	}
	
}
