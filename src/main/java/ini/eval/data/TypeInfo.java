package ini.eval.data;

public class TypeInfo {

	public static final int NULL = 0;
	public static final int INTEGER = 1;
	public static final int LONG = 2;
	public static final int DOUBLE = 3;
	public static final int FLOAT = 4;
	public static final int STRING = 5;
	public static final int BOOLEAN = 6;
	public static final int FUNCTION = 7;
	public static final int PROCESS = 8;
	public static final int CHANNEL = 9;
	
	public static int getTypeInfoForInstance(Object object) {
		if(object == null) {
			return NULL;
		} else {
			return getTypeInfo(object.getClass());
		}
	}

	public static int getTypeInfo(Class<?> clazz) {
		switch (clazz.getName()) {
		case "java.lang.Integer":
			return INTEGER;
		case "java.lang.Long":
			return LONG;
		case "java.lang.Double":
			return DOUBLE;
		case "java.lang.Float":
			return FLOAT;
		case "java.lang.String":
			return STRING;
		case "java.lang.Boolean":
			return BOOLEAN;
		case "ini.ast.Function":
			return FUNCTION;
		case "ini.ast.Process":
			return PROCESS;
		case "ini.ast.ChannelDeclaration":
			return CHANNEL;
		default:
			return NULL;
			//throw new RuntimeException("unknown type "+clazz);
		}
	}
	
}
