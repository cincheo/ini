package ini.ast;

import ini.parser.IniParser;

public abstract class NamedElement extends AstElement {
	public String name;

	public NamedElement(IniParser parser, Token token, String name) {
		super(parser, token);
		this.name = name;
	}
	
}
