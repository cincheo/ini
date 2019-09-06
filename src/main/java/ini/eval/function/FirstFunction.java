package ini.eval.function;

import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class FirstFunction extends BuiltInExecutable {

	public FirstFunction(IniParser parser) {
		super(parser, "first", "arrayData");
	}

	@Override
	public void eval(IniEval eval) {
		Data d = getArgument(eval, 0);
		if (d.getSize() < 1) {
			eval.result = new RawData(null);
		} else {
			eval.result = d.first();
		}
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib) {
		Type t = attrib.parser.types.createType();
		return attrib.parser.types.createFunctionalType(t, attrib.parser.types.createArrayType(t));
	}

}
