package ini.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import ini.parser.Types;

/**
 * A type checker validates a set of typing constraints and makes sure that
 * there is no inconsistency. It implements a Herbrand unification algorithm.
 * 
 * @author Renaud Pawlak
 */
public class TypeChecker {

	private Types types;
	private List<TypingConstraint> constraints;
	private boolean strict;

	/**
	 * Creates a new type checker.
	 * 
	 * @param strict
	 *            tells the type checker to strictly enforce equality
	 *            constraints between subtypes
	 * @param types
	 *            the type system to by used
	 * @param constraints
	 *            the typing constraints to be checked
	 */
	public TypeChecker(boolean strict, Types types, TypingConstraint... constraints) {
		this.strict = strict;
		this.types = types;
		if (constraints == null || constraints.length == 0) {
			this.constraints = new ArrayList<>();
		} else {
			this.constraints = new ArrayList<>(Arrays.asList(constraints));
		}
	}

	/**
	 * Creates a new type checker.
	 * 
	 * @param strict
	 *            tells the type checker to strictly enforce equality
	 *            constraints between subtypes
	 * @param types
	 *            the type system to by used
	 * @param constraints
	 *            the typing constraints to be checked
	 */
	public TypeChecker(boolean strict, Types types, List<TypingConstraint> constraints) {
		this.strict = strict;
		this.types = types;
		this.constraints = constraints;
	}

	/**
	 * Adds a typing constraint to the constraint set.
	 */
	public void addTypingConstraint(TypingConstraint constraint) {
		this.constraints.add(constraint);
	}

	/**
	 * Checks the typing constraint set by applying type unification.
	 * 
	 * @param errorHandler
	 *            upcalled when a typing error is detected (may be called
	 *            several times for a given constraint set)
	 */
	public void unify(Consumer<TypingError> errorHandler) {
		for (TypingConstraint c : new ArrayList<TypingConstraint>(constraints)) {
			if (c.left == null || c.right == null) {
				constraints.remove(c);
			}
		}

		// do substitution
		boolean allUsed = false;
		while (!allUsed) {
			allUsed = true;
			for (TypingConstraint substitution : constraints) {
				substitution.normalize();
				if (substitution.kind != TypingConstraint.Kind.EQ) {
					continue;
				}
				if ((!substitution.left.isVariable() && !substitution.right.isVariable()) || substitution.used) {
					continue;
				}
				for (TypingConstraint current : constraints) {
					current.substitute(substitution);
				}
				allUsed = false;
				substitution.used = true;
			}
			// System.out.println("===> after substitution");
			// printConstraints(System.out);
			simplify(errorHandler);
			// System.out.println("===> after simplification");
			// printConstraints(System.out);
		}
		for (TypingConstraint c : constraints) {
			if (!c.left.isVariable() && !c.right.isVariable() && !c.left.equals(c.right)) {
				errorHandler.accept(new TypingError(c.leftOrigin,
						"type mismatch: '" + c.left + "' is not compatible with '" + c.right + "'"));
				if (c.leftOrigin != c.rightOrigin) {
					errorHandler.accept(new TypingError(c.rightOrigin,
							"type mismatch: '" + c.left + "' is not compatible with '" + c.right + "'"));
				}
			}
		}

		// System.err.println("==================");
		// System.err.println("has errors: " + hasErrors());
		// printConstraints("", System.err);

	}

	private void simplify(Consumer<TypingError> errorHandler) {
		boolean simplified = true;
		while (simplified) {
			simplified = false;
			for (TypingConstraint c : new ArrayList<TypingConstraint>(constraints)) {
				// remove tautologies
				if (c.left.equals(c.right)) {
					constraints.remove(c);
					simplified = true;
					continue;
				}
				if (c.kind != TypingConstraint.Kind.EQ) {
					if (!c.left.isVariable() && !c.left.isVariable()) {
						// System.out.println("===> " + c + "===> "
						// + c.left.isLTE(c.right) + "===> "
						// + c.left.isGTE(c.right));
						// System.out.println("===> " + c.left.superType);
						if (c.kind == TypingConstraint.Kind.LTE && c.left.isLTE(c.right)) {
							constraints.remove(c);
						}
						if (c.kind == TypingConstraint.Kind.GTE && c.left.isGTE(c.right)) {
							constraints.remove(c);
						}
					}
					if (c.left.hasFields() || c.right.hasFields()) {
						c.kind = TypingConstraint.Kind.EQ;
					}
					// continue;
				}

				// skip
				if (((c.left.getFields() == null || c.left.getFields().isEmpty())
						&& (c.right.getFields() == null || c.right.getFields().isEmpty()))
						&& (c.left.isVariable() && c.left.getName() != null && c.left.getName().startsWith("_")
						/*
						 * || c.left.isVariable() &&
						 * c.left.getName().startsWith("_")
						 */)) {
					continue;
				}
				List<TypingError> errors = new ArrayList<TypingError>();
				List<TypingConstraint> subConstraints = c.reduce(strict, types, errors);
				if (!errors.isEmpty()) {
					for (TypingError e : errors) {
						errorHandler.accept(e);
					}
				} else if (!subConstraints.isEmpty()) {
					constraints.remove(c);
					constraints.addAll(subConstraints);
					simplified = true;
				}
			}
		}
	}

	/**
	 * Gets the typing constraints.
	 */
	public List<TypingConstraint> getConstraints() {
		return constraints;
	}

	/**
	 * Sets the typing constraints.
	 */
	public void setConstraints(List<TypingConstraint> constraints) {
		this.constraints = constraints;
	}

}
