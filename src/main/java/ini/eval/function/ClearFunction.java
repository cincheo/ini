package ini.eval.function;

import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class ClearFunction extends BuiltInExecutable {

	public ClearFunction(IniParser parser) {
		super(parser, "clear", "data");
	}

	@Override
	public void eval(IniEval eval) {
		Data d = getArgument(eval, 0);
		d.setValue(null);
		d.setReferences(null);
		eval.result = d;
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		Type t = attrib.parser.types.createType();
		return attrib.parser.types.createFunctionalType(t, t);
	}

}
