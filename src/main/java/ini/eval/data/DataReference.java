package ini.eval.data;

import ini.ast.Constructor;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public class DataReference implements Data {

	Data referencedData;

	public DataReference(Data referencedData) {
		this.referencedData = referencedData;
	}

	@Override
	public Data get(Object key) {
		// automatically fill pending reference
		if (isPending()) {
			referencedData = new RawData(null);
		}
		return referencedData.get(key);
	}

	@Override
	public Boolean getBoolean() {
		if (isPending())
			return false;
		return referencedData.getBoolean();
	}

	@Override
	public Number getNumber() {
		if (isPending())
			return 0;
		return referencedData.getNumber();
	}

	@Override
	public Map<Object, Data> getReferences() {
		if (isPending())
			return null;
		return referencedData.getReferences();
	}

	@Override
	public int getSize() {
		if (isPending())
			return 0;
		return referencedData.getSize();
	}

	@Override
	public <T> T getValue() {
		if (isPending())
			return null;
		return referencedData.getValue();
	}

	@Override
	public boolean isBoolean() {
		if (isPending())
			return false;
		return referencedData.isBoolean();
	}

	@Override
	public boolean isNumber() {
		if (isPending())
			return false;
		return referencedData.isNumber();
	}

	@Override
	public boolean isArray() {
		if (isPending())
			return false;
		return referencedData.isArray();
	}

	@Override
	public boolean isIndexedSet() {
		if (isPending())
			return false;
		return referencedData.isIndexedSet();
	}

	@Override
	public boolean isTrueOrDefined() {
		if (isPending())
			return false;
		return referencedData.isTrueOrDefined();
	}

	@Override
	public boolean isUndefined() {
		if (isPending())
			return true;
		return referencedData.isUndefined();
	}

	@Override
	public Object maxIndex() {
		if (isPending())
			return 0;
		return referencedData.maxIndex();
	}

	@Override
	public Object minIndex() {
		if (isPending())
			return 0;
		return referencedData.minIndex();
	}

	@Override
	public void prettyPrint(PrintStream out) {
		if (isPending()) {
			out.print("null");
		} else {
			referencedData.prettyPrint(out);
		}
	}

	@Override
	public void set(Object key, Data value) {
		// automatically fill pending reference
		if (isPending()) {
			referencedData = new RawData(null);
		}
		referencedData.set(key, value);
	}

	@Override
	public void copyData(Data data) {
		// automatically fill pending reference
		if (isPending()) {
			referencedData = new RawData(null);
		}
		referencedData.copyData(data);
	}

	@Override
	public void setValue(Object value) {
		// automatically fill pending reference
		if (isPending()) {
			referencedData = new RawData(value);
		} else {
			referencedData.setValue(value);
		}
	}

	@Override
	public void setReferences(Map<Object, Data> references) {
		// automatically fill pending reference
		if (isPending()) {
			referencedData = new RawData(null);
		}
		referencedData.setReferences(references);
	}

	@Override
	public String toPrettyString() {
		if (isPending()) {
			return "null";
		}
		return referencedData.toPrettyString();
	}

	@Override
	public String toJson() {
		if (isPending()) {
			return "null";
		}
		return referencedData.toJson();
	}
	
	public boolean isPending() {
		return this.referencedData == null;
	}

	public Kind getKind() {
		if (isPending())
			return null;
		return referencedData.getKind();
	}

	public void setKind(Kind kind) {
		if (isPending())
			throw new RuntimeException("Data reference is pending");
		referencedData.setKind(kind);
	}

	public Constructor getConstructor() {
		if (isPending())
			return null;
		return referencedData.getConstructor();
	}

	public void setConstructor(Constructor constructor) {
		if (isPending())
			throw new RuntimeException("Data reference is pending");
		referencedData.setConstructor(constructor);
	}

	public Data getReferencedData() {
		return referencedData;
	}

	public void setReferencedData(Data referencedData) {
		// if the referenced data was observed, copy the observers to the new
		// data
		if (!isPending()) {
			if (this.referencedData.getDataObservers() != null) {
				for (DataObserver observer : this.referencedData
						.getDataObservers()) {
					if (referencedData.getDataObservers() == null
							|| (referencedData.getDataObservers() != null && !referencedData
									.getDataObservers().contains(observer))) {
						referencedData.addDataObserver(observer);
					}
				}
			}
		}
		Data oldData = this.referencedData;
		this.referencedData = referencedData;
		List<DataObserver> observers = this.referencedData.getDataObservers();
		if (observers != null) {
			for (DataObserver o : observers) {
				o.dataReferenced(referencedData, oldData);
			}
		}
	}

	@Override
	public String toString() {
		if (isPending())
			return "null";
		return referencedData.toString();
	}

	@Override
	public boolean isPrimitive() {
		if (isPending())
			return false;
		return referencedData.isPrimitive();
	}

	@Override
	public void addDataObserver(DataObserver observer) {
		if (isPending())
			throw new RuntimeException("Data reference is pending");
		referencedData.addDataObserver(observer);
	}

	@Override
	public void addDataObservers(List<DataObserver> observers) {
		if (isPending())
			throw new RuntimeException("Data reference is pending");
		referencedData.addDataObservers(observers);
	}

	@Override
	public void clearDataObservers() {
		if (isPending())
			throw new RuntimeException("Data reference is pending");
		referencedData.clearDataObservers();
	}

	@Override
	public List<DataObserver> getDataObservers() {
		if (isPending())
			return null;
		return referencedData.getDataObservers();
	}

	@Override
	public boolean equals(Object object) {
		if (isPending()) {
			return false;
		}
		return referencedData.equals(object);
	}

	@Override
	public Object keyOf(Data data) {
		if (isPending()) {
			return null;
		}
		return referencedData.keyOf(data);
	}

	@Override
	public Data subArray(int min, int max) {
		if (isPending()) {
			return null;
		}
		return referencedData.subArray(min, max);
	}

	@Override
	public Data rest() {
		if (isPending()) {
			return null;
		}
		return referencedData.rest();
	}

	@Override
	public Data first() {
		if (isPending()) {
			return null;
		}
		return referencedData.first();
	}
	
	@Override
	public Data concat(Data data) {
		if (isPending()) {
			return data.concat(this);
		}
		return referencedData.concat(data);
	}

	@Override
	public String getTypeInfo() {
		if (isPending()) {
			return null;
		}
		return referencedData.getTypeInfo();
	}
	
}
