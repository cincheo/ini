package ini.eval.function;

import ini.ast.Expression;
import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class StartWebServiceFunction extends IniFunction {

	@Override
	public Data eval(final IniEval eval, final List<Expression> params) {
		new Thread() {
			@Override
			public void run() {
				Server server = new Server(eval.eval(params.get(0)).getNumber().intValue());
		        server.setHandler(new Handler(eval.fork(),eval.eval(params.get(1)).getValue().toString()));
		        try {
		        	server.start();
		        	server.join();
		        } catch (Exception e) {
		        	e.printStackTrace();
				}
			}
		}.start();
		return null;
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints, Invocation invocation) {
		return parser.ast.getFunctionalType(parser.ast.VOID, parser.ast.INT, parser.ast.getFunctionalType(parser.ast.VOID,parser.ast.STRING,parser.ast.ANY));
	}
	
	public class Handler extends AbstractHandler {

		IniEval eval;
		String function;

		public Handler(IniEval eval, String function) {
			this.eval=eval;
			this.function = function;
		}
		
		@Override
	    public void handle(String target,
	            Request baseRequest,
	            HttpServletRequest request,
	            HttpServletResponse response)
	            throws IOException, ServletException {
	        response.setContentType("text/html;charset=utf-8");
	        response.setStatus(HttpServletResponse.SC_OK);
	        baseRequest.setHandled(true);
	        // dispatch to ini function...
	        eval.invoke(function, baseRequest.getPathInfo(), response.getWriter());
	    }
		
		
	}
	
}
