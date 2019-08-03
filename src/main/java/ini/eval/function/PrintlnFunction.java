package ini.eval.function;

import ini.ast.Expression;
import ini.eval.IniEval;
import ini.eval.data.Data;

import java.util.List;

public class PrintlnFunction extends PrintFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		super.eval(eval, params);
		eval.parser.out.println();
		return null;
	}
	
}
