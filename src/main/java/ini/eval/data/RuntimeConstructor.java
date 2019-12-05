package ini.eval.data;

import java.util.Collection;
import java.util.Collections;

import ini.ast.Constructor;

public class RuntimeConstructor {

	public static final RuntimeConstructor BOOLEAN = new RuntimeConstructor("Boolean");
	public static final RuntimeConstructor STRING = new RuntimeConstructor("String");
	public static final RuntimeConstructor DOUBLE = new RuntimeConstructor("Double");
	public static final RuntimeConstructor FLOAT = new RuntimeConstructor("Float");
	public static final RuntimeConstructor INT = new RuntimeConstructor("Int");
	public static final RuntimeConstructor LONG = new RuntimeConstructor("Long");
	public static final RuntimeConstructor CHAR = new RuntimeConstructor("Char");
	public static final RuntimeConstructor BYTE = new RuntimeConstructor("Byte");
	public static final RuntimeConstructor CHANNEL = new RuntimeConstructor("Channel");

	public String name;
	public Collection<String> fields;

	public RuntimeConstructor(Constructor constructor) {
		this.name = constructor.name;
		this.fields = constructor.fieldMap.keySet();
	}

	private RuntimeConstructor(String name) {
		this.name = name;
		this.fields = Collections.emptyList();
	}
	
	public RuntimeConstructor(String name, Collection<String> fields) {
		this.name = name;
		this.fields = fields;
	}

}
