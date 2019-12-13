package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;
import java.util.List;

public class ConstructorMatchExpression extends NamedElement implements Expression {

	public List<Expression> fieldMatchExpressions;
	public TypeVariable type;

	public ConstructorMatchExpression(IniParser parser, Token token, String name,
			List<Expression> fieldMatchExpressions) {
		super(parser, token, name);
		this.fieldMatchExpressions = fieldMatchExpressions;
		this.nodeTypeId = CONSTRUCTOR_MATCH_EXPRESSION;
	}

	public ConstructorMatchExpression(IniParser parser, TypeVariable type) {
		super(parser, type.token, type.name);
		this.type = type;
		this.nodeTypeId = CONSTRUCTOR_MATCH_EXPRESSION;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		if (type != null) {
			type.prettyPrint(out);
		} else {
			out.print(name);
			if (fieldMatchExpressions != null && !fieldMatchExpressions.isEmpty()) {
				out.print("[");
				prettyPrintList(out, fieldMatchExpressions, ",");
				out.print("]");
			}
		}
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitConstructorMatchExpression(this);
	}

}
