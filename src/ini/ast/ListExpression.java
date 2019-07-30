package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;
import java.util.List;

public class ListExpression extends AstElement implements Expression {

	public List<Expression> elements;
	
	public ListExpression(IniParser parser, Token token, List<Expression> elements) {
		super(parser, token);
		this.elements = elements;
		nodeTypeId=LIST_EXPRESSION;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("[");
		for(int i=0;i<elements.size();i++) {
			elements.get(i).prettyPrint(out);
			if(i<elements.size()-1) {
				out.print(",");
			}
		}
		out.print("]");
	}

}
