package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class Field extends NamedElement {

	public Constructor constructor;
	
	public Field(IniParser parser, Token token, String name, Constructor constructor) {
		super(parser, token, name);
		this.constructor = constructor;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(name+":");
		constructor.prettyPrint(out);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitField(this);
	}
	
}
