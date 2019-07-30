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

public class KeyFunction extends IniFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		return new RawData(eval.eval(params.get(0)).keyOf(eval.eval(params.get(1))));
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints,
			Invocation invocation) {
//		Type k = new Type(parser);
//		Type v = new Type(parser);
//		Type mkv = parser.ast.getDependentType("Map", k, v);
//		//constraints.add(new TypingConstraint(TypingConstraint.Kind.EQ,t1,t2,invocation,invocation));
//		return parser.ast.getFunctionalType(k, mkv, v);

		Type mkv = new Type(parser,"Map");
		Type k = new Type(parser);
		Type v = new Type(parser);
		mkv.addTypeParameter(k);
		mkv.addTypeParameter(v);
		Type functionType = new Type(parser,"function");
		functionType.setReturnType(k);
		functionType.addTypeParameter(mkv);
		functionType.addTypeParameter(v);
		return functionType;

	}

}
