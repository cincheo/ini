package ini.eval.function;

import ini.eval.IniEval;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class ToByteFunction extends BuiltInExecutable {

	public ToByteFunction(IniParser parser) {
		super(parser, "to_byte", "data");
	}

	@Override
	public void eval(IniEval eval) {
		eval.result = new RawData(getArgument(eval, 0).getNumber().byteValue());
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib) {
		return attrib.parser.types.createFunctionalType(attrib.parser.types.BYTE, attrib.parser.types.ANY);
	}

}
