package ini.eval.function;

import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class AnyFunction extends BuiltInExecutable {

	public AnyFunction(IniParser parser) {
		super(parser, "any");
	}

	@Override
	public void eval(IniEval eval) {
		Data d = new RawData();
		eval.result = d;
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		return attrib.parser.types.createFunctionalType(parser.types.ANY);
	}

}
