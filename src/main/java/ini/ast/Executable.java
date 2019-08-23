package ini.ast;

import java.util.List;

import ini.parser.IniParser;
import ini.type.Type;

public abstract class Executable extends NamedElement {

	public List<Parameter> parameters;

	public Executable(IniParser parser, Token token, String name, List<Parameter> parameters) {
		super(parser, token, name);
		this.parameters = parameters;
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
