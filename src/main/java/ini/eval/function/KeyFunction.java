package ini.eval.function;

import ini.eval.IniEval;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;
import ini.type.TypingConstraint.Kind;

public class KeyFunction extends BuiltInExecutable {

	public KeyFunction(IniParser parser) {
		super(parser, "key", "dictData", "data");
	}

	@Override
	public void eval(IniEval eval) {
		eval.result = new RawData(getArgument(eval, 0).keyOf(getArgument(eval, 1)));
	}

	@Override
	protected void buildTypingConstraints() {
		Type k = parser.types.createType();
		Type v = parser.types.createType();
		Type mkv = parser.types.createMapType(k, v);
		addTypingConstraint(Kind.EQ, getParameterType(0), mkv);
		addTypingConstraint(Kind.EQ, getParameterType(1), v);
		addTypingConstraint(Kind.EQ, getReturnType(), k);
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib) {
		Type mkv = new Type(attrib.parser.types, "Map");
		Type k = new Type(attrib.parser.types);
		Type v = new Type(attrib.parser.types);
		mkv.addTypeParameter(k);
		mkv.addTypeParameter(v);
		Type functionType = new Type(attrib.parser.types, "function");
		functionType.setReturnType(k);
		functionType.addTypeParameter(mkv);
		functionType.addTypeParameter(v);
		return functionType;

	}

}
