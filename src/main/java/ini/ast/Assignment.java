package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class Assignment extends AstElement implements Statement, Expression {

	public VariableAccess assignee;
	public Expression assignment;

	public Assignment(IniParser parser, Token token, VariableAccess assignee, Expression assignment) {
		super(parser, token);
		this.assignee = assignee;
		this.assignee.setDeclaration(true);
		this.assignment = assignment;
		this.nodeTypeId = AstNode.ASSIGNMENT;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		assignee.prettyPrint(out);
		out.print("=");
		assignment.prettyPrint(out);
	}

}
