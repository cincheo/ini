package ini.type;

import ini.ast.UserType;
import ini.parser.IniParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Type {

	boolean constructorType = false;

	public boolean isLTE(Type type) {
		if (type == this) {
			return true;
		} else if (this.superType == null) {
			return false;
		} else {
			return this.superType.isLTE(type);
		}
	}

	public boolean isGTE(Type type) {
		return type.isLTE(this);
	}

	static int index = 1;

	public Class<?> toJavaType() {
		if(this==parser.ast.VOID) {
			return void.class;
		} else if(this==parser.ast.CHAR) {
			return char.class;
		} else if(this==parser.ast.DOUBLE) {
			return double.class;
		} else if(this==parser.ast.FLOAT) {
			return float.class;
		} else if(this==parser.ast.LONG) {
			return long.class;
		} else if(this==parser.ast.INT) {
			return int.class;
		} else if(this==parser.ast.BYTE) {
			return byte.class;
		} else if(this==parser.ast.BOOLEAN) {
			return boolean.class;
		} else if(this==parser.ast.STRING) {
			return String.class;
		} else {
			return null;
		}
	}
	
	public boolean variable = true;

	protected String name;
	List<Type> typeParameters;
	Type returnType;
	Map<String, Type> fields = null;

	IniParser parser;
	
	public Type(IniParser parser) {
		this.parser = parser;
		this.name = "_T" + (index++);
	}

	public Type(IniParser parser, String name) {
		this.parser = parser;
		this.name = name;
	}

	public UserType userType;

	public Type superType = null;
	public List<Type> subTypes = null;

	public void addSubType(Type type) {
		if (subTypes == null) {
			subTypes = new ArrayList<Type>();
		}
		subTypes.add(type);
	}

	public boolean hasSubTypes() {
		return subTypes != null && !subTypes.isEmpty();
	}

	public Type(UserType userType) {
		this.name = userType.name;
		this.userType = userType;
		this.fields = new HashMap<String, Type>();
	}

	public String getFullName() {
		if (hasTypeParameters()) {
			return name + "(" + typeParametersString(typeParameters) + ")";
		} else if (hasFields()) {
			if (name != null) {
				return name + "[" + fieldsString(fields) + "]";
			} else {
				return "[" + fieldsString(fields) + "]";
			}
		} else if (isFunctional()) {
			return "(" + typeParametersString(typeParameters) + ")->"
					+ returnType;
		} else {
			return name;
		}
	}

	public String getName() {
		return name;
	}

	public boolean isVariable() {
		return variable;
	}

	public String toString() {
		if (isList()) {
			if (typeParameters.get(1) == parser.ast.CHAR) {
				return "String";
			} else {
				return typeParameters.get(1).toString() + "*";
			}
		} else {
			return getFullName();// +(variable?"%":"");
		}
	}

	public boolean isList() {
		return isMap() && typeParameters.get(0) == parser.ast.INT;
	}

	static public String typeParametersString(List<Type> typeParameters) {
		String s = "";
		if (typeParameters != null) {
			for (int i = 0; i < typeParameters.size(); i++) {
				s += typeParameters.get(i).getFullName();
				if (i < typeParameters.size() - 1) {
					s += ",";
				}
			}
		}
		return s;
	}

	static public String fieldsString(Map<String, Type> fields) {
		String s = "";
		List<Map.Entry<String, Type>> fieldList = new ArrayList<Map.Entry<String, Type>>();
		fieldList.addAll(fields.entrySet());
		for (int i = 0; i < fieldList.size(); i++) {
			s += fieldList.get(i).getKey();
			s += ":";
			s += fieldList.get(i).getValue() == null ? null : fieldList.get(i)
					.getValue().getName();
			if (i < fieldList.size() - 1) {
				s += ",";
			}
		}
		return s;
	}

	public boolean isMap() {
		return "Map".equals(name);
	}

	public void addTypeParameter(Type type) {
		if (typeParameters == null) {
			typeParameters = new ArrayList<Type>();
		}
		typeParameters.add(type);
	}

	public void addField(String name, Type type) {
		if (fields == null) {
			fields = new HashMap<String, Type>();
		}
		fields.put(name, type);
	}

	public List<Type> getTypeParameters() {
		return typeParameters;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Type)) {
			return false;
		} else {
			return getFullName().equals(((Type) o).getFullName());
		}
	}

	public void substitute(TypingConstraint substitution) {
		if (hasTypeParameters()) {
			for (int i = 0; i < typeParameters.size(); i++) {
				if (typeParameters.get(i).isVariable()) {
					// ((TypeVariable) typeParameters.get(i))
					// .substitute(substitution);
					if (typeParameters.get(i).equals(substitution.left)) {
						typeParameters.set(i, substitution.right);
					}
				}
			}
		}

		if (hasFields()) {
			List<Map.Entry<String, Type>> fieldList = new ArrayList<Map.Entry<String, Type>>();
			fieldList.addAll(fields.entrySet());
			for (int i = 0; i < fieldList.size(); i++) {
				if (fieldList.get(i).getValue().isVariable()) {
					// do not substitute recursively to avoid infinite
					// regression on recursive types
					// ((TypeVariable) fieldList.get(i).getValue())
					// .substitute(substitution);
					if (fieldList.get(i).getValue().equals(substitution.left)) {
						fields.put(fieldList.get(i).getKey(),
								substitution.right);
					}
				}
			}
		}

		if (returnType != null && (returnType.isVariable())) {
			// ((TypeVariable) returnType).substitute(substitution);
			if (returnType.equals(substitution.left)) {
				returnType = substitution.right;
			}
		}
	}

	public Type getReturnType() {
		return returnType;
	}

	public void setReturnType(Type returnType) {
		this.returnType = returnType;
	}

	public boolean isFunctional() {
		return name != null && name.equals("function");
	}

	public Map<String, Type> getFields() {
		return fields;
	}

	public boolean hasFields() {
		return fields != null && !fields.isEmpty();
	}

	public boolean hasTypeParameters() {
		return typeParameters != null && !typeParameters.isEmpty();
	}

}
