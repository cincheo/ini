package ini.eval.function;

import ini.ast.Expression;
import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

import java.util.List;

public class ArrayFunction extends IniFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		Data d = eval.eval(params.get(0));
		d.set(Data.UPPER_BOUND_KEY, eval.eval(params.get(1)));
		return d;
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints, Invocation invocation) {
		Type t = new Type(parser.types,"Map");
		t.addTypeParameter(parser.types.INT);
		t.addTypeParameter(new Type(parser.types));
		Type functionType = new Type(parser.types,"function");
		functionType.setReturnType(t);
		functionType.addTypeParameter(t);
		functionType.addTypeParameter(parser.types.INT);
		//constraints.add(new TypingConstraint(TypingConstraint.Kind.EQ,t1,t2,invocation,invocation));
		return functionType;
	}
	
}
