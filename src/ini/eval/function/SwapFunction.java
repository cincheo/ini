package ini.eval.function;

import ini.ast.Expression;
import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

import java.util.List;
import java.util.Map;

public class SwapFunction extends IniFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		Data d1 = eval.eval(params.get(0));
		if(d1.isUndefined()) return null;
		Data d2 = eval.eval(params.get(1));
		if(d2.isUndefined()) return null;
		Object o = d1.getValue();
		d1.setValue(d2.getValue());
		d2.setValue(o);
		Map<Object,Data> refs = d1.getReferences();
		d1.setReferences(d2.getReferences());
		d2.setReferences(refs);
		return null;
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints, Invocation invocation) {
		Type t = new Type(parser);
		return parser.ast.getFunctionalType(parser.ast.VOID, t, t);
	}
	
}
