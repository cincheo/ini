package ini.eval.function;

import ini.ast.ChannelDeclaration;
import ini.ast.ChannelDeclaration.Visibility;
import ini.ast.Invocation;
import ini.ast.StringLiteral;
import ini.broker.Channel;
import ini.broker.BrokerClient;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.parser.Types;
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
		ChannelDeclaration channel = channelData.getValue();
		Data data = getArgument(eval, 1);
		try {
			BrokerClient.getDefaultInstance(eval.parser.env, channel.visibility == Visibility.GLOBAL).produce(
					new Channel<>(channel.mappedName, Data.class, channel.getChannelConfiguration()),
					RawData.rawCopy(data));
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
		Type chanType = attrib.parser.types.getDependentType(Types.CHANNEL_TYPE_NAME, t);
		return attrib.parser.types.createFunctionalType(chanType, chanType, t);
		// }
	}

}
