package ini.eval.function;

import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;
import ini.type.TypingConstraint.Kind;

public class ErrorFunction extends BuiltInExecutable {

	public ErrorFunction(IniParser parser) {
		super(parser, "error", "message");
	}
	
	@Override
	public void eval(IniEval eval) {
		Data d = getArgument(eval, 0);
		throw new RuntimeException(d.toPrettyString());
	}

	@Override
	protected void buildTypingConstraints() {
		addTypingConstraint(Kind.EQ, getReturnType(), parser.types.VOID, this);
		addTypingConstraint(Kind.EQ, getParameterType(0), parser.types.STRING, this.parameters.get(0));
	}
	
	@Override
	public Type getFunctionalType(AstAttrib attrib) {
		return attrib.parser.types.createFunctionalType(attrib.parser.types.VOID, attrib.parser.types.STRING);
	}
	
	
}
