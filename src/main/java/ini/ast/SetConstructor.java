package ini.ast;

import java.io.PrintStream;
import java.util.List;

import ini.parser.IniParser;

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
