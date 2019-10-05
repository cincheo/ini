package ini.ast;

import ini.parser.IniParser;
import ini.type.AstAttrib;
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

	public boolean isLocal() {
		return member != null;
	}

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
			if (returnType != null) {
				returnType.context = typeParameters;
			}
		}
		this.className = getAnnotationValue("class");
		this.member = getAnnotationValue("target");
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

	public boolean match(AstAttrib attrib, Invocation invocation) {
		// TODO: also check types ;)
		return (invocation.arguments == null ? 0 : invocation.arguments.size()) == (parameterTypes == null ? 0
				: parameterTypes.size());
	}

	public Type getFunctionalType(AstAttrib attrib) {
		// clear context to get fresh type variables and create constraints
		if (typeParameters != null) {
			for (TypeVariable tv : typeParameters) {
				tv.type = null;
				if (tv.superType != null) {
					attrib.addTypingConstraint(ini.type.TypingConstraint.Kind.LTE, tv.getType(), tv.superType.getType(),
							this, null);
				}
			}
		}
		Type type = new Type(parser.types, "function");
		// TODO: recursive type params substitution
		type.setReturnType(returnType.lookupTypeVariable(returnType.name) != null
				? returnType.lookupTypeVariable(returnType.name) : returnType.getType());
		if (parameterTypes != null) {
			for (TypeVariable v : parameterTypes) {
				// System.out.println(" - "+v+" -
				// "+v.lookupTypeVariable(v.name));
				type.addTypeParameter(
						// TODO: recursive type params substitution
						v.lookupTypeVariable(v.name) != null ? v.lookupTypeVariable(v.name) : v.getType());
			}
		}
		//System.out.println("FUNCTIONAL TYPE FOR " + this + " : " + type);
		return type;
	}

}
