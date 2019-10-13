package ini.eval.function;

import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class PrintFunction extends BuiltInExecutable {

	public PrintFunction(IniParser parser) {
		super(parser, "print", "data");
	}

	@Override
	public void eval(IniEval eval) {
		Data d = getArgument(eval, 0);
		if(d==null) {
			eval.parser.out.print("null");
		} else {
			d.prettyPrint(eval.parser.out);
		}
		eval.result = null;
	}
	
	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		return attrib.parser.types.createFunctionalType(attrib.parser.types.VOID, attrib.parser.types.ANY);
	}
	
	
}
