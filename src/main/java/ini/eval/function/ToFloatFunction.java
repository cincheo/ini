package ini.eval.function;

import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class ToFloatFunction extends BuiltInExecutable {

	public ToFloatFunction(IniParser parser) {
		super(parser, "to_float", "data");
	}

	@Override
	public void eval(IniEval eval) {
		eval.result = new RawData(getArgument(eval, 0).getNumber().floatValue());
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		return attrib.parser.types.createFunctionalType(attrib.parser.types.FLOAT, attrib.parser.types.ANY);
	}	
	
}
