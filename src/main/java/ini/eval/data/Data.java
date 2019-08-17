package ini.eval.data;

import ini.ast.Constructor;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public interface Data {

	public enum Kind {
		REGULAR, INT_SET, FUNCTIONAL
	}

	static final String UPPER_BOUND_KEY = "upperBound";
	static final String LOWER_BOUND_KEY = "lowerBound";

	boolean isPrimitive();
	
	boolean isUndefined();

	boolean isBoolean();

	boolean isNumber();
	
	boolean isTrueOrDefined();

	boolean isIndexedSet();

	boolean isArray();
	
	Object minIndex();

	Object maxIndex();

	<T> T getValue();

	void setValue(Object value);

	Boolean getBoolean();

	Number getNumber();

	void copyData(Data data);

	void set(Object key, Data value);

	Data get(Object key);

	String toString();

	String toPrettyString();

	String toJson();

	void prettyPrint(PrintStream out);

	Map<Object, Data> getReferences();

	void setReferences(Map<Object, Data> references);

	int getSize();

	Kind getKind();

	void setKind(Kind kind);

	Constructor getConstructor();
	
	void setConstructor(Constructor constructor);
	
	void addDataObserver(DataObserver observer);

	void clearDataObservers();
	
	void addDataObservers(List<DataObserver> observers);
	
	List<DataObserver> getDataObservers();

	/**
	 * Equals by value (including maps).
	 */
	boolean equals(Object object);

	/**
	 * Gets the key associated to the data in the reference map.
	 */
	Object keyOf(Data data);

	/**
	 * Gets the subarray between min and max indexes.
	 */
	Data subArray(int min, int max);
	
	Data concat(Data data);
	
	Data rest();
	
	Data first();
	
	int getTypeInfo();
	
}