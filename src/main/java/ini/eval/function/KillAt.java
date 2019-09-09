package ini.eval.function;

import ini.eval.IniEval;
import ini.eval.at.At;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class KillAt extends BuiltInExecutable {

	public KillAt(IniParser parser) {
		super(parser, "stop", "name");
	}
	
	@Override
	public void eval(IniEval eval) {
		At at = (At)getArgument(eval, 0).getValue();
		at.terminate();
		eval.result = null;
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib) {
		return parser.types.createFunctionalType(parser.types.VOID, parser.types.THREAD);
	}
	
}
