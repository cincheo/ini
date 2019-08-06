package ini.eval.function;

import java.util.List;

import ini.ast.Expression;
import ini.ast.Invocation;
import ini.broker.DefaultBrokerClient;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

public class ProduceFunction extends IniFunction {

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		String channel = eval.eval(params.get(0)).getValue();
		Data data = eval.eval(params.get(1));
		try {
			DefaultBrokerClient.getInstance().produce(channel, RawData.rawCopy(data));
			//KafkaClient.runProducer(topic, RawData.rawCopy(message));
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints,
			Invocation invocation) {
		return parser.ast.getFunctionalType(parser.ast.VOID, parser.ast.STRING, parser.ast.ANY);
	}

}