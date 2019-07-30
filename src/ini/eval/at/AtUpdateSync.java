package ini.eval.at;

import ini.ast.AtPredicate;
import ini.ast.Expression;
import ini.ast.Rule;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.eval.data.DataObserver;
import ini.eval.data.DataReference;

import java.util.Map;

public class AtUpdateSync extends At {

	@Override
	public void eval(final IniEval eval) {

		DataObserver o = new DataObserver() {
			@Override
			public void dataCopied(Data data, Data from) {
				eval.eval(getRule());
			}

			@Override
			public void referencesUpdated(Data data,
					Map<Object, Data> oldReferences) {
				// eval.eval(rule);
			}

			@Override
			public void referenceUpdated(Data data, Object key,
					Data oldReferencedData) {
				System.out.println("reference updated");
			}

			@Override
			public void valueUpdated(Data data, Object oldValue) {
				eval.eval(getRule());
			}

			@Override
			public void dataReferenced(Data data, Data oldData) {
				eval.eval(getRule());
			}

		};
		for (Expression e : getAtPredicate().inParameters) {
			Data d = eval.eval(e);
			if (d instanceof DataReference && ((DataReference) d).isPending()) {
				d.setValue(false);
			}
			try {
				d.addDataObserver(o);
			} catch (Exception ex) {
				throw new RuntimeException("cannot observe updates for '" + e
						+ "' (variable is probably undefined)", ex);
			}
		}

	}

	@Override
	public boolean checkTerminated() {
		return terminated;
	}

}
