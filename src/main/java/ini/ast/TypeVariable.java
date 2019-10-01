package ini.ast;

import java.io.PrintStream;
import java.util.List;

import ini.parser.IniParser;
import ini.type.AstAttrib;
import ini.type.Type;

public class TypeVariable extends Variable {

	public TypeVariable component;
	public TypeVariable superType;
	public List<TypeVariable> context;
	public List<TypeVariable> typeParameters;
	public boolean parameter = false;

	public TypeVariable(IniParser parser, Token token, String name) {
		super(parser, token, name);
		nodeTypeId = TYPE_VARIABLE;
	}

	public TypeVariable(IniParser parser, Token token, String name, boolean parameter) {
		super(parser, token, name);
		this.parameter = parameter;
		nodeTypeId = TYPE_VARIABLE;
	}

	public TypeVariable(IniParser parser, Token token, TypeVariable component) {
		super(parser, token, null);
		this.component = component;
		nodeTypeId = TYPE_VARIABLE;
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

	public Type lookupTypeVariable(String name) {
		if (context == null) {
			return null;
		}
		for (TypeVariable v : context) {
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
				Type t = lookupTypeVariable(name);
				if (t == null) {
					type = parser.types.getSimpleType(name);
					if (this.superType != null) {
						type.superType = this.superType.getType();
					}
				} else {
					type = t;
				}
			}
			if (typeParameters != null) {
				for (TypeVariable v : typeParameters) {
					type.addTypeParameter(v.getType());
				}
			}
		}
		return type;
	}

	public boolean isTypeRegistered(AstAttrib attrib) {
		if (component != null) {
			return component.isTypeRegistered(attrib);
		} else {
			return attrib.parser.types.isRegistered(name);
		}
	}

	public boolean isParameter() {
		return parameter;
	}

}
