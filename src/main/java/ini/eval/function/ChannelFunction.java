package ini.eval.function;

import java.util.UUID;

import ini.ast.Channel;
import ini.ast.Channel.Visibility;
import ini.ast.Invocation;
import ini.ast.SetConstructor;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class ChannelFunction extends BuiltInExecutable {

	public ChannelFunction(IniParser parser) {
		super(parser, "channel", "visibility");
		setDefaultValue(0, new SetConstructor(parser, null, "Local", null)); // new BooleanLiteral(parser, null, false));
	}

	@Override
	public void eval(IniEval eval) {
		Data visibility = getArgument(eval, 0);
		Channel channel = new Channel(parser, token, "temp." + UUID.randomUUID().toString().replace('-', '.'), null,
				"Global".equals(visibility.getConstructor().name) ? Visibility.GLOBAL : Visibility.LOCAL, false, annotations);
		eval.result = new RawData(channel);
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		return parser.types.createFunctionalType(parser.types.createDependentType("Channel", parser.types.createType()),
				parser.types.Scope);
	}

}
