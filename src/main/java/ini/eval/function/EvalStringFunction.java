package ini.eval.function;

import ini.Main;
import ini.ast.Expression;
import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;
import ini.type.TypingConstraint;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class EvalStringFunction extends IniFunction {

	@Override
	public Data eval(final IniEval eval, final List<Expression> params) {
		String code = eval.eval(params.get(0)).getValue();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			IniParser parser = IniParser.createParserForCode(eval.parser.env, eval.parser, code);
			parser.out = new PrintStream(baos);
			parser.parse();
			if(parser.hasErrors()) {
				parser.printErrors(parser.out);
				return new RawData(baos.toString());
			}
			AstAttrib attrib = new AstAttrib(parser);
			attrib.attrib(parser);
			attrib.unify();
			if(attrib.hasErrors()) {
				parser.printErrors(parser.out);
				return new RawData(baos.toString());
			}
			Main.mainEval(parser, attrib, null);
		} catch (Exception e) {
			throw new RuntimeException("cannot evaluate string", e);
		}
		return new RawData(baos.toString());
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints,
			Invocation invocation) {
		return parser.types.createFunctionalType(parser.types.ANY, parser.types.STRING);
	}

}
