package ini.type;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import ini.ast.Variable;
import ini.parser.Types;

public class AttrContext {

	private Type executableType;
	private Types types;

	public AttrContext(Types types, Type executableType) {
		this.types = types;
		this.executableType = executableType;
	}

	public AttrContext(AttrContext context) {
		this.types = context.types;
		this.executableType = context.executableType;
		this.variables = new HashMap<String, Type>(context.variables);
	}

	private Map<String, Type> variables = new HashMap<String, Type>();

	public void bind(String name, Type type) {
		variables.put(name, type);
	}

	public boolean hasBinding(String name) {
		return variables.containsKey(name);
	}

	public Type get(String name) {
		return variables.get(name);
	}

	public Type getOrCreate(Variable variable) {
		if (!variables.containsKey(variable.name)) {
			bind(variable.name, new Type(types));
		}
		return variables.get(variable.name);
	}

	@Override
	public String toString() {
		return variables.toString();
	}

	public void prettyPrint(PrintStream out) {
		for (String v : variables.keySet()) {
			out.print("   ");
			prettyPrintVariable(out, v);
			out.println();
		}
	}

	public void prettyPrintVariable(PrintStream out, String variableName) {
		out.print(variableName + " = ");
		Type t = variables.get(variableName);
		if (t == null) {
			out.print("<undefined>");
		} else {
			out.print(t.toString());
		}
	}

	public Type getExecutableType() {
		return executableType;
	}

}
