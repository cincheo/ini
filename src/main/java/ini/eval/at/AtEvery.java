package ini.eval.at;

import ini.eval.IniEval;
import ini.eval.IniThread;
import ini.type.AstAttrib;

public class AtEvery extends At {

	Thread mainThread;
	IniThread ruleThread;

	@Override
	public void eval(final IniEval eval) {
		ruleThread = new IniThread(eval, this, getRule(), null);
		mainThread = new Thread() {
			@Override
			public void run() {
				do {
					try {
						sleep(getInContext().get("time").getNumber().longValue());
					} catch (InterruptedException e) {
						break;
					}
					execute(ruleThread);
				} while (!checkTerminated());
			}
		};
		mainThread.start();
	}

	@Override
	public void evalType(AstAttrib attrib) {
		typeInParameters(attrib, true, attrib.parser.types.INT, "time");
	}
	
	
}
