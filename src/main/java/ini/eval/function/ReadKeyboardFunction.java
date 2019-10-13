package ini.eval.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class ReadKeyboardFunction extends BuiltInExecutable {

	public ReadKeyboardFunction(IniParser parser) {
		super(parser, "read_keyboard");
	}

	@Override
	public void eval(IniEval eval) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			eval.result = new RawData(in.readLine());
		} catch (IOException e) {
		} 
		eval.result = new RawData(null);
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		return parser.types.createFunctionalType(parser.types.STRING);
	}
	
	
}
