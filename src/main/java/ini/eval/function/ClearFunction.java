package ini.eval.function;

import ini.ast.Expression;
import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

import java.util.List;

public class ClearFunction extends IniFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		Data d = eval.eval(params.get(0));
		d.setValue(null);
		d.setReferences(null);
		return null;
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints, Invocation invocation) {
		return parser.ast.ANY;
	}
	
}
