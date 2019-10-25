package ini.eval.function;

import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class NodeFunction extends BuiltInExecutable {

	public NodeFunction(IniParser parser) {
		super(parser, "node");
	}

	@Override
	public void eval(IniEval eval) {
		eval.result = new RawData(eval.parser.env.node);
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		return parser.types.createFunctionalType(parser.types.STRING);
	}

}
