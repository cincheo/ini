package ini.eval.function;

import ini.eval.IniEval;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;
import ini.type.TypingConstraint.Kind;

public class ToStringFunction extends BuiltInExecutable {

	public ToStringFunction(IniParser parser) {
		super(parser, "to_string", "data");
	}

	@Override
	public void eval(IniEval eval) {
		eval.result = new RawData(getArgument(eval, 0).toPrettyString());
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib) {
		return attrib.parser.types.createFunctionalType(attrib.parser.types.STRING, attrib.parser.types.ANY);
	}

	@Override
	protected void buildTypingConstraints() {
		addTypingConstraint(Kind.EQ, getReturnType(), parser.types.STRING);
	}

}
