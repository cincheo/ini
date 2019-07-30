package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;
import java.util.List;

public class ConstructorMatchExpression extends NamedElement implements Expression {

	public List<Expression> fieldMatchExpressions;
	
	public ConstructorMatchExpression(IniParser parser, Token token, String name, List<Expression> fieldMatchExpressions) {
		super(parser, token, name);
		this.fieldMatchExpressions = fieldMatchExpressions;
		this.nodeTypeId = CONSTRUCTOR_MATCH_EXPRESSION;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(name);
		if(fieldMatchExpressions!=null && !fieldMatchExpressions.isEmpty()) {
			out.print("[");
			prettyPrintList(out, fieldMatchExpressions, ",");
			out.print("]");
		}
	}

}
