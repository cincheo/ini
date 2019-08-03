package ini.eval.function;

import java.util.List;

import ini.ast.Expression;
import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

public class ToJsonFunction extends IniFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		Data data = eval.eval(params.get(0));
		return new RawData(data.toJson());
	}

	@Override
	public Type getType(IniParser parser,
			List<TypingConstraint> constraints, Invocation invocation) {
		return parser.ast.getFunctionalType(parser.ast.STRING, parser.ast.ANY);
	}
	
}
