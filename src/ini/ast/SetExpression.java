package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;
import java.util.List;

public class SetExpression extends AstElement implements Expression {

	public List<Variable> variables;
	public Expression set;
	public Expression expression;
	
	public SetExpression(IniParser parser, Token token, List<Variable> variables, Expression set, Expression expression) {
		super(parser, token);
		this.variables = variables;
		this.set = set;
		this.expression = expression;
		this.nodeTypeId=AstNode.SET_EXPRESSION;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		prettyPrintList(out, variables, ",");
		out.print(" in "+set+" | ");
		expression.prettyPrint(out);
	}

}
