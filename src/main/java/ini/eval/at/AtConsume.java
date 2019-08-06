package ini.eval.at;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.errors.InterruptException;

import ini.broker.DefaultBrokerClient;
import ini.eval.IniEval;
import ini.eval.data.Data;

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
						
						//System.out.println("CCCC: "+d.getValue());
						
						DefaultBrokerClient.getInstance().consume(d.getValue(), value -> {
							Map<String, Data> variables = new HashMap<String, Data>();
							variables.put(getAtPredicate().outParameters.get(0).toString(),
									value);
							execute(eval, variables);
						});
						
						/*List<Data> values = KafkaClient.runConsumer(d.getValue());
						for(Data value : values) {
							Map<String, Data> variables = new HashMap<String, Data>();
							variables.put(getAtPredicate().outParameters.get(0).toString(),
									value);
							execute(eval, variables);
						}*/
					} catch (InterruptedException e) {
						break;
					} catch (InterruptException e) {
						break;
					}
				} while (!checkTerminated());
			}
		};
		mainThread.start();		

	}

	@Override
	public void terminate() {
		// TODO: interrupt properly 
		mainThread.interrupt();
		super.terminate();
	}

}
