package ini.ast;

import java.io.PrintStream;
import java.util.List;

import ini.eval.IniEval;
import ini.eval.IniEval.ReturnException;
import ini.parser.IniParser;

public class Function extends Executable {

	public Sequence<AstNode> statements;
	public boolean oneExpressionLambda = false;

	public Function(IniParser parser, Token token, String name, List<Parameter> parameters,
			Sequence<AstNode> statements) {
		super(parser, token, name, parameters);
		this.statements = statements;
		this.nodeTypeId = AstNode.FUNCTION;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		if(name != null) {
			out.print("function " + name);
		}
		out.print("(");
		prettyPrintList(out, parameters, ",");
		out.println(") {");
		Sequence<AstNode> s = statements;
		while (s != null) {
			s.get().prettyPrint(out);
			out.println();
			s = s.next();
		}
		out.println("}");
	}

	@Override
	public void eval(IniEval eval) {
		try {
			Sequence<AstNode> s = this.statements;
			while (s != null) {
				eval.eval(s.get());
				s = s.next();
			}
		} catch (ReturnException e) {
			// swallow
		}
	}

}
