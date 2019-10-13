package ini.eval.function;

import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class ExitFunction extends BuiltInExecutable {

	public ExitFunction(IniParser parser) {
		super(parser, "exit");
	}

	@Override
	public void eval(IniEval eval) {
		System.exit(1);
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		return attrib.parser.types.createFunctionalType(parser.types.ANY);
	}

}
