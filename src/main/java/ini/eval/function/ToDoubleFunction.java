package ini.eval.function;

import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class ToDoubleFunction extends BuiltInExecutable {

	public ToDoubleFunction(IniParser parser) {
		super(parser, "to_double", "data");
	}

	@Override
	public void eval(IniEval eval) {
		eval.result = new RawData(getArgument(eval, 0).getNumber().doubleValue());
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		return attrib.parser.types.createFunctionalType(attrib.parser.types.DOUBLE, attrib.parser.types.ANY);
	}	
	
}
