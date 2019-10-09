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
import ini.type.AstAttrib;

public class AtConsume extends At {

	Thread mainThread;
	Channel channel;
	BrokerClient<Data> brokerClient;
	IniThread ruleThread;

	@Override
	public void eval(final IniEval eval) {
		ruleThread = new IniThread(eval, this, getRule(), null);
		mainThread = new Thread() {
			@Override
			public void run() {
				do {
					try {
						channel = (getInContext().get("channel") == null ? getInContext().get("from")
								: getInContext().get("channel")).getValue();
						brokerClient = BrokerClient.createDefaultInstance(eval.parser.env,
								channel.visibility == Visibility.GLOBAL);
						brokerClient.consume(channel.mappedName, value -> {
							Map<String, Data> variables = new HashMap<String, Data>();
							variables.put(getAtPredicate().outParameters.get(0).toString(), value);
							//System.err.println(">>> "+getAtPredicate().outParameters.get(0).toString()+": "+value);
							execute(ruleThread.fork(variables));
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

	@Override
	public void evalType(AstAttrib attrib) {
		typeInParameters(attrib, true,
				attrib.parser.types.getDependentType("Channel", attrib.parser.types.createType()), "from", "channel");
	}

}
