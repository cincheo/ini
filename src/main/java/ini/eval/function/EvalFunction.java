package ini.eval.function;

import ini.ast.Expression;
import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

import java.util.List;

public class EvalFunction extends IniFunction {

	@Override
	public Data eval(final IniEval eval, final List<Expression> params) {
		String function = eval.eval(params.get(0)).getValue();
		List<Expression> args = params.subList(1, params.size());
		Invocation i = new Invocation(null,eval.evaluationStack.peek().token(),function,args);
		return eval.eval(i);
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints, Invocation invocation) {
		return parser.types.ANY;
	}

	
}
