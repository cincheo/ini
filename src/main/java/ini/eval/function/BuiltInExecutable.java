package ini.eval.function;

import java.util.ArrayList;

import ini.ast.Executable;
import ini.ast.Parameter;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint.Kind;

public abstract class BuiltInExecutable extends Executable {

	public BuiltInExecutable(IniParser parser, String name, String... parameterNames) {
		super(parser, null, name, null);
		parameters = new ArrayList<Parameter>();
		for (String parameterName : parameterNames) {
			parameters.add(new Parameter(null, null, parameterName));
		}
	}
	
	protected final void addTypingConstraint(Kind kind, Type leftType, Type rightType) {
		getType().addTypingConstraint(kind, leftType, rightType, this);
	}
	
}
