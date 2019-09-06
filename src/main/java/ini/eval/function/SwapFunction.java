package ini.eval.function;

import java.util.Map;

import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class SwapFunction extends BuiltInExecutable {

	public SwapFunction(IniParser parser) {
		super(parser, "swap", "data1", "data2");
	}
	
	@Override
	public void eval(IniEval eval) {
		eval.result = null;
		Data d1 = getArgument(eval, 0);
		if(d1.isUndefined()) return;
		Data d2 = getArgument(eval, 1);
		if(d2.isUndefined()) return;
		Object o = d1.getValue();
		d1.setValue(d2.getValue());
		d2.setValue(o);
		Map<Object,Data> refs = d1.getReferences();
		d1.setReferences(d2.getReferences());
		d2.setReferences(refs);
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib) {
		Type t = new Type(parser.types);
		return parser.types.createFunctionalType(parser.types.VOID, t, t);
	}

	
}
