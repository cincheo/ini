package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class ReturnStatement extends AstElement implements Statement {

	public Expression expression;
	
	public ReturnStatement(IniParser parser, Token token, Expression expression) {
		super(parser, token);
		this.expression = expression;
		this.nodeTypeId=AstNode.RETURN_STATEMENT;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("return");
		if(expression!=null) {
			out.print(" ");
			expression.prettyPrint(out);
		}
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitReturnStatement(this);
	}
	
}
