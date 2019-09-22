package ini.ast;

public interface VariableAccess extends Expression {
	void setDeclaration(boolean declaration);
	boolean isDeclaration();
}
