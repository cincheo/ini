package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class SubArrayAccess extends AstElement implements VariableAccess {

	public VariableAccess variableAccess;
	public Expression minExpression;
	public Expression maxExpression;
	
	public SubArrayAccess(IniParser parser, Token token, VariableAccess variableAccess, Expression minExpression, Expression maxExpression) {
		super(parser, token);
		this.variableAccess = variableAccess;
		this.minExpression = minExpression;
		this.maxExpression = maxExpression;
		this.nodeTypeId=AstNode.SUB_ARRAY_ACCESS;
	}
	
	@Override
	public void prettyPrint(PrintStream out) {
		variableAccess.prettyPrint(out);
		out.print("[");
		minExpression.prettyPrint(out);
		out.print("..");
		maxExpression.prettyPrint(out);
		out.print("]");
	}
	
}
