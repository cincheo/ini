package ini.eval.at;

import ini.eval.IniEval;
import ini.eval.IniThread;
import ini.eval.data.Data;
import it.sauronsoftware.cron4j.Scheduler;

public class AtCron extends At {

	@Override
	public void eval(IniEval eval) {
		if (getAtPredicate().inParameters.size() != 1) {
			throw new RuntimeException(
					"wrong number of arguments in @cron (expecting one cron expression as a string)");
		}
		//final Data d = eval.eval(getAtPredicate().inParameters.get(0));
		String pattern = getInContext().get("pattern").getValue();
		Scheduler s = new Scheduler();
		s.schedule(pattern, new IniThread(eval, this, getRule()));
		s.start();
	}

	@Override
	public boolean checkTerminated() {
		return terminated;
	}

}
