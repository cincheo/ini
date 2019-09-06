package ini.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ini.ast.Constructor;
import ini.ast.UserType;
import ini.type.Type;

public class Types {

	IniParser parser;
	private int index = 1;

	public int nextIndex() {
		return index++;
	}

	private Set<Type> primitiveTypes = new HashSet<>();
	
	public boolean isPrimitive(Type type) {
		return primitiveTypes.contains(type);
	}
	
	public Types(IniParser parser) {
		this.parser = parser;
		ANY = getSimpleType("Any");
		VOID = getSimpleType("Void");
		CHAR = getSimpleType("Char");
		DOUBLE = getSimpleType("Double");
		FLOAT = getSimpleType("Float", DOUBLE);
		LONG = getSimpleType("Long", FLOAT);
		INT = getSimpleType("Int", LONG);
		BYTE = getSimpleType("Byte", INT);
		BOOLEAN = getSimpleType("Boolean");
		THREAD = getSimpleType("Thread");
		STRING = getDependentType("Map", INT, CHAR);
		aliases.put("String", STRING);
		aliases.put("Number", DOUBLE);
		primitiveTypes.add(ANY);
		primitiveTypes.add(VOID);
		primitiveTypes.add(CHAR);
		primitiveTypes.add(DOUBLE);
		primitiveTypes.add(FLOAT);
		primitiveTypes.add(LONG);
		primitiveTypes.add(INT);
		primitiveTypes.add(BYTE);
		primitiveTypes.add(BOOLEAN);
		primitiveTypes.add(THREAD);
		primitiveTypes.add(STRING);
	}

	public List<UserType> userTypes = new ArrayList<UserType>();
	public Map<String, UserType> userTypeMap = new HashMap<String, UserType>();

	public Map<String, Constructor> constructors = new HashMap<String, Constructor>();

	public void register(Constructor constructor) {
		if (constructor.name != null && !constructors.containsKey(constructor.name)) {
			constructors.put(constructor.name, constructor);
		}
	}

	public Constructor getOrCreatePrimitiveConstructor(String name) {
		Constructor c = constructors.get(name);
		if (c == null) {
			c = new Constructor(parser, null, name, null);
			constructors.put(name, c);
		}
		return c;
	}

	public Type getListOf(Type type) {
		Type t = new Type(this, "Map");
		t.addTypeParameter(parser.types.INT);
		t.addTypeParameter(type);
		return t;
	}

	public Constructor getConstructor(String name) {
		return constructors.get(name);
	}

	public Constructor getFirstLevelConstructor(String name) {
		Constructor constructor = constructors.get(name);
		if (constructor == null || (constructor != null && constructor.userType == null)) {
			UserType ut = userTypeMap.get(name);
			if (ut != null && ut.constructors != null && ut.constructors.size() == 1) {
				constructor = ut.constructors.get(0);
			}
		}
		return constructor;
	}

	public boolean isConstructor(String name) {
		return constructors.containsKey(name);
	}

	public Map<String, Type> types = new HashMap<String, Type>();
	public Map<String, Type> aliases = new HashMap<String, Type>();

	public final Type ANY;
	public final Type VOID;
	public final Type BYTE;
	public final Type CHAR;
	public final Type INT;
	public final Type LONG;
	public final Type FLOAT;
	public final Type DOUBLE;
	public final Type BOOLEAN;
	public final Type STRING;
	public final Type THREAD;

	public Type getSimpleType(String name) {
		Type t = aliases.get(name);
		if (t == null) {
			t = types.get(name);
			if (t == null) {
				t = new Type(this, name);
				t.variable = false;
				types.put(name, t);
			}
		}
		return t;
	}

	public Type getSimpleType(String name, Type superType) {
		Type t = aliases.get(name);
		if (t == null) {
			t = types.get(name);
			if (t == null) {
				t = new Type(this, name);
				t.variable = false;
				t.superType = superType;
				types.put(name, t);
			}
		}
		return t;
	}

	public Type getDependentType(String name, Type... typeParameters) {
		String fullName = name + "(" + Type.typeParametersString(Arrays.asList(typeParameters)) + ")";
		Type t = types.get(fullName);
		if (t == null) {
			t = new Type(this, name);
			t.variable = false;
			for (Type p : typeParameters) {
				t.addTypeParameter(p);
			}
			types.put(fullName, t);
		}
		return t;
	}

	public Type createMapType(Type keyType, Type valueType) {
		String fullName = "Map(" + keyType.getFullName() + "," + valueType.getFullName() + ")";
		Type t = types.get(fullName);
		if (t == null) {
			t = new Type(this, "Map");
			t.variable = false;
			t.addTypeParameter(keyType);
			t.addTypeParameter(valueType);
			types.put(fullName, t);
		}
		return t;
	}

	public Type createArrayType(Type componentType) {
		return createMapType(INT, componentType);
	}

	public Type createFunctionalType(Type returnType, Type... parameterTypes) {
		Type t = new Type(this, "function");
		t.variable = true;
		for (Type p : parameterTypes) {
			t.addTypeParameter(p);
		}
		t.setReturnType(returnType);
		return t;
	}

	/*
	 * public Type getFunctionalType(Type returnType, Type... parameterTypes) {
	 * String fullName = "(" +
	 * Type.typeParametersString(Arrays.asList(parameterTypes)) + ")->" +
	 * returnType; Type t = types.get(fullName); if (t == null) { t = new
	 * Type(this, "function"); t.variable = true; for (Type p : parameterTypes)
	 * { t.addTypeParameter(p); } t.setReturnType(returnType);
	 * types.put(fullName, t); } return t; }
	 */

	public void register(String name, Type type) {
		if (!types.containsKey(name)) {
			types.put(name, type);
			type.variable = false;
		} else {
			throw new RuntimeException("type '" + name + "' is already registered");
		}
	}

	public Type createType() {
		return new Type(this);
	}

	public Type createType(String name) {
		return new Type(this, name);
	}

	public Type createType(Type superType) {
		Type t = new Type(this);
		t.setSuperType(superType);
		return t;
	}

}
