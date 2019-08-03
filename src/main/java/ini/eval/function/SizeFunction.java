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

public class SizeFunction extends IniFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		return new RawData(eval.eval(params.get(0)).getSize());
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints, Invocation invocation) {
		Type l = new Type(parser,"Map");
		Type t = new Type(parser);
		l.addTypeParameter(parser.ast.INT);
		l.addTypeParameter(t);
		return parser.ast.getFunctionalType(parser.ast.INT, l);
	}
	
}
