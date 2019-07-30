package ini.eval.function;

import ini.ast.Expression;
import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ReadKeyboardFunction extends IniFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			return new RawData(in.readLine());
		} catch (IOException e) {
		} 
		return new RawData(null);
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints, Invocation invocation) {
		return parser.ast.getFunctionalType(parser.ast.STRING);
	}
	
	
}
