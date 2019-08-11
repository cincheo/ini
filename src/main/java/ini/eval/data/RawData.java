package ini.eval.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ini.ast.Assignment;
import ini.ast.BooleanLiteral;
import ini.ast.Constructor;
import ini.ast.Expression;
import ini.ast.ListExpression;
import ini.ast.NumberLiteral;
import ini.ast.StringLiteral;
import ini.ast.Variable;
import ini.broker.TypeInfo;
import ini.parser.IniParser;
import ini.type.Type;

public class RawData implements Data {

	// for serialization / deserialization
	private int typeInfo = 0;

	private Object value;

	private Map<Object, Data> references;

	private Kind kind = Kind.REGULAR;

	private transient boolean explodedString = false;

	private transient List<DataObserver> dataObservers;

	private transient Constructor constructor;

	@Override
	public Constructor getConstructor() {
		return constructor;
	}

	@Override
	public void setConstructor(Constructor constructor) {
		this.constructor = constructor;
	}

	@Override
	public void addDataObserver(DataObserver observer) {
		if (dataObservers == null) {
			dataObservers = new ArrayList<DataObserver>();
		}
		dataObservers.add(observer);
	}

	@Override
	public void addDataObservers(List<DataObserver> observers) {
		if (dataObservers == null) {
			dataObservers = new ArrayList<DataObserver>();
		}
		dataObservers.addAll(observers);
	}

	@Override
	public void clearDataObservers() {
		if (dataObservers != null) {
			dataObservers.clear();
		}
	}

	@Override
	public List<DataObserver> getDataObservers() {
		return dataObservers;
	}

	private void notifyDataObservers(Object oldValue) {
		if (dataObservers == null)
			return;
		for (DataObserver dataObserver : dataObservers) {
			dataObserver.valueUpdated(this, oldValue);
		}
	}

	private void notifyDataObserversForCopy(Data oldData) {
		if (dataObservers == null)
			return;
		for (DataObserver dataObserver : dataObservers) {
			dataObserver.dataCopied(new RawData(value), oldData);
		}
	}

	private void notifyDataObserversForReferences(Map<Object, Data> oldReferences) {
		if (dataObservers == null)
			return;
		for (DataObserver dataObserver : dataObservers) {
			dataObserver.referencesUpdated(this, oldReferences);
		}
	}

	private void notifyDataObservers(Object key, Data oldData) {
		if (dataObservers == null)
			return;
		for (DataObserver dataObserver : dataObservers) {
			dataObserver.referenceUpdated(this, key, oldData);
		}
	}

	public static Data objectToData(Object object) {
		if (object == null) {
			return new RawData(null);
		}
		if (object.getClass().isArray()) {
			object = Arrays.asList((Object[]) object);
		}
		if (object instanceof Data) {
			return (Data) object;
		} else if (object instanceof List) {
			List<?> l = (List<?>) object;
			Data d = new RawData(null);
			d.setReferences(new HashMap<Object, Data>());
			for (int i = 0; i < l.size(); i++) {
				d.set(i, objectToData(l.get(i)));
			}
			return d;
		} else if (object instanceof Set) {
			Set<?> s = (Set<?>) object;
			Data d = new RawData(null);
			d.setReferences(new HashMap<Object, Data>());
			for (Object o : s) {
				Data dd = objectToData(o);
				d.set(dd, dd);
			}
			return d;
		} else if (object instanceof Map) {
			Map<?, ?> m = (Map<?, ?>) object;
			Data d = new RawData(null);
			d.setReferences(new HashMap<Object, Data>());
			for (Entry<?, ?> e : m.entrySet()) {
				d.set(e.getKey(), objectToData(e.getValue()));
			}
			return d;
		} else {
			return new RawData(object);
		}
	}

	public static RawData rawCopy(Data data) {
		RawData d = new RawData(null);
		d.copyData(data);
		return d;
	}

