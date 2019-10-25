package ini.ast;

import java.io.PrintStream;

import ini.parser.IniParser;

public class ConditionalExpression extends AstElement implements Expression {
	public Expression condition;
	public Expression trueExpression;
	public Expression falseExpression;

	public ConditionalExpression(IniParser parser, Token token, Expression condition, Expression trueExpression, Expression falseExpression) {
		super(parser, token);
		this.condition = condition;
		this.trueExpression = trueExpression;
		this.falseExpression = falseExpression;
		this.nodeTypeId = AstNode.CONDITIONAL;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.println(condition+"?"+trueExpression+":"+falseExpression);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitConditionalExpression(this);
	}
	
}
