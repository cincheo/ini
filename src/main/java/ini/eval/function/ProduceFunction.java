package ini.eval.function;

import ini.ast.Channel;
import ini.ast.Invocation;
import ini.ast.StringLiteral;
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
		setDefaultValue(1, new StringLiteral(parser, token, "VOID"));
	}

	@Override
	public void eval(IniEval eval) {
		Data channelData = getArgument(eval, 0);
		Channel channel = channelData.getValue();
		Data data = getArgument(eval, 1);
		try {
			BrokerClient.createDefaultInstance(eval.parser.env, channel.visibility == Visibility.GLOBAL)
					.produce(channel.mappedName, RawData.rawCopy(data));
			// KafkaClient.runProducer(topic, RawData.rawCopy(message));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		eval.result = channelData;
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		// if (invocation != null && invocation.arguments.size() == 1) {
		// return
		// attrib.parser.types.createFunctionalType(attrib.parser.types.VOID,
		// attrib.parser.types.getSimpleType("Channel"));
		// } else {
		Type t = parser.types.createType();
		Type chanType = attrib.parser.types.getDependentType("Channel", t);
		return attrib.parser.types.createFunctionalType(chanType, chanType, t);
		// }
	}

}
