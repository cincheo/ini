package ini.eval.function;

import ini.ast.Expression;
import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

import java.util.List;

public class SleepFunction extends IniFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		try {
			Thread.sleep((Integer)eval.eval(params.get(0)).getValue());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Override
	public Type getType(IniParser parser,
			List<TypingConstraint> constraints, Invocation invocation) {
		return parser.ast.getFunctionalType(parser.ast.VOID, parser.ast.INT);
	}

}
