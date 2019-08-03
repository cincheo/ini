package ini.ast;

import java.io.PrintStream;

import ini.parser.IniParser;
import ini.type.Type;

public class TypeVariable extends Variable {

	public TypeVariable component;
	
	public TypeVariable(IniParser parser, Token token, String name) {
		super(parser, token, name);
	}

	public TypeVariable(IniParser parser, Token token, TypeVariable component) {
		super(parser, token, null);
		this.component = component;
	}
	
	public boolean isList() {
		return component!=null;
	}
	
	@Override
	public void prettyPrint(PrintStream out) {
		if(isList()) {
			component.prettyPrint(out);
			out.append("*");
		} else {
			super.prettyPrint(out);
		}
	}
	
	public Type getType() {
		if(isList()) {
			return parser.ast.getListOf(component.getType());
		} else {
			return parser.ast.getSimpleType(name);
		}
	}
	
}
