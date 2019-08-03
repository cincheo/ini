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

public class CopyFunction extends IniFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		Data d = new RawData(null);
		d.copyData(eval.eval(params.get(0)));
		return d;
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints, Invocation invocation) {
		Type functionType = new Type(parser,"function");
		Type t1 = new Type(parser);
		Type t2 = new Type(parser);
		functionType.setReturnType(t1);
		functionType.addTypeParameter(t2);
		constraints.add(new TypingConstraint(TypingConstraint.Kind.EQ,t1,t2,invocation,invocation));
		return functionType;
	}
	
}
