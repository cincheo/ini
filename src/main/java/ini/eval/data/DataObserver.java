package ini.eval.data;

import java.util.Map;

public interface DataObserver {
	void valueUpdated(Data data, Object oldValue);
	void referenceUpdated(Data data, Object key, Data oldReferencedData);
	void dataCopied(Data data, Data oldData);
	void referencesUpdated(Data data, Map<Object,Data> oldReferences);
	void dataReferenced(Data data, Data oldData);
}
