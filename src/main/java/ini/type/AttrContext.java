package ini.type;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import ini.ast.Variable;
import ini.parser.Types;

public final class AttrContext {

	private Type executableType;
	private Types types;
	public boolean hadReturnStatement = false;

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

	public final void bind(String name, Type type) {
		variables.put(name, type);
	}

	public final boolean hasBinding(String name) {
		return variables.containsKey(name);
	}

	/**
	 * Returns true if the context contains a binding representing a global
	 * declaration (for instance, functions and channels are global
	 * declarations).
	 */
	public final boolean hasGlobalDeclarationBinding(String name) {
		return variables.containsKey(name) && (variables.get(name).isChannel() || variables.get(name).isFunctional());
	}

	public final Type get(String name) {
		return variables.get(name);
	}

	public final Type getOrCreate(Variable variable) {
		if (!variables.containsKey(variable.name)) {
			bind(variable.name, new Type(types));
		}
		return variables.get(variable.name);
	}

	@Override
	public final String toString() {
		return variables.toString();
	}

	public final void prettyPrint(PrintStream out) {
		for (String v : variables.keySet()) {
			out.print("   ");
			prettyPrintVariable(out, v);
			out.println();
		}
	}

	public final void prettyPrintVariable(PrintStream out, String variableName) {
		out.print(variableName + " = ");
		Type t = variables.get(variableName);
		if (t == null) {
			out.print("<undefined>");
		} else {
			out.print(t.toString());
		}
	}

	public final Type getExecutableType() {
		return executableType;
	}

}
