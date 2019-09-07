package ini.eval.function;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ini.ast.Expression;
import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

// TODO: merge with Ast.Executable
public abstract class IniFunction {

	public static Map<String,IniFunction> functions=new HashMap<String, IniFunction>();

	static {
		//functions.put("rest", new RestFunction()); //B
		//functions.put("first", new FirstFunction()); //B
		//functions.put("min", new MinFunction()); //M 
		//functions.put("max", new MaxFunction()); //M
		//functions.put("key", new KeyFunction()); //B
		//functions.put("sqrt", new SqrtFunction()); //M
		functions.put("kill", new KillFunction());
		//functions.put("sleep", new SleepFunction()); //B
		functions.put("array", new ArrayFunction());
		functions.put("eval", new EvalFunction()); //??
		functions.put("eval_string", new EvalStringFunction()); //??
		//functions.put("error", new ErrorFunction()); //B
		//functions.put("to_char", new ToCharFunction());
		//functions.put("to_string", new ToStringFunction());
		//functions.put("to_byte", new ToByteFunction());
		//functions.put("to_int", new ToIntFunction());
		//functions.put("to_float", new ToFloatFunction());
		//functions.put("to_double", new ToDoubleFunction());
		functions.put("parse_number", new ParseNumberFunction());
		//functions.put("to_long", new ToLongFunction());
		functions.put("start_web_service", new StartWebServiceFunction()); //??
		functions.put("stop_web_service", new StopWebServiceFunction()); //??
		//functions.put("clear", new ClearFunction()); //B
		//functions.put("swap", new SwapFunction()); //B
		//functions.put("copy", new CopyFunction()); //B
		//functions.put("time", new TimeFunction());
		//functions.put("mod", new ModFunction()); //M
		//functions.put("pow", new PowFunction()); //M
		//functions.put("size", new SizeFunction()); //B
		//functions.put("read_keyboard", new ReadKeyboardFunction()); //B
		functions.put("zip", new ZipFunction()); //IO
		functions.put("upload_ftp", new UploadFTPFunction()); //NET
		functions.put("stop", new KillAt());
		functions.put("restart", new RestartAtFunction()); 
		functions.put("reconfigure", new ReconfigureAtFunction()); //B
		functions.put("to_json", new ToJsonFunction()); //B
		//functions.put("produce", new ProduceFunction()); //B
		//functions.put("wait", new WaitFunction());
	}
	
	public abstract Data eval(IniEval eval, List<Expression> params);
	
	public abstract Type getType(IniParser parser, List<TypingConstraint> constraints, Invocation invocation);
	
}
