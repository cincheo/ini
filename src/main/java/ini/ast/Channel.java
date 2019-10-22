package ini.ast;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ini.eval.data.Data;
import ini.eval.data.RawData;
import ini.parser.IniParser;

public class Channel extends NamedElement {

	private static int index = 0;

	public static synchronized String getNextName() {
		return "_ANONYMOUS_" + (index++);
	}

	public enum Visibility {
		PRIVATE, APPLICATION, GLOBAL
	}

	public static final Data STOP_MESSAGE = new RawData("__STOP__");
	public static final Data VOID_MESSAGE = new RawData("__VOID__");

	public TypeVariable typeVariable;
	public boolean indexed = false;
	public Visibility visibility;
	public String mappedName;
	private transient Map<Integer, Channel> components;

	public Channel(IniParser parser, Token token, String name, TypeVariable typeVariable, Visibility visibility,
			boolean indexed, List<Expression> annotations) {
		super(parser, token, name);
		if (name == null) {
			name = getNextName();
		}
		if (typeVariable == null) {
			this.typeVariable = new TypeVariable(parser, token, "Any");
		} else {
			this.typeVariable = typeVariable;
		}
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
		if (typeVariable != null) {
			typeVariable.prettyPrint(out);
		}
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

	@Override
	public void accept(Visitor visitor) {
		visitor.visitChannel(this);
	}

}
