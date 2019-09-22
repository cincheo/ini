package ini.ast;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ini.parser.IniParser;

public class Channel extends NamedElement {

	public enum Visibility {
		PRIVATE, APPLICATION, GLOBAL
	}

	public TypeVariable typeVariable;
	public boolean indexed = false;
	public Visibility visibility;
	public String mappedName;
	private transient Map<Integer, Channel> components;

	public Channel(IniParser parser, Token token, String name, TypeVariable typeVariable, Visibility visibility,
			boolean indexed, List<Expression> annotations) {
		super(parser, token, name);
		this.typeVariable = typeVariable;
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
		typeVariable.prettyPrint(out);
		out.print(")");
		if (annotations != null) {
			out.print(" " + annotations);
		}
	}

	public Channel getComponent(int i) {
		if (!indexed) {
			throw new RuntimeException("cannot access component on non-indexed channel");
		}
		if (components == null) {
			components = new HashMap<Integer, Channel>();
		}
		if (!components.containsKey(i)) {
			components.put(i, new Channel(parser, token, mappedName + i, typeVariable, visibility, false, null));
		}
		return components.get(i);
	}

}
