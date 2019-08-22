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

public class ModFunction extends IniFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		return new RawData(
				eval.eval(params.get(0)).getNumber().intValue() % eval.eval(params.get(1)).getNumber().intValue());
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints, Invocation invocation) {
		return parser.ast.getFunctionalType(parser.ast.INT, parser.ast.INT, parser.ast.INT);
	}

}
