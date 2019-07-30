package ini.eval.function;

import ini.ast.Expression;
import ini.ast.Invocation;
import ini.ast.ListExpression;
import ini.eval.IniEval;
import ini.eval.at.At;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

import java.util.List;

public class ReconfigureAtFunction extends IniFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		At at = (At)eval.eval(params.get(0)).getValue();
		at.terminate();
		//System.out.println(((ListExpression)params.get(1)).elements + ">>>>>"+ params.get(1).getClass());
		at.parseInParameters(eval, ((ListExpression)params.get(1)).elements);
		at.restart(eval);
		return null;
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints,
			Invocation invocation) {
		return parser.ast.getFunctionalType(parser.ast.VOID);
	}
}