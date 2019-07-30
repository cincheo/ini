package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class FieldAccess extends AstElement implements VariableAccess {

	public VariableAccess variableAccess;
	public String fieldName;
	
	public FieldAccess(IniParser parser, Token token, VariableAccess variableAccess, String fieldName) {
		super(parser, token);
		this.variableAccess = variableAccess;
		this.fieldName = fieldName;
		this.nodeTypeId=AstNode.FIELD_ACCESS;
	}
	
	@Override
	public void prettyPrint(PrintStream out) {
		variableAccess.prettyPrint(out);
		out.print("."+fieldName);
	}
	
}
