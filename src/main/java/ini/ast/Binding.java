package ini.ast;

import ini.parser.IniParser;
import ini.type.Type;

import java.io.PrintStream;
import java.util.List;

public class Binding extends NamedElement {

	public String className;
	public String member;
	public String memberName;
	public List<TypeVariable> parameterTypes;
	public List<TypeVariable> typeParameters;
	public TypeVariable returnType;

	public enum Kind {
		FIELD, METHOD, CONSTRUCTOR
	}

	public Binding(IniParser parser, Token token, String name, List<TypeVariable> typeParameters,
			List<TypeVariable> parameterTypes, TypeVariable returnType, List<Expression> annotations) {
		super(parser, token, name);
		this.annotations = annotations;
		this.parameterTypes = parameterTypes;
		this.typeParameters = typeParameters;
		this.returnType = returnType;
		if (parameterTypes != null) {
			for (TypeVariable v : parameterTypes) {
				v.context = typeParameters;
			}
			if(returnType!=null) {
				returnType.context = typeParameters;
			}
		}
		this.className = getAnnotationValue("class");
		this.member = getAnnotationValue("member");
		if (member != null) {
			this.memberName = this.member.split("\\(")[0];
		}
		this.nodeTypeId = AstNode.BINDING;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(name + "(");
		prettyPrintList(out, parameterTypes, ",");
		out.print(")=>" + returnType);
		if (annotations != null) {
			out.print(" " + annotations);
		}
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
		return memberName;
	}

	public Type getFunctionalType() {
		if (type == null) {
			type = new Type(parser.types, "function");
			type.setReturnType(returnType.getType());
			if (parameterTypes != null) {
				for (TypeVariable v : parameterTypes) {
					type.addTypeParameter(v.getType());
				}
			}
		}
		return type;
	}

}
