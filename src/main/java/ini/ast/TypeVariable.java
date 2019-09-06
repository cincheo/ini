package ini.ast;

import java.io.PrintStream;
import java.util.List;

import ini.parser.IniParser;
import ini.type.Type;

public class TypeVariable extends Variable {

	public TypeVariable component;
	public TypeVariable superType;
	public List<TypeVariable> typeParameters;
	private boolean parameter = false;

	public TypeVariable(IniParser parser, Token token, String name) {
		super(parser, token, name);
	}

	public TypeVariable(IniParser parser, Token token, String name, boolean parameter) {
		super(parser, token, name);
		this.parameter = parameter;
	}

	public TypeVariable(IniParser parser, Token token, TypeVariable component) {
		super(parser, token, null);
		this.component = component;
	}

	public boolean isList() {
		return component != null;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		if (isList()) {
			component.prettyPrint(out);
			out.append("*");
		} else {
			super.prettyPrint(out);
		}
	}

	private Type lookupTypeParameter(String name) {
		if (typeParameters == null) {
			return null;
		}
		for (TypeVariable v : typeParameters) {
			if (name.equals(v.name)) {
				return v.getType();
			}
		}
		return null;
	}

	public Type getType() {
		if (type != null) {
			return type;
		}
		if (parameter) {
			type = parser.types.createType();
			if (superType != null) {
				type.superType = parser.types.getSimpleType(superType.name);
			}
		} else {
			if (isList()) {
				type = parser.types.getListOf(component.getType());
			} else {
				Type t = lookupTypeParameter(name);
				if (t == null) {
					type = parser.types.getSimpleType(name);
				} else {
					type = t;
				}
			}
		}
		return type;
	}

	public boolean isParameter() {
		return parameter;
	}

}
