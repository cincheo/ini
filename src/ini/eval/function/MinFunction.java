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

public class MinFunction extends IniFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		return new RawData(Math.min(eval.eval(params.get(0)).getNumber().doubleValue(), eval
				.eval(params.get(1)).getNumber().doubleValue()));
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints, Invocation invocation) {
		return parser.ast.getFunctionalType(parser.ast.FLOAT, parser.ast.FLOAT, parser.ast.FLOAT);
	}

	
}
