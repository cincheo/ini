package ini.ast;

import ini.parser.IniParser;
import ini.type.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ast {
	
	IniParser parser;
	
	public Ast(IniParser parser) {
		this.parser = parser;
		buildDefaultTypes();
	}
	
	public List<UserType> userTypes = new ArrayList<UserType>();
	public Map<String,UserType> userTypeMap = new HashMap<String,UserType>();

	public Map<String, Constructor> constructors = new HashMap<String, Constructor>();

	public void register(Constructor constructor) {
		if (constructor.name != null
				&& !constructors.containsKey(constructor.name)) {
			constructors.put(constructor.name, constructor);
		}
	}

	public Constructor getOrCreatePrimitive(String name) {
		Constructor c = constructors.get(name);
		if (c == null) {
			c = new Constructor(parser, null, name, null);
			constructors.put(name, c);
		}
		return c;
	}

	public Type getListOf(Type type) {
		Type t = new Type(parser,"Map");
		t.addTypeParameter(parser.ast.INT);
		t.addTypeParameter(type);
		return t;
	}

	public Constructor getConstructor(String name) {
		return constructors.get(name);
	}

	public Constructor getFirstLevelConstructor(String name) {
		Constructor constructor = constructors.get(name);
		if (constructor == null
				|| (constructor != null && constructor.userType == null)) {
			UserType ut = userTypeMap.get(name);
			if (ut != null && ut.constructors != null
					&& ut.constructors.size() == 1) {
				constructor = ut.constructors.get(0);
			}
		}
		return constructor;
	}

	public boolean isConstructor(String name) {
		return constructors.containsKey(name);
	}

	public final Type ANY = new Type(parser);

	public Map<String, Type> types = new HashMap<String, Type>();
	public Map<String, Type> aliases = new HashMap<String, Type>();

	public Type VOID;
	public Type BYTE;
	public Type CHAR;
	public Type INT;
	public Type LONG;
	public Type FLOAT;
	public Type DOUBLE;
	public Type BOOLEAN;
	public Type STRING;
	public Type THREAD;

	public void buildDefaultTypes() {
		VOID = getSimpleType("Void");
		CHAR = getSimpleType("Char");
		DOUBLE = getSimpleType("Double");
		FLOAT = getSimpleType("Float", DOUBLE);
		LONG = getSimpleType("Long", FLOAT);
		INT = getSimpleType("Int", LONG);
		BYTE = getSimpleType("Byte", INT);
		BOOLEAN = getSimpleType("Boolean");
		THREAD = getSimpleType("Tread");
		STRING = getDependentType("Map", INT, CHAR);
		aliases.put("String", STRING);
	}
	
	public Type getSimpleType(String name) {
		Type t = aliases.get(name);
		if (t == null) {
			t = types.get(name);
			if (t == null) {
				t = new Type(parser,name);
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
				t = new Type(parser,name);
				t.variable = false;
				t.superType = superType;
				types.put(name, t);
			}
		}
		return t;
	}

	public Type getDependentType(String name, Type... typeParameters) {
		String fullName = name + "("
				+ Type.typeParametersString(Arrays.asList(typeParameters)) + ")";
		Type t = types.get(fullName);
		if (t == null) {
			t = new Type(parser,name);
			t.variable = false;
			for (Type p : typeParameters) {
				t.addTypeParameter(p);
			}
			types.put(fullName, t);
		}
		return t;
	}

	public Type getFunctionalType(Type returnType,
			Type... parameterTypes) {
		String fullName = "("
				+ Type.typeParametersString(Arrays.asList(parameterTypes)) + ")->"
				+ returnType;
		Type t = types.get(fullName);
		if (t == null) {
			t = new Type(parser,"function");
			t.variable = true;
			for (Type p : parameterTypes) {
				t.addTypeParameter(p);
			}
			t.setReturnType(returnType);
			types.put(fullName, t);
		}
		return t;
	}

	public void register(String name, Type type) {
		if (!types.containsKey(name)) {
			types.put(name, type);
			type.variable = false;
		} else {
			throw new RuntimeException("type '" + name
					+ "' is already registered");
		}
	}
	
	

}
