package ini.ast;

import java.io.PrintStream;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;

import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public abstract class Executable extends NamedElement implements Expression {

	public List<Parameter> parameters;

	public Executable(IniParser parser, Token token, String name, List<Parameter> parameters) {
		super(parser, token, name);
		this.parameters = parameters;
	}

	protected final Data getArgument(IniEval eval, int index) {
		return eval.invocationStack.peek().get(parameters.get(index).name);
	}

	protected final Data getArgument(IniEval eval, String name) {
		return eval.invocationStack.peek().get(name);
	}

	protected final Type getParameterType(int index) {
		return getType().getTypeParameters().get(index);
	}

	protected final Type getReturnType() {
		return getType().getReturnType();
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(name + "(");
		prettyPrintList(out, parameters, ",");
		out.print(")");
	}

	@Override
	public String toString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		out.print(name + "(");
		prettyPrintList(out, parameters, ",");
		out.print(")");
		try {
			return baos.toString("UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public abstract void eval(IniEval eval);

	public final Type getType() {
		if (this.type == null) {
			this.type = parser.types.createFunctionalType(parser.types.createType());
			for (int i = 0; i < parameters.size(); i++) {
				this.type.addTypeParameter(new Type(parser.types));
			}
		}
		return this.type;
	}

	public Type getFunctionalType(AstAttrib attrib) {
		Type functionalType = new Type(parser.types, "function");
		if (name != null && name.equals("main")) {
			if (parameters != null && parameters.size() == 1) {
				functionalType
						.addTypeParameter(parser.types.getDependentType("Map", parser.types.INT, parser.types.STRING));
			}
			functionalType.setReturnType(parser.types.VOID);
		} else {
			functionalType.setReturnType(new Type(parser.types));
		}
		for (int i = 0; i < parameters.size(); i++) {
			functionalType.addTypeParameter(new Type(parser.types));
		}
		return functionalType;
	}

}
