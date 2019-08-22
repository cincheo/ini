package ini.eval.function;

import java.util.List;

import ini.ast.Expression;
import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

public class WaitFunction extends IniFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		return eval.eval(params.get(0)).getIfAvailable();
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints, Invocation invocation) {
		Type functionType = new Type(parser, "function");
		Type t1 = new Type(parser);
		Type t2 = new Type(parser);
		functionType.setReturnType(t1);
		functionType.addTypeParameter(t2);
		constraints.add(new TypingConstraint(TypingConstraint.Kind.EQ, t1, t2, invocation, invocation));
		return functionType;
	}

}
