package ini.eval.data;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ini.ast.ChannelDeclaration;
import ini.ast.Constructor;
import ini.ast.TypeVariable;
import ini.type.Type;

public class RuntimeConstructor {

	public static final RuntimeConstructor BOOLEAN = new RuntimeConstructor("Boolean");
	public static final RuntimeConstructor STRING = new RuntimeConstructor("String");
	public static final RuntimeConstructor DOUBLE = new RuntimeConstructor("Double");
	public static final RuntimeConstructor FLOAT = new RuntimeConstructor("Float");
	public static final RuntimeConstructor INT = new RuntimeConstructor("Int");
	public static final RuntimeConstructor LONG = new RuntimeConstructor("Long");
	public static final RuntimeConstructor CHAR = new RuntimeConstructor("Char");
	public static final RuntimeConstructor BYTE = new RuntimeConstructor("Byte");

	public String name;
	public Collection<String> fields;
	public List<RuntimeConstructor> dependentConstructors;

	public RuntimeConstructor(Constructor constructor) {
		this.name = constructor.name;
		this.fields = constructor.fieldMap.keySet();
	}

	private RuntimeConstructor(String name) {
		this.name = name;
		this.fields = Collections.emptyList();
	}

	public RuntimeConstructor(String name, List<RuntimeConstructor> dependentConstructors, Collection<String> fields) {
		this.name = name;
		this.dependentConstructors = dependentConstructors;
		this.fields = fields == null ? Collections.emptyList() : fields;
	}

	public boolean matches(Type type) {
		if (!type.getName().equals(name)) {
			return false;
		}
		if (type.hasTypeParameters()) {
			if (dependentConstructors == null || type.getTypeParameters().size() != dependentConstructors.size()) {
				return false;
			}
			for (int i = 0; i < type.getTypeParameters().size(); i++) {
				if (!dependentConstructors.get(i).matches(type.getTypeParameters().get(i))) {
					return false;
				}
			}
		}
		return true;
	}

}
