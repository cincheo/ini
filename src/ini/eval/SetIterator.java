package ini.eval;

import ini.ast.SetExpression;
import ini.ast.Variable;
import ini.eval.data.Data;
import ini.eval.data.RawData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SetIterator {

	Context context;
	SetExpression expression;
	Data set;
	int lowerBound;
	int upperBound;
	boolean setDone = false;
	boolean first;
	Map<Variable, Iterator<Data>> varIterators;

	public SetIterator(Context context, SetExpression expression, Data set) {
		if(set.isUndefined()) {
			setDone = true;
			return;
		}
		this.context = context;
		this.expression = expression;
		this.set = set;
		if (set.getKind() == Data.Kind.INT_SET) {
			lowerBound = set.get(Data.LOWER_BOUND_KEY).getNumber().intValue();
			upperBound = set.get(Data.UPPER_BOUND_KEY).getNumber().intValue();
		} else {
			// System.out.println("NEW ITERATOR =======> "+set +" / "+context);
			varIterators = new HashMap<Variable, Iterator<Data>>();
			for (Variable v : expression.variables) {
				varIterators.put(v, set.getReferences().values().iterator());
			}
		}
		init();
	}

	public void init() {
		if (set.getKind() == Data.Kind.INT_SET) {
			for (Variable v : expression.variables) {
				context.bind(v.name, new RawData(lowerBound));
			}
		} else {
			for (Variable v : expression.variables) {
				context.bind(v.name, varIterators.get(v).next());
			}
		}
		setDone = false;
		first = true;
		// System.out.println("IT: created / "+context);
	}

	/**
	 * Goes to the next element in the set.
	 * 
	 * @return false if the end of the set has been reached, true while still an
	 *         element
	 */
	public boolean nextElement() {
		// System.out.println("IT: next element / "+context);
		if (first) {
			first = false;
			return true;
		}
		if (setDone) {
			return false;
		}
		if (set.getKind() == Data.Kind.INT_SET) {
			boolean setDone = false;
			List<Variable> vars = expression.variables;
			// increment the var set value
			for (int i = 0; i < vars.size(); i++) {
				Data d = context.get(vars.get(i).name);
				if (d.getNumber().intValue() < upperBound) {
					d.setValue(d.getNumber().intValue() + 1);
					// System.out.println("IT: incremented "+vars.get(i).name+"
					// / "+context);
					break;
				} else {
					if (i == vars.size() - 1) {
						// System.out.println("IT: full end reached for
						// "+vars.get(i).name+" / "+context);
						setDone = true;
						break;
					} else {
						// System.out.println("IT: partial end reached for
						// "+vars.get(i).name+" / "+context);
						d.setValue(lowerBound);
						// d = context.get(vars.get(i+1).name);
						// d.setValue(d.getDouble()+1);
					}
				}
			}
			return !setDone;
		} else {
			boolean setDone = false;
			List<Variable> vars = expression.variables;
			// increment the var set value
			for (int i = 0; i < vars.size(); i++) {
				Variable v = vars.get(i);
				if (varIterators.get(v).hasNext()) {
					context.bind(v.name, varIterators.get(v).next());
					break;
				} else {
					if (i == vars.size() - 1) {
						// System.out.println("IT: full end reached for
						// "+vars.get(i).name+" / "+context);
						setDone = true;
						break;
					} else {
						// System.out.println("IT: partial end reached for
						// "+vars.get(i).name+" / "+context);
						varIterators.remove(v);
						varIterators.put(v, set.getReferences().values()
								.iterator());
					}
				}
			}
			return !setDone;
		}
	}

}
