package ini.ast;

import ini.parser.IniParser;
import ini.type.Type;

import java.io.PrintStream;
import java.util.List;

public class Binding extends NamedElement {

	public String className;
	public String member;
	public List<TypeVariable> parameterTypes;
	public TypeVariable returnType;

	public enum Kind {
		FIELD, METHOD, CONSTRUCTOR
	}

	public Binding(IniParser parser, Token token, String name,
			List<TypeVariable> parameterTypes, TypeVariable returnType, String className,
			String member) {
		super(parser, token, name);
		this.parameterTypes = parameterTypes;
		this.returnType = returnType;
		this.className = className;
		this.member = member;
		this.nodeTypeId = AstNode.BINDING;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(name + " => " + "\"" + className + "\"" + "," + "\"" + member
				+ "\"");
	}

	public Kind getKind() {
		String[] parts = member.split("\\(");
		if (parts.length == 1) {
			return Kind.FIELD;
		} else if (parts[0].equals("new")) {
			return Kind.CONSTRUCTOR;
		} else {
			return Kind.METHOD;
		}
	}

	public String getMemberName() {
		return member.split("\\(")[0];
	}

	public Type getFunctionalType() {
		Type t = new Type(parser,"function");
		t.setReturnType(returnType.getType());
		if (parameterTypes != null) {
			for (TypeVariable v : parameterTypes) {
				t.addTypeParameter(v.getType());
			}
		}
		return t;
	}

}