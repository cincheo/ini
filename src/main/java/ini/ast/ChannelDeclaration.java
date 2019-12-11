package ini.ast;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ini.broker.ChannelConfiguration;
import ini.eval.data.Data;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.parser.Types;
import ini.type.Type;

public class ChannelDeclaration extends NamedElement {

	private static long localId = 1;

	public static long getLocalId() {
		return localId++;
	}

	public enum Visibility {
		LOCAL, APPLICATION, GLOBAL
	}

	public static final Data STOP_MESSAGE = new RawData("__STOP__");
	public static final Data VOID_MESSAGE = new RawData("__VOID__");

	public TypeVariable typeVariable;
	public boolean indexed = false;
	public Visibility visibility;
	public String mappedName;
	public Integer size;
	private transient Map<Integer, ChannelDeclaration> components;

	public ChannelDeclaration(IniParser parser, Token token, String name, TypeVariable typeVariable,
			Visibility visibility, boolean indexed, List<Expression> annotations) {
		super(parser, token, name);
		if (name == null) {
			throw new RuntimeException("channel name cannot be null");
		}
		if (typeVariable == null) {
			this.typeVariable = new TypeVariable(parser, token, "Any");
		} else {
			this.typeVariable = typeVariable;
		}
		this.visibility = visibility == null ? Visibility.LOCAL : visibility;
		this.indexed = indexed;
		this.annotations = annotations;
		this.size = (Integer) getAnnotationNumberValue("capacity", "size");
		this.mappedName = getAnnotationStringValue("name");
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

	@Override
	public Type getType() {
		if (this.type != null) {
			return this.type;
		}
		Type t = parser.types.createDependentType(Types.CHANNEL_TYPE_NAME, typeVariable.getType());
		if (indexed) {
			this.type = parser.types.createArrayType(t);
		} else {
			this.type = t;
		}
		return this.type;
	}

	public ChannelDeclaration getComponent(int i) {
		if (!indexed) {
			throw new RuntimeException("cannot access component on non-indexed channel");
		}
		if (components == null) {
			components = new HashMap<Integer, ChannelDeclaration>();
		}
		if (!components.containsKey(i)) {
			components.put(i, new ChannelDeclaration(parser, token, mappedName + i, typeVariable, visibility, false,
					annotations));
		}
		return components.get(i);
	}

	public ChannelConfiguration getChannelConfiguration() {
		if (indexed) {
			throw new RuntimeException("illegal operation on indexed channels");
		}
		if (size != null) {
			return new ChannelConfiguration(size);
		} else {
			return null;
		}
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitChannel(this);
	}

}
