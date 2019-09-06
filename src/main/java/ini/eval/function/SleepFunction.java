package ini.eval.function;

import ini.eval.IniEval;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class SleepFunction extends BuiltInExecutable {

	public SleepFunction(IniParser parser) {
		super(parser, "sleep", "duration");
	}

	@Override
	public void eval(IniEval eval) {
		try {
			Thread.sleep((Integer) getArgument(eval, 0).getValue());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib) {
		return attrib.parser.types.createFunctionalType(attrib.parser.types.VOID, attrib.parser.types.INT);
	}

}
