package ini.eval.function;

import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class ErrorFunction extends BuiltInExecutable {

	public ErrorFunction(IniParser parser) {
		super(parser, "error", "message");
	}
	
	@Override
	public void eval(IniEval eval) {
		Data d = getArgument(eval, 0);
		throw new RuntimeException(d.toPrettyString());
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		return attrib.parser.types.createFunctionalType(attrib.parser.types.VOID, attrib.parser.types.STRING);
	}
	
	
}
