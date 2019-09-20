package ini.eval.at;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.errors.InterruptException;

import ini.ast.Channel;
import ini.ast.Channel.Visibility;
import ini.broker.BrokerClient;
import ini.eval.IniEval;
import ini.eval.IniThread;
import ini.eval.data.Data;

public class AtConsume extends At {

	Thread mainThread;
	Channel channel;
	BrokerClient<Data> brokerClient;
	IniThread ruleThread;

	@Override
	public void eval(final IniEval eval) {
		ruleThread = new IniThread(eval, this, getRule());
		mainThread = new Thread() {
			@Override
			public void run() {
				do {
					try {
						channel = getInContext().get("channel").getValue();
						brokerClient = BrokerClient.createDefaultInstance(eval.parser.env, channel.visibility==Visibility.GLOBAL);
						brokerClient.consume(channel.mappedName, value -> {
							Map<String, Data> variables = new HashMap<String, Data>();
							variables.put(getAtPredicate().outParameters.get(0).toString(), value);
							execute(ruleThread.setVariables(variables));
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
		brokerClient.stopConsumer(channel.mappedName);
		// TODO: interrupt properly
		mainThread.interrupt();
	}
	
}
