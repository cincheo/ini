package ini.eval.data;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import ini.ast.TypeVariable;
import ini.parser.Types;
import ini.type.Type;

public interface Data extends Comparable<Object> {

	public enum Kind {
		REGULAR, INT_SET, EXECUTABLE
	}

	static final String UPPER_BOUND_KEY = "upperBound";
	static final String LOWER_BOUND_KEY = "lowerBound";

	boolean isExecutable();
	
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

	//RuntimeConstructor getConstructor();
	 
	//void setConstructor(RuntimeConstructor constructor);

	/**
	 * This is used for the "matches" operator. 
	 */
	TypeVariable getConstructor();
	 
	/**
	 * This is used for the "matches" operator. 
	 */
	void setConstructor(TypeVariable constructor);

	Type getRuntimeType(Types types);
	
	void addDataObserver(DataObserver observer);

	void clearDataObservers();
	
	void addDataObservers(List<DataObserver> observers);
	
	List<DataObserver> getDataObservers();

	/**
	 * Gets the data only if available (may return null or block depending on the implementation).
	 */
	Data getIfAvailable();
	
	/**
	 * Non-blocking way to know if a data has an available value.
	 */
	boolean isAvailable();
	
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