package ini.eval.function;

import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class ToStringFunction extends BuiltInExecutable {

	public ToStringFunction(IniParser parser) {
		super(parser, "to_string", "data");
	}

	@Override
	public void eval(IniEval eval) {
		eval.result = new RawData(getArgument(eval, 0).toPrettyString());
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		return attrib.parser.types.createFunctionalType(attrib.parser.types.STRING, attrib.parser.types.ANY);
	}

}
