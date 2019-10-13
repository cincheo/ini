package ini.eval.function;

import java.util.ArrayList;

import ini.ast.Executable;
import ini.ast.Parameter;
import ini.ast.Visitor;
import ini.parser.IniParser;

public abstract class BuiltInExecutable extends Executable {

	public BuiltInExecutable(IniParser parser, String name, String... parameterNames) {
		super(parser, null, name, null);
		parameters = new ArrayList<Parameter>();
		for (String parameterName : parameterNames) {
			parameters.add(new Parameter(null, null, parameterName));
		}
	}
	
	@Override
	public void accept(Visitor visitor) {
		// ignore
	}
	
}
