package ini.type;

import ini.ast.Function;
import ini.ast.Variable;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class AttrContext {

	Function function;

	public AttrContext(Function function) {
		this.function = function;
	}
	
	public AttrContext(AttrContext context) {
		this.function = context.function;
		this.variables = new HashMap<String, Type>(context.variables);
	}

	private Map<String, Type> variables = new HashMap<String, Type>();

	public void bind(String name, Type type) {
		variables.put(name,type);
	}

	public Type get(String name) {
		return variables.get(name);
	}

	public Type getOrCreate(Variable variable) {
		if (!variables.containsKey(variable.name)) {
			bind(variable.name, new Type(function.parser));
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
	

}
