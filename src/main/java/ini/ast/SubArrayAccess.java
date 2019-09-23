package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class SubArrayAccess extends AstElement {

	public Expression targetExpression;
	public Expression minExpression;
	public Expression maxExpression;

	public SubArrayAccess(IniParser parser, Token token, Expression targetExpression, Expression minExpression,
			Expression maxExpression) {
		super(parser, token);
		this.targetExpression = targetExpression;
		this.minExpression = minExpression;
		this.maxExpression = maxExpression;
		this.nodeTypeId = AstNode.SUB_ARRAY_ACCESS;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		targetExpression.prettyPrint(out);
		out.print("[");
		minExpression.prettyPrint(out);
		out.print("..");
		maxExpression.prettyPrint(out);
		out.print("]");
	}

}
