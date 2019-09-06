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

public class ToByteFunction extends IniFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		return new RawData(eval.eval(params.get(0)).getNumber().byteValue());
	}

	@Override
	public Type getType(IniParser parser,
			List<TypingConstraint> constraints, Invocation invocation) {
		Type t = new Type(parser.types);
		constraints.add(new TypingConstraint(TypingConstraint.Kind.LTE, t,
				parser.types.DOUBLE, invocation, invocation));
		return parser.types.createFunctionalType(parser.types.BYTE, t);
	}
	
}
