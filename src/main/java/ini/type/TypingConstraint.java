package ini.type;

import ini.ast.AstNode;
import ini.parser.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TypingConstraint {

	public enum Kind {
		EQ, LTE, GTE,
	}

	public Type left;
	public Type right;
	public Kind kind;
	public AstNode leftOrigin;
	public AstNode rightOrigin;
	public boolean used = false;

	public TypingConstraint(Kind kind, Type left, Type right, AstNode leftOrigin, AstNode rightOrigin) {
		super();
		this.kind = kind;
		this.left = left;
		this.right = right;
		this.leftOrigin = leftOrigin;
		if (rightOrigin == null) {
			rightOrigin = leftOrigin;
		}
		this.rightOrigin = rightOrigin;
	}

	@Override
	public String toString() {
		String s = "";
		if (used) {
			s += "*";
		}
		s += "[" + (left == null ? "null" : left.getFullName());
		switch (kind) {
		case EQ:
			s = s + " = " + (right == null ? "null" : right.getFullName());
			break;
		case LTE:
			s = s + " <= " + (right == null ? "null" : right.getFullName());
			break;
		case GTE:
			s = s + " >= " + (right == null ? "null" : right.getFullName());
			break;
		}
		s = s + (leftOrigin != null ? " at '" + leftOrigin.toString() + "'"
				+ (leftOrigin.token() != null ? " " + leftOrigin.token().getLocation() : "") : "");
		s = s + (rightOrigin != null && rightOrigin != leftOrigin ? " and at '" + rightOrigin.toString() + "'"
				+ (rightOrigin.token() != null ? " " + rightOrigin.token().getLocation() : "") : "");
		s += "]";
		return s;
	}

	public TypingConstraint deepCopy() {
		TypingConstraint copy = new TypingConstraint(this.kind, this.left == null ? null : this.left.deepCopy(),
				this.right == null ? null : this.right.deepCopy(), this.leftOrigin, this.rightOrigin);
		return copy;
	}

	public void substitute(TypingConstraint substitution) {
		if (this == substitution) {
			return;
		}
		if (left.hasVariablePart()) {
			left.substitute(substitution);
			if (left.equals(substitution.left)) {
				left = substitution.right;
				leftOrigin = substitution.rightOrigin;
			}
		}
		if (right.hasVariablePart()) {
			right.substitute(substitution);
			if (right.equals(substitution.left)) {
				right = substitution.right;
				rightOrigin = substitution.rightOrigin;
			}
		}
	}

	public void normalize() {
		if ((!left.isVariable() && right.isVariable()) || (!left.hasVariablePart() && right.hasVariablePart())) {
			Type tmp = left;
			left = right;
			right = tmp;
			AstNode tmpNode = leftOrigin;
			leftOrigin = rightOrigin;
			rightOrigin = tmpNode;
			if (kind == Kind.GTE) {
				kind = Kind.LTE;
			} else if (kind == Kind.LTE) {
				kind = Kind.GTE;
			}
		}
	}

	public List<TypingConstraint> reduce(Types types, List<TypingError> errors) {
		List<TypingConstraint> result = new ArrayList<TypingConstraint>();
		// remove super type equalities...
		if (left.superType == right) {
			result.add(new TypingConstraint(Kind.EQ, left, left, leftOrigin, leftOrigin));
			return result;
		}
		if (right.superType == left) {
			result.add(new TypingConstraint(Kind.EQ, right, right, rightOrigin, rightOrigin));
			return result;
		}
		if (left.superType != null && right.superType != null && left.superType == right.superType) {
			result.add(new TypingConstraint(Kind.EQ, left, left, leftOrigin, leftOrigin));
			return result;
		}
		if (left.hasVariablePart() || right.hasVariablePart()) {
			if (left.getName() != null && left.getName().equals(right.getName())) {
				if (left.getTypeParameters().size() == right.getTypeParameters().size()) {
					for (int i = 0; i < left.getTypeParameters().size(); i++) {
						TypingConstraint c = new TypingConstraint(Kind.EQ, left.getTypeParameters().get(i),
								right.getTypeParameters().get(i), this.leftOrigin, this.rightOrigin);
						result.add(c);
						/*
						 * List<TypingConstraint> subresult = c.reduce(errors);
						 * if (errors.isEmpty()) { if (subresult.isEmpty()) {
						 * result.add(c); } else { result.addAll(subresult); } }
						 */
					}
					if (left.isFunctional()) {
						TypingConstraint c = new TypingConstraint(Kind.EQ, left.getReturnType(), right.getReturnType(),
								this.leftOrigin, this.rightOrigin);
						result.add(c);
						/*
						 * List<TypingConstraint> subresult = new
						 * ArrayList<TypingConstraint>(); subresult =
						 * c.reduce(errors); if (errors.isEmpty()) { if
						 * (subresult.isEmpty()) { result.add(c); } else {
						 * result.addAll(subresult); } }
						 */
					}
				} else {
					errors.add(new TypingError(leftOrigin,
							"type mismatch: '" + left + "' is not compatible with '" + right + "'"));
					if (leftOrigin != rightOrigin) {
						errors.add(new TypingError(rightOrigin,
								"type mismatch: '" + left + "' is not compatible with '" + right + "'"));
					}
				}
			} else {
				if (left.getName() != null && right.getName() != null
						&& !(left == types.ANY || left.getName().startsWith("_"))
						&& !(right == types.ANY || right.getName().startsWith("_"))) {
					errors.add(new TypingError(leftOrigin,
							"type mismatch: '" + left + "' is not compatible with '" + right + "'"));
					if (leftOrigin != rightOrigin) {
						errors.add(new TypingError(rightOrigin,
								"type mismatch: '" + left + "' is not compatible with '" + right + "'"));
					}
				}
			}
			if (left.hasFields()) {
				if (left.isVariable() && !right.isVariable()) {
					for (String fieldName : left.fields.keySet()) {
						if (right.fields == null || !right.fields.containsKey(fieldName)) {
							errors.add(new TypingError(leftOrigin,
									"undeclared field '" + fieldName + "' in type '" + right.name + "'"));
						}
					}
					// return result;
				}
				List<String> reducedFields = new ArrayList<String>();
				reduceFields(reducedFields, result, leftOrigin, rightOrigin, left, right, errors);
				reduceFields(reducedFields, result, leftOrigin, rightOrigin, right, left, errors);
			}
		}
		return result;
	}

	static void reduceFields(List<String> reducedFields, List<TypingConstraint> result, AstNode leftOrigin,
			AstNode rightOrigin, Type left, Type right, List<TypingError> errors) {
		if (left.getFields() != null) {
			for (Map.Entry<String, Type> field : left.getFields().entrySet()) {
				if (reducedFields.contains(field.getKey())) {
					continue;
				}
				if (right.getFields() != null) {
					reducedFields.add(field.getKey());
					Type t = right.getFields().get(field.getKey());
					if (t != null) {
						TypingConstraint c = new TypingConstraint(Kind.EQ, field.getValue(), t, leftOrigin,
								rightOrigin);
						result.add(c);
						/*
						 * List<TypingConstraint> subresult = new
						 * ArrayList<TypingConstraint>(); subresult =
						 * c.reduce(errors); if (errors.isEmpty()) { if
						 * (subresult.isEmpty()) { result.add(c); } else {
						 * result.addAll(subresult); } }
						 */
					}
				}
			}
		}
	}

}
