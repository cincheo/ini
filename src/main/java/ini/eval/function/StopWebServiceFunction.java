package ini.eval.function;

import java.util.List;

import org.eclipse.jetty.server.Server;

import ini.ast.Expression;
import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

public class StopWebServiceFunction extends IniFunction {

	@Override
	public Data eval(final IniEval eval, final List<Expression> params) {
		Server server = StartWebServiceFunction.servers.get(eval.eval(params.get(0)).getNumber().intValue());
		try {
			server.stop();
		} catch (Exception e) {
			// swallow
		}
		return null;
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints, Invocation invocation) {
		return parser.ast.getFunctionalType(parser.ast.VOID, parser.ast.INT);
	}

}
