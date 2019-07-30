package ini.eval.at;

import ini.eval.IniEval;

public class AtEvery extends At {

	Thread mainThread;

	@Override
	public void eval(final IniEval eval) {
		
		mainThread = new Thread() {
			@Override
			public void run() {
				do {
					try {
						sleep(getInContext().get("time").getNumber()
								.longValue());
					} catch (InterruptedException e) {
						//System.out.println("INTERUPTED");
						break;
					}
					execute(eval, null);
				} while (!checkTerminated());
			}
		};
		mainThread.start();
	}

	@Override
	public void terminate() {
		super.terminate();
		mainThread.interrupt();
		//mainThread = null;
	}

}
