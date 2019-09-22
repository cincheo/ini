package ini.eval.at;

import ini.ast.AtBinding;
import ini.eval.IniEval;
import ini.type.AstAttrib;

public class BoundJavaAt extends At {

	public AtBinding binding;
	public At userAt;

	@Override
	public void eval(IniEval eval) {
		userAt.eval(eval);
	}

	public BoundJavaAt(AtBinding binding) {
		this.binding = binding;
		try {
			userAt = (At)Class.forName(binding.className).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean checkTerminated() {
		return terminated;
	}

	@Override
	public void evalType(AstAttrib attrib) {
		// TODO
	}
	
}
