package ini.ast;

import java.io.PrintStream;
import java.util.List;

import ini.parser.IniParser;

public class Function extends Executable {

	public Sequence<Statement> statements;

	public Function(IniParser parser, Token token, String name, List<Parameter> parameters, Sequence<Statement> statements) {
		super(parser, token, name, parameters);
		this.statements = statements;
		this.nodeTypeId = AstNode.FUNCTION;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("function " + name + "(");
		prettyPrintList(out, parameters, ",");
		out.println(") {");
		Sequence<Statement> s = statements;
		while(s!=null) {
			s.get().prettyPrint(out);
			out.println();
			s = s.next();
		}
		out.println("}");
	}

	@Override
	public String toString() {
		return "function " + name;
	}

}
