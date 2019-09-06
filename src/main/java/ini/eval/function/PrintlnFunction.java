package ini.eval.function;

import ini.eval.IniEval;
import ini.parser.IniParser;

public class PrintlnFunction extends PrintFunction {

	public PrintlnFunction(IniParser parser) {
		super(parser);
		this.name = "println";
	}
	
	@Override
	public void eval(IniEval eval) {
		super.eval(eval);
		eval.parser.out.println();
	}
	
}