	public static Expression dataToExpression(IniParser parser, Data data) {
		Object value = data.getValue();
		if (data.getReferences() == null || data.getReferences().isEmpty()) {
			if (value instanceof String) {
				return new StringLiteral(parser, null, (String) value);
			} else if (value instanceof Number) {
				return new NumberLiteral(parser, null, (Number) value);
			} else if (value instanceof Boolean) {
				return new BooleanLiteral(parser, null, (Boolean) value);
			} else {
				throw new RuntimeException("unhandled data");
			}
		} else {
			if (data.isIndexedSet() && ((Integer) data.minIndex()) > 0) {
				List<Expression> l = new ArrayList<Expression>();
				ListExpression expression = new ListExpression(parser, null, l);
				for (int i = 0; i < (Integer) data.maxIndex(); i++) {
					if (i < (Integer) data.minIndex()) {
						l.add(null);
					} else {
						l.add(dataToExpression(parser, data.getReferences().get(i)));
					}
				}
				return expression;
			} else {
				List<Expression> l = new ArrayList<Expression>();
				ListExpression expression = new ListExpression(parser, null, l);
				for (Entry<Object, Data> e : data.getReferences().entrySet()) {
					l.add(new Assignment(parser, null, new Variable(parser, null, (String) e.getKey()),
							dataToExpression(parser, e.getValue())));
				}
				return expression;
			}
		}

	}

	public static Object dataToObject(Type type, Data data) {
		if (data.isArray() && type != null && type.getName().equals("Map")) {
			try {
				Object array = Array.newInstance(type.getTypeParameters().get(1).toJavaType(), data.getSize());
				if (data.getReferences() != null) {
					for (int i = 0; i < (Integer) data.maxIndex(); i++) {
						Data d = data.getReferences().get(i);
						if (d != null) {
							Array.set(array, i, dataToObject(null, d));
						}
					}
				}
				return array;
			} catch (NegativeArraySizeException e) {
				e.printStackTrace();
			}
		}
		Object value = data.getValue();
		if (data.getReferences() == null || data.getReferences().isEmpty()) {
			return value;
		} else {
			if (data.isIndexedSet() && ((Integer) data.minIndex()) > 0) {
				List<Object> l = new ArrayList<Object>();
				for (int i = 0; i < (Integer) data.maxIndex(); i++) {
					if (i < (Integer) data.minIndex()) {
						l.add(null);
					} else {
						l.add(dataToObject(null, data.getReferences().get(i)));
					}
				}
				return l;
			} else {
				Map<Object, Object> m = new HashMap<Object, Object>();
				for (Entry<Object, Data> e : data.getReferences().entrySet()) {
					m.put(e.getKey(), dataToObject(null, e.getValue()));
				}
				return m;
			}
		}
	}

	/*
	 * @SuppressWarnings("unchecked") public static Object dataToObject(Class
	 * type, Data data) { if (type == null) { return dataToObject(data); } if
	 * (type.isPrimitive() || type == String.class ||
	 * Number.class.isAssignableFrom(type) || type == Boolean.class) { return
	 * data.getValue(); } if (List.class.isAssignableFrom(type)) { List<Object>
	 * l = new ArrayList<Object>(); for (int i = 0; i <= (Integer)
	 * data.maxIndex(); i++) { if (i < (Integer) data.minIndex()) { l.add(null);
	 * } else { l.add(dataToObject(data.getReferences().get(i))); } } return l;
	 * }
	 * 
	 * if (Map.class.isAssignableFrom(type)) { Map<Object, Object> m = new
	 * HashMap<Object, Object>(); for (Entry<Object, Data> e :
	 * data.getReferences().entrySet()) { m.put(e.getKey(),
	 * dataToObject(e.getValue())); } return m; }
	 * 
	 * if (type.isArray()) { Class ct = type.getComponentType(); Object array =
	 * Array.newInstance(ct, (Integer) data.maxIndex()); for (int i = 0; i <=
	 * (Integer) data.maxIndex(); i++) { if (i < (Integer) data.minIndex()) {
	 * Array.set(array, i, null); } else { Array.set(array, i, dataToObject(ct,
	 * data.getReferences() .get(i))); } } return array; }
	 * 
	 * throw new RuntimeException("cannot convert data to object"); }
	 */

