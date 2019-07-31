package ini.eval.at;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ini.broker.KafkaClient;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.eval.data.RawData;

public class AtConsume extends At {
	
	Thread mainThread;

	@Override
	public void eval(final IniEval eval) {

		mainThread = new Thread() {
			@Override
			public void run() {
				do {
					try {
						Data d = getInContext().get("channel");
						List<Data> values = KafkaClient.runConsumer(d.getValue());
						for(Data value : values) {
							Map<String, Data> variables = new HashMap<String, Data>();
							variables.put(getAtPredicate().outParameters.get(0).toString(),
									value);
							execute(eval, variables);
						}
					} catch (InterruptedException e) {
						//System.out.println("INTERUPTED");
						break;
					}
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
