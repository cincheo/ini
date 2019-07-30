package ini.eval.function;

import ini.ast.Expression;
import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IniFunction {

	public static Map<String,IniFunction> functions=new HashMap<String, IniFunction>();

	static {
		functions.put("rest", new RestFunction());
		functions.put("first", new FirstFunction());
		functions.put("min", new MinFunction());
		functions.put("max", new MaxFunction());
		functions.put("key", new KeyFunction());
		functions.put("sqrt", new SqrtFunction());
		functions.put("kill", new KillFunction());
		functions.put("sleep", new SleepFunction());
		functions.put("array", new ArrayFunction());
		functions.put("eval", new EvalFunction());
		functions.put("eval_string", new EvalStringFunction());
		functions.put("error", new ErrorFunction());
		functions.put("to_char", new ToCharFunction());
		functions.put("to_string", new ToStringFunction());
		functions.put("to_byte", new ToByteFunction());
		functions.put("to_int", new ToIntFunction());
		functions.put("to_float", new ToFloatFunction());
		functions.put("to_double", new ToDoubleFunction());
		functions.put("parse_number", new ParseNumberFunction());
		functions.put("to_long", new ToLongFunction());
		functions.put("start_web_service", new StartWebServiceFunction());
		functions.put("clear", new ClearFunction());
		functions.put("swap", new SwapFunction());
		functions.put("copy", new CopyFunction());
		functions.put("time", new TimeFunction());
		functions.put("pow", new PowFunction());
		functions.put("print", new PrintFunction());
		functions.put("println", new PrintlnFunction());
		functions.put("size", new SizeFunction());
		functions.put("read_keyboard", new ReadKeyboardFunction());
		functions.put("zip", new ZipFunction());
		functions.put("upload_ftp", new UploadFTPFunction());
		functions.put("stop_event", new KillAt());
		functions.put("restart_event", new RestartAtFunction());
		functions.put("reconfigure", new ReconfigureAtFunction());
		functions.put("to_json", new ToJsonFunction());
		functions.put("produce", new ProduceFunction());
	}
	
	public abstract Data eval(IniEval eval, List<Expression> params);
	
	public abstract Type getType(IniParser parser, List<TypingConstraint> constraints, Invocation invocation);
	
}
