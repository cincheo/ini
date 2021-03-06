package ini.eval.at;

import java.util.HashMap;
import java.util.Map;

import ini.eval.Context;
import ini.eval.IniEval;
import ini.eval.IniThread;
import ini.eval.data.Data;
import ini.eval.data.DataObserver;
import ini.eval.data.DataReference;
import ini.eval.data.RawData;
import ini.type.AstAttrib;

public class AtUpdate extends At {

	String mode = "sync";

	@Override
	public void eval(final IniEval eval) {
		IniThread thread = new IniThread(eval, AtUpdate.this, getRule(), null);
		DataObserver o = new DataObserver() {
			@Override
			public void dataCopied(Data data, Data oldData) {
				notify(data, oldData);
			}

			@Override
			public void referencesUpdated(Data data, Map<Object, Data> oldReferences) {
				// startUpdateThread();
			}

			@Override
			public void referenceUpdated(Data data, Object key, Data oldReferencedData) {
				System.out.println("reference updated");
			}

			@Override
			public void valueUpdated(Data data, Object oldValue) {
				notify(data, RawData.objectToData(oldValue));
			}

			@Override
			public void dataReferenced(Data data, Data oldData) {
				notify(data, oldData);
			}

			void notify(Data data, Data oldData) {
				Map<String, Data> variables = new HashMap<String, Data>();
				variables.put(getAtPredicate().outParameters.get(0).toString(), oldData);
				variables.put(getAtPredicate().outParameters.get(1).toString(), data);
				execute(thread.fork(variables));
			}
		};
		Data d = getInContext().get("mode");
		if (d != null) {
			mode = d.getValue();
		}
		d = getInContext().get("variable");
		if (d instanceof DataReference && ((DataReference) d).isPending()) {
			d.setValue(false);
		}
		try {
			d.addDataObserver(o);
		} catch (Exception ex) {
		}

	}

	@Override
	public boolean checkTerminated() {
		// getThreadExecutor().shutdownNow();
		// return getThreadExecutor().getTaskCount() == 0;
		// System.out.println("update :" + (getThreadExecutor().getActiveCount()
		// == 0));
		return getThreadExecutor().getActiveCount() == 0;
	}

	@Override
	public void evalType(AstAttrib attrib) {
		typeInParameters(attrib, true, attrib.parser.types.ANY, "variable");
		typeInParameters(attrib, false, attrib.parser.types.STRING, "mode");
	}

}
