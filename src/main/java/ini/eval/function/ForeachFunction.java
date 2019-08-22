package ini.eval.function;

import ini.ast.Expression;
import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

import java.util.List;

public class ForeachFunction extends IniFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		
		return new RawData(eval.eval(params.get(0)).getSize());
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints, Invocation invocation) {
		Type t = new Type(parser);
		Type l = parser.ast.getDependentType("Map", parser.ast.INT, t);
		return parser.ast.getFunctionalType(l, l, parser.ast.getFunctionalType(parser.ast.VOID, t, parser.ast.ANY));

	}

}
