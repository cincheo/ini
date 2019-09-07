package ini.eval.function;

import ini.eval.IniEval;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class ToCharFunction extends BuiltInExecutable {

	public ToCharFunction(IniParser parser) {
		super(parser, "to_char", "data");
	}

	@Override
	public void eval(IniEval eval) {
		eval.result = new RawData((char)getArgument(eval, 0).getNumber().byteValue());
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib) {
		return attrib.parser.types.createFunctionalType(attrib.parser.types.CHAR, attrib.parser.types.ANY);
	}	

}
