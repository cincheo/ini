package ini.eval.function;

import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;
import ini.type.TypingConstraint.Kind;

public class ClearFunction extends BuiltInExecutable {

	public ClearFunction(IniParser parser) {
		super(parser, "clear", "data");
	}

	@Override
	public void eval(IniEval eval) {
		Data d = getArgument(eval, 0);
		d.setValue(null);
		d.setReferences(null);
		eval.result = d;
	}

	@Override
	protected void buildTypingConstraints() {
		addTypingConstraint(Kind.EQ, getParameterType(0), getReturnType());
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib) {
		Type t = attrib.parser.types.createType();
		return attrib.parser.types.createFunctionalType(t, t);
	}

}
