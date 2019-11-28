package ini.eval.at;

import java.util.HashMap;
import java.util.Map;

import ini.Main;
import ini.ast.ChannelDeclaration;
import ini.ast.ChannelDeclaration.Visibility;
import ini.broker.Channel;
import ini.broker.BrokerClient;
import ini.eval.IniEval;
import ini.eval.IniThread;
import ini.eval.data.Data;
import ini.parser.Types;
import ini.type.AstAttrib;

public class AtConsume extends At {

	Thread mainThread;
	ChannelDeclaration channel;
	BrokerClient brokerClient;
	IniThread ruleThread;
	Integer stop;
	int stopCount = 0;
	String consumerId;

	@Override
	public void eval(final IniEval eval) {
		ruleThread = new IniThread(eval, this, getRule(), null);
		channel = (getInContext().get("channel") == null ? getInContext().get("from") : getInContext().get("channel"))
				.getValue();
		brokerClient = BrokerClient.getDefaultInstance(eval.parser.env, channel.visibility == Visibility.GLOBAL);
		stop = getInContext().get("stop") == null ? 1 : getInContext().get("stop").getValue();
		consumerId = brokerClient
				.consume(new Channel<>(channel.mappedName, Data.class, channel.getChannelConfiguration()), value -> {
					if (ChannelDeclaration.STOP_MESSAGE.equals(value)) {
						Main.LOGGER.debug("recieved stop message: " + AtConsume.this);
						stopCount++;
						if (stop != 0 && stopCount >= stop) {
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

	}

	@Override
	public void terminate() {
		super.terminate();
		brokerClient.stopConsumer(consumerId);
	}

	@Override
	public void evalType(AstAttrib attrib) {
		typeInParameters(attrib, true,
				attrib.parser.types.getDependentType(Types.CHANNEL_TYPE_NAME, attrib.parser.types.createType()), "from",
				"channel");
	}

}
