package ini.eval.function;

import ini.ast.Channel;
import ini.ast.Channel.Visibility;
import ini.eval.IniEval;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class ChannelFunction extends BuiltInExecutable {

	public ChannelFunction(IniParser parser) {
		super(parser, "channel", "name");
	}

	@Override
	public void eval(IniEval eval) {
		String name = getArgument(eval, 0).getValue();
		Visibility visibility = Visibility.PRIVATE;
		if (name.startsWith("+")) {
			visibility = Visibility.GLOBAL;
			name = name.substring(1);
		}
		Channel channel = new Channel(parser, token, name, null, visibility, false, annotations);
		eval.result = new RawData(channel);
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib) {
		return attrib.parser.types.createFunctionalType(
				attrib.parser.types.getDependentType("Channel", parser.types.ANY), parser.types.STRING);
	}

}
