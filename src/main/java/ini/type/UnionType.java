package ini.type;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class UnionType extends Type {

	public List<Type> unionedTypes = new ArrayList<>();

	public static UnionType create(Type t1, Type t2) {
		UnionType result = new UnionType();
		result.addTypes(new Type[] { t1, t2 });
		return result;
	}

	public void addTypes(Type... types) {
		for (Type type : types) {
			if (type instanceof UnionType) {
				addTypes(((UnionType) type).unionedTypes.toArray(new Type[0]));
			} else {
				if (!unionedTypes.contains(type)) {
					unionedTypes.add(type);
				}
			}
		}
	}

	public UnionType() {
		super(null, null);
	}

	@Override
	public Type substitute(List<Type> parameters, List<Type> arguments, List<Type> freeVariables) {
		for (Type t : unionedTypes) {
			t.substitute(parameters, arguments, freeVariables);
		}
		return this;
	}

	@Override
	public void substitute(TypingConstraint substitution) {
		for (Type t : unionedTypes) {
			t.substitute(substitution);
		}
	}

	@Override
	protected Type deepCopy(List<Type> org, List<Type> dest) {
		UnionType copy = new UnionType();
		org.add(this);
		dest.add(copy);
		for (Type t : this.unionedTypes) {
			copy.unionedTypes.add(t.deepCopy(org, dest));
		}
		return copy;
	}

	@Override
	public String getFullName() {
		return StringUtils.join(this.unionedTypes, "|");
	}

	@Override
	public boolean isGTE(Type type) {
		for (Type t : unionedTypes) {
			if (!t.isGTE(type)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isLTE(Type type) {
		for (Type t : unionedTypes) {
			if (!t.isLTE(type)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean equals(Object o) {
		Type type = (Type) o;
		for (Type t : unionedTypes) {
			if (type.equals(t)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isVariable() {
		for (Type t : unionedTypes) {
			if (t.isVariable()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasVariablePart() {
		for (Type t : unionedTypes) {
			if (t.hasVariablePart()) {
				return true;
			}
		}
		return false;
	}

}
