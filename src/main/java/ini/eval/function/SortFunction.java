package ini.eval.function;

import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class SortFunction extends BuiltInExecutable {

	public SortFunction(IniParser parser) {
		super(parser, "sort", "arrayData");
	}

	@Override
	public void eval(IniEval eval) {
		// TODO
		//eval.result = getArgument(eval, 0)..getSize());
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		Type t = new Type(attrib.parser.types);
		return attrib.parser.types.createFunctionalType(attrib.parser.types.INT, attrib.parser.types.createArrayType(t));
	}

}
