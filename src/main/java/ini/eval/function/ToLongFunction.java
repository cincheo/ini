package ini.eval.function;

import ini.eval.IniEval;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class ToLongFunction extends BuiltInExecutable {

	public ToLongFunction(IniParser parser) {
		super(parser, "to_long", "data");
	}

	@Override
	public void eval(IniEval eval) {
		eval.result = new RawData(getArgument(eval, 0).getNumber().longValue());
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib) {
		return attrib.parser.types.createFunctionalType(attrib.parser.types.LONG, attrib.parser.types.ANY);
	}

}
