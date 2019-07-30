package ini.eval.at;

import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.eval.data.DataObserver;
import ini.eval.data.DataReference;
import ini.eval.data.RawData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AtUpdate extends At {
	@Override
	public void eval(final IniEval eval) {

		DataObserver o = new DataObserver() {
			@Override
			public void dataCopied(Data data, Data oldData) {
				startUpdateThread(data, oldData);
			}

			@Override
			public void referencesUpdated(Data data,
					Map<Object, Data> oldReferences) {
				// startUpdateThread();
			}

			@Override
			public void referenceUpdated(Data data, Object key,
					Data oldReferencedData) {
				System.out.println("reference updated");
			}

			@Override
			public void valueUpdated(Data data, Object oldValue) {
				startUpdateThread(data, RawData.objectToData(oldValue));
			}

			@Override
			public void dataReferenced(Data data, Data oldData) {
				startUpdateThread(data, oldData);
			}

			void startUpdateThread(Data data, Data oldData) {
				Map<String, Data> variables = new HashMap<String, Data>();
				variables.put(getAtPredicate().outParameters.get(0).toString(),
						oldData);
				variables
						.put(getAtPredicate().outParameters.get(1).toString(), data);
				execute(eval, variables);
				// terminate();
			}
		};
		//for (Expression e : getAtPredicate().inParameters) {
//			Expression e = getAtPredicate().inParameters.get(0);
//			Assignment a = (Assignment)e;
			Data d = getInContext().get("variable");
			if (d instanceof DataReference && ((DataReference) d).isPending()) {
				d.setValue(false);
			}
			try {
				d.addDataObserver(o);
			} catch (Exception ex) {
			}
		//}

	}

	@Override
	public boolean checkTerminated() {
		//getThreadExecutor().shutdownNow();
		//return getThreadExecutor().getTaskCount() == 0;
		return getThreadExecutor().getActiveCount() == 0;
	}

}
