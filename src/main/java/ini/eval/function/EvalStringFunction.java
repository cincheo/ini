package ini.eval.function;

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
		ByteArrayOutputStream baos;
		try {
			PrintStream out = new PrintStream(baos = new ByteArrayOutputStream());
			IniParser parser = IniParser.parseCode(code,out,out);
			if(parser.hasErrors()) {
				parser.printErrors(out);
				return new RawData(baos.toString());
			}
			AstAttrib attrib = parser.attrib();
			if(attrib.hasErrors()) {
				attrib.printErrors(out);
				return new RawData(baos.toString());
			}
			parser.evalMainFunction();
		} catch (Exception e) {
			throw new RuntimeException("cannot evaluate string", e);
		}
		return new RawData(baos.toString());
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints,
			Invocation invocation) {
		return parser.ast.getFunctionalType(parser.ast._, parser.ast.STRING);
	}

}
