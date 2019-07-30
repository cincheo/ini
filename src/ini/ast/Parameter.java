package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class Parameter extends NamedElement {

	public Expression defaultValue;
	
	public Parameter(IniParser parser, Token token, String name, Expression defaultValue) {
		super(parser, token, name);
		this.defaultValue = defaultValue;
		this.nodeTypeId = AstNode.PARAMETER;
	}

	public Parameter(IniParser parser, Token token, String name) {
		this(parser,token,name,null);
	}
	
	@Override
	public void prettyPrint(PrintStream out) {
		out.print(name);
		if(defaultValue!=null) {
			out.print("=");
			defaultValue.prettyPrint(out);
		}
	}
	
}