	public RawData(Object value) {
		setValue(value);
	}

	public boolean isUndefined() {
		return (value == null) && references == null;
	}

	public boolean isBoolean() {
		return (value instanceof Boolean);
	}

	@Override
	public boolean isNumber() {
		return (value instanceof Number);
	}

	public boolean isTrueOrDefined() {
		if (isBoolean()) {
			return (Boolean) value;
		} else {
			return !isUndefined();
		}
	}

	public boolean isIndexedSet() {
		if (references == null)
			return false;
		for (Object o : references.keySet()) {
			if (!(o instanceof Integer)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * If this data is an integer set, tries to transform all keys into integers
	 * to make it a list. This is used mostly because JSON serialization of data
	 * will loose integer keys and transform them to strings. So we have to
	 * transform them back to integers if possible.
	 * 
	 * @return
	 */
	public RawData tryNumerizeKeys() {
		if (references != null && kind == Kind.INT_SET) {
			Map<Object, Data> newRefs = new HashMap<>();
			boolean numerizable = true;
			for (Entry<Object, Data> entry : references.entrySet()) {
				try {
					int key = Integer.parseInt(entry.getKey().toString());
					newRefs.put(key, entry.getValue());
				} catch (Exception e) {
					numerizable = false;
					break;
				}
			}
			if (numerizable && references.size() == newRefs.size()) {
				references = newRefs;
			}
		}
		return this;
	}

	public RawData applyTypeInfo() {
		if (value == null || typeInfo == 0) {
			return this;
		}
		switch (typeInfo) {
		case TypeInfo.INTEGER:
			value = ((Number) value).intValue();
			break;
		case TypeInfo.LONG:
			value = ((Number) value).longValue();
			break;
		case TypeInfo.DOUBLE:
			value = ((Number) value).doubleValue();
			break;
		case TypeInfo.FLOAT:
			value = ((Number) value).floatValue();
			break;
		case TypeInfo.STRING:
			break;
		case TypeInfo.BOOLEAN:
			break;
		default:
			System.out.println("NO CONVERSION: " + typeInfo + " / " + value + " / " + value.getClass());
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public Object minIndex() {
		if (references == null) {
			return 0;
		}
		if (references.containsKey(Data.LOWER_BOUND_KEY)) {
			return references.get(Data.LOWER_BOUND_KEY).getValue();
		} else {
			return Collections.min((Set) references.keySet());
		}
	}

	@SuppressWarnings("unchecked")
	public Object maxIndex() {
		if (references == null) {
			return -1;
		}
		if (references.containsKey(Data.UPPER_BOUND_KEY)) {
			return references.get(Data.UPPER_BOUND_KEY).getValue();
		} else {
			return Collections.max((Set) references.keySet());
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue() {
		if (explodedString) {
			implodeString();
		}
		return (T) value;
	}

	public void setValue(Object value) {
		if (explodedString) {
			references.clear();
			explodedString = false;
		}
		Object oldValue = this.value;
		this.value = value;
		this.typeInfo = TypeInfo.getTypeInfoForInstance(value);
		notifyDataObservers(oldValue);
	}

	synchronized private void explodeString() {
		if (value instanceof String) {
			// System.out.println("<<< Exploding string "+value+" >>>");
			String s = (String) value;
			for (int i = 0; i < s.length(); i++) {
				Integer key = (int) i;
				references.put(key, new RawData(s.charAt(i)));
			}
		}
		value = null;
		explodedString = true;
	}

	synchronized private String getImplodedString() {
		if (explodedString) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < references.size(); i++) {
				sb.append((Object) references.get(i).getValue());
			}
			return sb.toString();
		} else {
			throw new RuntimeException("invalid operation on data");
		}
	}

	synchronized private void implodeString() {
		if (explodedString) {
			value = getImplodedString();
			references.clear();
			// System.out.println("<<< Imploded string "+value+" >>>");
			explodedString = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ini.eval.data.Data#getBoolean()
	 */
	public Boolean getBoolean() {
		return value == null ? false : (Boolean) value;
	}

	public Number getNumber() {
		return value == null ? 0 : (Number) value;
	}

	public void copyData(Data data) {
		// System.out.println(this + ":" + data);
		Data oldData = new RawData(this.value);
		this.value = data.getValue();
		this.typeInfo = data.getTypeInfo();
		this.explodedString = false; // because getData implodes the string
		this.kind = data.getKind();
		if (data.getReferences() != null) {
			for (Entry<Object, Data> e : data.getReferences().entrySet()) {
				Data rd = new RawData(null);
				rd.copyData(e.getValue());
				set(e.getKey(), rd);
			}
		}

		notifyDataObserversForCopy(oldData);
	}

	public void set(Object key, Data value) {
		if (key == null) {
			throw new RuntimeException("key cannot be null");
		}
		if (references == null) {
			references = new HashMap<Object, Data>();
		}
		// System.out.println("Putting " + key + " (" + key.getClass() + "), "
		// + value);
		if (!explodedString && (this.value instanceof Character) && (key instanceof Integer)) {
			explodeString();
		}
		Data oldData = references.get(key);
		references.put(key, value);
		notifyDataObservers(key, oldData);
	}

	public Data get(Object key) {
		if (key == null) {
			throw new RuntimeException("key cannot be null");
		}
		/*
		 * if (key instanceof Character) { key = key.toString(); }
		 */
		onMapAccess();
		if (!references.containsKey(key)) {
			references.put(key, new DataReference(null));
		}
		// System.out.println("Getting " + key + " (" + key.getClass() + ")");
		return references.get(key);
	}

	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append("Data(");
		if (value != null)
			ret.append(value/* + ":" + value.getClass().getSimpleName() */);
		if (references != null) {
			ret.append("{");
			for (Entry<Object, Data> e : references.entrySet()) {
				if (e.getKey() != null) {
					if (e.getKey() == e.getValue()) {
						ret.append(e.getKey() + ",");
					} else {
						ret.append(e.getKey()
								/*
								 * + ":" + e.getKey().getClass().getSimpleName()
								 */ + "=" + e.getValue() + ",");
					}
				} else {
					ret.append("null=" + e.getValue() + ",");
				}
			}
			if (!references.isEmpty()) {
				ret.deleteCharAt(ret.length() - 1);
			}
			ret.append("}");
			// ret.append(references.toString());
		}
		ret.append(")");
		return ret.toString();
	}

	public String toJson() {
		StringBuffer ret = new StringBuffer();
		boolean array = isIndexedSet() || isArray();
		if (references != null) {
			ret.append(array ? "[" : "{");
			for (Entry<Object, Data> e : references.entrySet()) {
				if (e.getKey() != null) {
					if (array) {
						ret.append(e.getValue().toJson() + ",");
					} else {
						if (e.getKey() == e.getValue()) {
							ret.append(e.getKey() + ",");
						} else {
							if (!e.getValue().isUndefined()) {
								ret.append(e.getKey() + ":" + e.getValue().toJson() + ",");
							}
						}
					}
				} else {
					ret.append("null=" + e.getValue().toJson() + ",");
				}
			}
			if (!references.isEmpty()) {
				ret.deleteCharAt(ret.length() - 1);
			}
			ret.append(array ? "]" : "}");
		} else {
			if (value != null && Number.class.isAssignableFrom(value.getClass())) {
				ret.append("" + value);
			} else {
				ret.append("\"" + value + "\"");
			}
		}
		return ret.toString();
	}

	public String toPrettyString() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		prettyPrint(new PrintStream(os));
		String s = os.toString();
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}

	public void prettyPrint(PrintStream out) {
		if (explodedString) {
			implodeString();
		}
		if (getValue() != null) {
			out.print((Object) getValue());
		} else {
			if (kind == Data.Kind.INT_SET || isIndexedSet()) {
				if (references != null && references.containsKey(Data.LOWER_BOUND_KEY)) {
					int min = ((Double) minIndex()).intValue();
					int max = ((Double) maxIndex()).intValue();
					out.print("[" + min + ".." + max + "]");
				} else {
					out.print("[");
					int min = ((Number) minIndex()).intValue();
					int max = ((Number) maxIndex()).intValue();
					for (int i = min; i <= max; i++) {
						getReferences().get(i).prettyPrint(out);
						if (i < max) {
							out.print(",");
						}
					}
					out.print("](" + min + ".." + max + ")");
				}
			} else if (getReferences() == null) {
				out.print("null");
			} else {
				out.print(toString());
			}
		}

	}

	public Map<Object, Data> getReferences() {
		return references;
	}

	@Override
	public boolean isArray() {
		return references != null
				&& (!references.containsKey(Data.LOWER_BOUND_KEY) && references.containsKey(Data.UPPER_BOUND_KEY));
	}

	public int getSize() {
		onMapAccess();
		if (isArray()) {
			return ((Integer) references.get(Data.UPPER_BOUND_KEY).getNumber()) + 1;
		}
		if (value instanceof String) {
			return ((String) value).length();
		} else {
			if (references.containsKey(Data.LOWER_BOUND_KEY)) {
				return ((Integer) references.get(Data.UPPER_BOUND_KEY).getNumber()
						- (Integer) references.get(Data.LOWER_BOUND_KEY).getNumber()) + 1;
			}
			return references.size();
		}
	}

	public Kind getKind() {
		return kind;
	}

	public void setKind(Kind type) {
		this.kind = type;
	}

	@Override
	public boolean isPrimitive() {
		return value != null && ((value instanceof Boolean) || (Number.class.isAssignableFrom(value.getClass()))
				|| (value instanceof Character));
	}

	@Override
	public void setReferences(Map<Object, Data> references) {
		Map<Object, Data> oldReferences = this.references;
		this.references = references;
		notifyDataObserversForReferences(oldReferences);
	}

	@Override
	public boolean equals(Object object) {
		Data d = (Data) object;
		if (getValue() != null && d.getValue() != null) {
			return getValue().equals(d.getValue());
		} else if (getReferences() != null && d.getReferences() != null) {
			// test same mapping, then values
			if (getReferences().equals(d.getReferences())) {
				for (Entry<Object, Data> e : getReferences().entrySet()) {
					if (!e.getValue().equals(d.getReferences().get(e.getKey()))) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private void onMapAccess() {
		if (references == null) {
			references = new HashMap<Object, Data>();
		}
		if (!explodedString && (this.value instanceof String)) {
			explodeString();
		}
	}

	@Override
	public Object keyOf(Data data) {
		onMapAccess();
		for (Entry<Object, Data> e : getReferences().entrySet()) {
			if (e.getValue().equals(data)) {
				return e.getKey();
			}
		}
		return null;
	}

	@Override
	public Data subArray(int min, int max) {
		onMapAccess();
		Data d = new RawData(null);
		int key = 0;
		for (int i = min; i <= max; i++) {
			d.set(key++, get(i));
		}
		return d;
	}

	@Override
	public Data concat(Data data) {
		onMapAccess();
		Data res = new RawData(null);
		res.copyData(this);
		if (res.isIndexedSet()) {
			int i1 = res.getSize();
			int max = data.getSize();
			for (int i2 = 0; i2 < max; i2++) {
				res.set(i1++, data.get(i2));
			}
		} else {
			for (Entry<Object, Data> e : data.getReferences().entrySet()) {
				res.set(e.getKey(), e.getValue());
			}
		}
		return res;
	}

	@Override
	public Data first() {
		return get(0);
	}

	@Override
	public Data rest() {
		return subArray(1, getSize() - 1);
	}

	public int getTypeInfo() {
		return typeInfo;
	}

}
