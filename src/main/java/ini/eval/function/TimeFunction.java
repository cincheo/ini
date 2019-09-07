package ini.eval.function;

import ini.eval.IniEval;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class TimeFunction extends BuiltInExecutable {

	public TimeFunction(IniParser parser) {
		super(parser, "time");
	}

	@Override
	public void eval(IniEval eval) {
		eval.result = new RawData((long)System.currentTimeMillis());
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib) {
		return attrib.parser.types.createFunctionalType(attrib.parser.types.LONG);
	}
	
}
