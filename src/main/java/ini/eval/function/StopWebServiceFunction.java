package ini.eval.function;

import org.eclipse.jetty.server.Server;

import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class StopWebServiceFunction extends BuiltInExecutable {

	public StopWebServiceFunction(IniParser parser) {
		super(parser, "stop_web_service", "port");
	}

	@Override
	public void eval(final IniEval eval) {
		Server server = StartWebServiceFunction.servers.get(getArgument(eval, 0).getNumber().intValue());
		try {
			server.stop();
		} catch (Exception e) {
			// swallow
		}
		eval.result = null;
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		return parser.types.createFunctionalType(parser.types.VOID, parser.types.INT);
	}
	
}
