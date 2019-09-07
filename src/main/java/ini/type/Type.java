package ini.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ini.ast.Executable;
import ini.ast.UserType;
import ini.parser.Types;

public class Type {

	public boolean constructorType = false;
	public Executable executable = null;

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

	public Class<?> toJavaType() {
		if (this == types.VOID) {
			return void.class;
		} else if (this == types.CHAR) {
			return char.class;
		} else if (this == types.DOUBLE) {
			return double.class;
		} else if (this == types.FLOAT) {
			return float.class;
		} else if (this == types.LONG) {
			return long.class;
		} else if (this == types.INT) {
			return int.class;
		} else if (this == types.BYTE) {
			return byte.class;
		} else if (this == types.BOOLEAN) {
			return boolean.class;
		} else if (this == types.STRING) {
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

	Types types;

	public Type(Types types) {
		this.types = types;
		this.name = "_T" + (types.nextIndex());
	}

	public Type(Types types, String name) {
		this.types = types;
		this.name = name;
	}

	public UserType userType;

	public Type superType = null;
	public List<Type> subTypes = null;

	public void setSuperType(Type type) {
		this.superType = type;
	}

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
			return "(" + typeParametersString(typeParameters) + ")->" + returnType;
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
			if (typeParameters.get(1) == types.CHAR) {
				return "String";
			} else {
				return typeParameters.get(1).toString() + "*";
			}
		} else {
			return getFullName();
		}
	}

	public boolean isList() {
		return isMap() && typeParameters.get(0) == types.INT;
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
			s += fieldList.get(i).getValue() == null ? null : fieldList.get(i).getValue().getName();
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

	@SuppressWarnings("unchecked")
	public List<Type> getTypeParameters() {
		if (typeParameters == null) {
			return Collections.EMPTY_LIST;
		}
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

	public Type deepCopy() {
		if (types.isPrimitive(this)) {
			return this;
		} else {
			Type copy = new Type(this.types, this.name);
			if (this.typeParameters != null) {
				copy.typeParameters = new ArrayList<>();
				for (Type t : this.typeParameters) {
					copy.typeParameters.add(t.deepCopy());
				}
			}
			copy.constructorType = this.constructorType;
			copy.executable = this.executable;
			if (this.fields != null) {
				copy.fields = new HashMap<>();
				for (Entry<String, Type> e : this.fields.entrySet()) {
					copy.fields.put(e.getKey(), e.getValue().deepCopy());
				}
			}
			if (this.returnType != null) {
				copy.returnType = this.returnType.deepCopy();
			}
			copy.subTypes = subTypes;
			copy.superType = superType;
			copy.userType = this.userType;
			copy.variable = this.variable;
			return copy;
		}
	}

	public Type substitute(List<Type> parameters, List<Type> arguments, List<Type> freeVariables) {
		int index = parameters.indexOf(this);
		if (index >= 0) {
			return arguments.get(index);
		} else {
			if (this.typeParameters != null && !this.typeParameters.isEmpty()) {
				List<Type> l = new ArrayList<>();
				for (Type t : this.typeParameters) {
					l.add(t.substitute(parameters, arguments, freeVariables));
				}
			}
			if (this.name.startsWith("_")) {
				freeVariables.add(this);
			}
			return this;
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
						fields.put(fieldList.get(i).getKey(), substitution.right);
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

	public boolean hasVariablePart() {
		if (variable) {
			return true;
		} else {
			if (typeParameters != null) {
				for (Type t : typeParameters) {
					if (t.hasVariablePart()) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
