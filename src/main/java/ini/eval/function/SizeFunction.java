package ini.eval.function;

import ini.eval.IniEval;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class SizeFunction extends BuiltInExecutable {

	public SizeFunction(IniParser parser) {
		super(parser, "size", "arrayData");
	}

	@Override
	public void eval(IniEval eval) {
		eval.result = new RawData(getArgument(eval, 0).getSize());
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib) {
		Type t = new Type(attrib.parser.types);
		return attrib.parser.types.createFunctionalType(attrib.parser.types.INT, attrib.parser.types.createArrayType(t));
	}

}
