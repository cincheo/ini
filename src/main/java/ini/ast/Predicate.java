package ini.ast;

import java.io.PrintStream;

import ini.parser.IniParser;

public class Predicate extends NamedElement {

	public String expression;

	public Predicate(IniParser parser, Token token, String name, String expression) {
		super(parser, token, name);
		this.expression = expression;
		this.nodeTypeId = AstNode.PREDICATE;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("predicate " + name + " "+STRING_LITERAL);
		if (annotations != null) {
			out.print(" " + annotations);
		}
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitPredicate(this);
	}
	
}
