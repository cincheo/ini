package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;
import java.util.List;

public class AtBinding extends NamedElement {

	public String className;
	public List<TypeVariable> configurationTypes;
	public List<TypeVariable> runtimeTypes;

	public AtBinding(IniParser parser, Token token, String name,
			List<TypeVariable> configurationTypes, List<TypeVariable> runtimeTypes, String className) {
		super(parser, token, name);
		this.configurationTypes = configurationTypes;
		this.runtimeTypes = runtimeTypes;
		this.className = className;
		this.nodeTypeId = AstNode.AT_BINDING;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(name + " => " + "\"" + className + "\"");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitAtBinding(this);
	}
	
}
