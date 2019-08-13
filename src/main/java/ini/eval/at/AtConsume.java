package ini.eval.at;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.errors.InterruptException;

import ini.broker.BrokerClient;
import ini.eval.IniEval;
import ini.eval.data.Data;

public class AtConsume extends At {
	
	Thread mainThread;
	String channel;
	BrokerClient<Data> brokerClient;

	@Override
	public void eval(final IniEval eval) {

		mainThread = new Thread() {
			@Override
			public void run() {
				do {
					try {
						brokerClient = BrokerClient.createDefaultInstance(eval.parser);
						channel = getInContext().get("channel").getValue();
						brokerClient.consume(channel, value -> {
							Map<String, Data> variables = new HashMap<String, Data>();
							variables.put(getAtPredicate().outParameters.get(0).toString(),
									value);
							execute(eval, variables);
						});
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
		super.terminate();
		brokerClient.stopConsumer(channel);
		// TODO: interrupt properly 
		mainThread.interrupt();
	}

}
