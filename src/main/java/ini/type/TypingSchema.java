package ini.type;

import java.util.ArrayList;
import java.util.List;

public class TypingSchema implements TypingElement {

	public List<Type> typeParameters = new ArrayList<>();
	public List<Type> typeArguments = new ArrayList<>();
	public List<TypingConstraint> typingConstraints = new ArrayList<>();

	public TypingSchema() {
		super();
	}

	@Override
	public String toString() {
		String s = "";
		s += "S" + typeParameters + " {\n";
		for (TypingConstraint tc : typingConstraints) {
			s += ("  " + tc + "\n");
		}
		s += "}\n";
		return s;
	}

	private Type substitute(Type type) {
		int index = typeParameters.indexOf(type);
		if (index >= 0) {
			return typeArguments.get(index);
		}
		return type;
	}

	public List<TypingConstraint> apply() {
		List<TypingConstraint> result = new ArrayList<>();
		for (TypingConstraint tc : typingConstraints) {
			result.add(new TypingConstraint(tc.kind, substitute(tc.left), substitute(tc.right), tc.leftOrigin,
					tc.rightOrigin));
		}
		return result;
	}

}
