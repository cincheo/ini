package ini.eval.function;

import ini.ast.Channel;
import ini.ast.Channel.Visibility;
import ini.ast.Invocation;
import ini.broker.BrokerClient;
import ini.eval.IniEval;
import ini.eval.at.At;
import ini.eval.data.Data;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.parser.Types;
import ini.type.AstAttrib;
import ini.type.Type;
import ini.type.UnionType;

public class KillAt extends BuiltInExecutable {

	public KillAt(IniParser parser) {
		super(parser, "stop", "target");
	}

	@Override
	public void eval(IniEval eval) {
		Data targetData = getArgument(eval, 0);
		Object target = targetData.getValue();
		if (target instanceof At) {
			((At) target).terminate();
		} else if (target instanceof Channel) {
			Channel channel = (Channel) target;
			try {
				BrokerClient.createDefaultInstance(eval.parser.env, channel.visibility == Visibility.GLOBAL)
						.produce(channel.mappedName, Channel.STOP_MESSAGE);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new RuntimeException("invalid parameter: " + target);
		}
		eval.result = targetData;
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		Type t = parser.types.createType();
		Type target = UnionType.create(parser.types.THREAD, parser.types.createDependentType(Types.CHANNEL_TYPE_NAME, t));
		return parser.types.createFunctionalType(target,
				target);
	}

}
