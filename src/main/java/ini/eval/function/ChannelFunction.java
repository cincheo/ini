package ini.eval.function;

import java.util.UUID;

import ini.ast.BooleanLiteral;
import ini.ast.Channel;
import ini.ast.Invocation;
import ini.ast.Channel.Visibility;
import ini.eval.IniEval;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class ChannelFunction extends BuiltInExecutable {

	public ChannelFunction(IniParser parser) {
		super(parser, "channel", "global");
		setDefaultValue(0, new BooleanLiteral(parser, null, false));
	}

	@Override
	public void eval(IniEval eval) {
		boolean global = getArgument(eval, 0).getValue();
		Channel channel = new Channel(parser, token, "temp." + UUID.randomUUID().toString().replace('-', '.'), null,
				global ? Visibility.GLOBAL : Visibility.PRIVATE, false, annotations);
		eval.result = new RawData(channel);
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		return parser.types.createFunctionalType(parser.types.createDependentType("Channel", parser.types.createType()),
				parser.types.BOOLEAN);
	}

}
