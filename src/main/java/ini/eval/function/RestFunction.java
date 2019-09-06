package ini.eval.function;

import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;
import ini.type.TypingConstraint.Kind;

public class RestFunction extends BuiltInExecutable {

	public RestFunction(IniParser parser) {
		super(parser, "rest", "arrayData");
	}

	@Override
	public void eval(IniEval eval) {
		Data d = getArgument(eval, 0);
		if (d.getSize() < 2) {
			eval.result = new RawData(null);
		} else {
			eval.result = d.subArray(1, d.getSize() - 1);
		}
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib) {
		Type t = attrib.parser.types.createType();
		return attrib.parser.types.createFunctionalType(attrib.parser.types.createArrayType(t),
				attrib.parser.types.createArrayType(t));
	}

	@Override
	protected void buildTypingConstraints() {
		Type t = parser.types.createType();
		addTypingConstraint(Kind.EQ, getParameterType(0), parser.types.createArrayType(t));
		addTypingConstraint(Kind.EQ, getReturnType(), parser.types.createArrayType(t));
	}
	
}
