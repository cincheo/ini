package ini.eval.function;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import ini.ast.Executable;
import ini.eval.IniEval;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class StartWebServiceFunction extends BuiltInExecutable {

	public static Map<Integer, Server> servers = new HashMap<>();

	public StartWebServiceFunction(IniParser parser) {
		super(parser, "start_web_service", "port", "handler");
	}

	@Override
	public void eval(IniEval eval) {
		final int port = getArgument(eval, 0).getNumber().intValue();
		final Handler handler = new Handler(eval.fork(), getArgument(eval, 1).getValue());
		new Thread() {
			@Override
			public void run() {
				Server server = new Server(port);
				servers.put(port, server);
				server.setHandler(handler);
				try {
					server.start();
					server.join();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		eval.result = null;
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib) {
		return parser.types.createFunctionalType(parser.types.VOID, parser.types.INT,
				parser.types.createFunctionalType(parser.types.VOID, parser.types.STRING, parser.types.ANY));
	}

	public class Handler extends AbstractHandler {

		IniEval eval;
		Executable executable;

		public Handler(IniEval eval, Executable executable) {
			this.eval = eval;
			this.executable = executable;
		}

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
			response.setContentType("text/html;charset=utf-8");
			response.setStatus(HttpServletResponse.SC_OK);
			baseRequest.setHandled(true);
			// dispatch to ini function...
			eval.invoke(executable, new Object[] { baseRequest.getPathInfo(), response.getWriter() });
		}

	}

}
