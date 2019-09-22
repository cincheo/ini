package ini.eval.function;

import ini.ast.Channel;
import ini.ast.Channel.Visibility;
import ini.broker.BrokerClient;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class ProduceFunction extends BuiltInExecutable {

	public ProduceFunction(IniParser parser) {
		super(parser, "produce", "channel", "data");
	}

	@Override
	public void eval(IniEval eval) {
		Channel channel = getArgument(eval, 0).getValue();
		Data data = getArgument(eval, 1);
		try {
			BrokerClient.createDefaultInstance(eval.parser.env, channel.visibility == Visibility.GLOBAL)
					.produce(channel.mappedName, RawData.rawCopy(data));
			// KafkaClient.runProducer(topic, RawData.rawCopy(message));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		eval.result = data;
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib) {
		Type t = parser.types.createType();
		return attrib.parser.types.createFunctionalType(attrib.parser.types.VOID,
				attrib.parser.types.getDependentType("Channel", t), t);
	}

}
