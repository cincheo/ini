package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;
import java.util.List;

public class SetConstructor extends NamedElement implements Expression {

	public List<Assignment> fieldAssignments;
	
	public SetConstructor(IniParser parser, Token token, String name, List<Assignment> fieldAssignments) {
		super(parser, token, name);
		this.fieldAssignments=fieldAssignments;
		this.nodeTypeId=AstNode.SET_CONSTRUCTOR;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(name+"[");
		prettyPrintList(out, fieldAssignments, ",");
		out.print("]");
	}

}
