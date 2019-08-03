package ini.ast;

import java.io.PrintStream;
import java.util.List;

import ini.parser.IniParser;

public class Rule extends AstElement {

	public Sequence<Statement> statements;
	public Expression guard;
	public AtPredicate atPredicate;
	public List<Expression> synchronizedAtsNames;

	public Rule(IniParser parser, Token token, AtPredicate atPredicate, Expression guard,
			Sequence<Statement> statements, List<Expression> synchronizedAtsNames) {
		super(parser, token);
		this.atPredicate = atPredicate;
		this.guard = guard;
		this.statements = statements;
		this.synchronizedAtsNames = synchronizedAtsNames;
		this.nodeTypeId = AstNode.RULE;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("  ");
		if(atPredicate!=null) {
			atPredicate.prettyPrint(out);
			if(guard!=null) {
				out.println(" ");
			}
		}
		if(guard!=null) {
			guard.prettyPrint(out);
		}
		out.println(" {");
		Sequence<Statement> s = statements;
		while (s != null) {
			out.print("    ");
			s.get().prettyPrint(out);
			out.println();
			s = s.next();
		}
		out.println("  }");
	}

	@Override
	public String toString() {
		if(atPredicate!=null) {
			return atPredicate.toString();
		}
		if(guard!=null) {
			return guard.toString();
		}
		throw new RuntimeException("illegal AST node");
	}

}
