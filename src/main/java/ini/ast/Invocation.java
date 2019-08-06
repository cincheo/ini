package ini.ast;

import java.io.PrintStream;
import java.util.List;

import ini.parser.IniParser;

public class Invocation extends NamedElement implements Statement, Expression {

	public List<Expression> arguments;
	
	public Invocation(IniParser parser, Token token, String name, List<Expression> arguments) {
		super(parser, token, name);
		this.arguments = arguments;
		this.nodeTypeId=AstNode.INVOCATION;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(name+"(");
		prettyPrintList(out, arguments, ",");
		out.print(")");
	}
	
}
