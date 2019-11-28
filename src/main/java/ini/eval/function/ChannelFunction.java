package ini.eval.function;

import java.util.UUID;

import ini.ast.ChannelDeclaration;
import ini.ast.ChannelDeclaration.Visibility;
import ini.ast.Invocation;
import ini.ast.SetConstructor;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.parser.Types;
import ini.type.AstAttrib;
import ini.type.Type;

public class ChannelFunction extends BuiltInExecutable {

	public ChannelFunction(IniParser parser) {
		super(parser, "channel", "visibility");
		setDefaultValue(0, new SetConstructor(parser, null, "Local", null));
	}

	@Override
	public void eval(IniEval eval) {
		Data visibility = getArgument(eval, 0);
		ChannelDeclaration channel = new ChannelDeclaration(parser, token,
				"temp." + UUID.randomUUID().toString().replace('-', '.'), null,
				"Global".equals(visibility.getConstructor().name) ? Visibility.GLOBAL : Visibility.LOCAL, false,
				annotations);
		eval.result = new RawData(channel);
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib, Invocation invocation) {
		return parser.types.createFunctionalType(
				parser.types.createDependentType(Types.CHANNEL_TYPE_NAME, parser.types.createType()),
				parser.types.Scope);
	}

}
