package ini.ast;

import ini.parser.IniParser;
import ini.type.Type;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Function extends NamedElement {

	public List<Rule> rules;
	public List<Parameter> parameters;

	public boolean isProcess() {
		return false;
	}

	public Function(IniParser parser, Token token, String name, List<Parameter> parameters, List<Rule> rules) {
		super(parser, token, name);
		this.parameters = parameters;
		this.rules = rules;
		this.nodeTypeId = AstNode.FUNCTION;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("function " + name + "(");
		prettyPrintList(out, parameters, ",");
		out.println(") {");
		for (Rule r : rules) {
			r.prettyPrint(out);
			out.println();
		}
		out.println("}");
	}

	@Override
	public String toString() {
		return "function " + name;
	}

	transient public Type functionType = null;

	public Type getFunctionType() {
		functionType = new Type(parser, "function");
		if (name != null && name.equals("main")) {
			if (parameters != null && parameters.size() == 1) {
				functionType.addTypeParameter(parser.ast.getDependentType("Map", parser.ast.INT, parser.ast.STRING));
			}
			functionType.setReturnType(parser.ast.VOID);
		} else {
			functionType.setReturnType(new Type(parser));
		}
		for (int i = 0; i < parameters.size(); i++) {
			functionType.addTypeParameter(new Type(parser));
		}
		return functionType;
	}

}
