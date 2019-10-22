package ini.eval.function;

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
		super(parser, "channel");
	}

	@Override
	public void eval(IniEval eval) {
		//String name = getArgument(eval, 0).getValue();
		Channel channel = new Channel(parser, token, null, null, Visibility.PRIVATE, false, annotations);
		eval.result = new RawData(channel);
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		return attrib.parser.types
				.createFunctionalType(attrib.parser.types.createDependentType("Channel", parser.types.createType()));
	}

}
