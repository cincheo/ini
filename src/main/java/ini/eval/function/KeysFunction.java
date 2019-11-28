package ini.eval.function;

import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class KeysFunction extends BuiltInExecutable {

	public KeysFunction(IniParser parser) {
		super(parser, "keys", "dictData");
	}

	@Override
	public void eval(IniEval eval) {
		eval.result = new RawData(getArgument(eval, 0).getReferences().keySet());
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		Type k = new Type(attrib.parser.types);
		Type v = new Type(attrib.parser.types);
		Type mkv = attrib.parser.types.createMapType(k, v);
		return attrib.parser.types.createFunctionalType(attrib.parser.types.createArrayType(k), mkv);
	}

}
