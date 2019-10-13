package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;
import java.util.List;

public class CaseStatement extends AstElement implements Statement {
	public List<Rule> cases;
	public Sequence<Statement> defaultStatements;

	public CaseStatement(IniParser parser, Token token, List<Rule> cases, Sequence<Statement> defaultStatements) {
		super(parser, token);
		this.cases = cases;
		this.defaultStatements = defaultStatements;
		this.nodeTypeId = AstNode.CASE_STATEMENT;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.println("    case {");
		if (cases != null) {
			for (Rule rule : cases) {
				rule.prettyPrint(out);
			}
		}
		out.println("      default {");
		if (defaultStatements != null) {
			Sequence<Statement> s = defaultStatements;
			while (s != null) {
				out.print("        ");
				s.get().prettyPrint(out);
				out.println();
				s = s.next();
			}
		}
		out.println("      }");
		out.println("    }");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitCaseStatement(this);
	}
	
}
