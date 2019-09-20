package ini.ast;

import java.io.PrintStream;
import java.util.List;

import ini.parser.IniParser;

public class Channel extends NamedElement {

	public enum Visibility {
		PRIVATE, APPLICATION, GLOBAL
	}

	public TypeVariable type;
	public boolean indexed = false;
	public Visibility visibility;
	public String mappedName;

	public Channel(IniParser parser, Token token, String name, TypeVariable type, Visibility visibility,
			boolean indexed, List<Expression> annotations) {
		super(parser, token, name);
		this.type = type;
		this.visibility = visibility == null ? Visibility.PRIVATE : visibility;
		this.indexed = indexed;
		this.annotations = annotations;
		this.mappedName = getAnnotationValue("name");
		if (this.mappedName == null) {
			this.mappedName = name;
		}
		this.nodeTypeId = AstNode.CHANNEL;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("channel " + name + "(");
		type.prettyPrint(out);
		out.print(")");
		if (annotations != null) {
			out.print(" " + annotations);
		}
	}

}
