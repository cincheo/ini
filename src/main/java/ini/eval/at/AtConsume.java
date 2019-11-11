package ini.eval.at;

import java.util.HashMap;
import java.util.Map;

import ini.Main;
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
	Integer stop;
	int stopCount = 0;
	String consumerId;

	@Override
	public void eval(final IniEval eval) {
		ruleThread = new IniThread(eval, this, getRule(), null);
		channel = (getInContext().get("channel") == null ? getInContext().get("from")
				: getInContext().get("channel")).getValue();
		brokerClient = BrokerClient.getDefaultInstance(eval.parser.env,
				channel.visibility == Visibility.GLOBAL);
		stop = getInContext().get("stop")==null?1:getInContext().get("stop").getValue();
		consumerId = brokerClient.consume(channel.mappedName, value -> {
			if (Channel.STOP_MESSAGE.equals(value)) {
				stopCount++;
				if(stop != 0 && stopCount >= stop) {
					AtConsume.this.isEmptyQueue();
					Main.LOGGER.debug("stopping " + AtConsume.this);
					AtConsume.this.terminate();
				}
			} else {
				Map<String, Data> variables = new HashMap<String, Data>();
				if (!getAtPredicate().outParameters.isEmpty()) {
					variables.put(getAtPredicate().outParameters.get(0).toString(), value);
				}
				Main.LOGGER.debug("starting event thread for " + AtConsume.this);
				execute(ruleThread.fork(variables));
			}
		});
		
		/*
		mainThread = new Thread() {
			@Override
			public void run() {
						channel = (getInContext().get("channel") == null ? getInContext().get("from")
								: getInContext().get("channel")).getValue();
						brokerClient = BrokerClient.getDefaultInstance(eval.parser.env,
								channel.visibility == Visibility.GLOBAL);
						stop = getInContext().get("stop")==null?1:getInContext().get("stop").getValue();
						consumerId = brokerClient.consume(channel.mappedName, value -> {
							if (Channel.STOP_MESSAGE.equals(value)) {
								stopCount++;
								if(stop != 0 && stopCount >= stop) {
									AtConsume.this.isEmptyQueue();
									Main.LOGGER.debug("stopping " + AtConsume.this);
									AtConsume.this.terminate();
								}
							} else {
								Map<String, Data> variables = new HashMap<String, Data>();
								if (!getAtPredicate().outParameters.isEmpty()) {
									variables.put(getAtPredicate().outParameters.get(0).toString(), value);
								}
								Main.LOGGER.debug("starting event thread for " + AtConsume.this);
								execute(ruleThread.fork(variables));
							}
						});
						do {
							try {

					} catch (InterruptException e) {
						break;
					}
				} while (!checkTerminated());
			}
		};
		mainThread.start();
*/
		
	}

	@Override
	public void terminate() {
		super.terminate();
		brokerClient.stopConsumer(consumerId);
		// TODO: interrupt properly
		//mainThread.interrupt();
	}

	@Override
	public void evalType(AstAttrib attrib) {
		typeInParameters(attrib, true,
				attrib.parser.types.getDependentType("Channel", attrib.parser.types.createType()), "from", "channel");
	}

}
