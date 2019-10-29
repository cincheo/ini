package ini.eval.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ini.Main;
import ini.ast.Assignment;
import ini.ast.BooleanLiteral;
import ini.ast.Channel;
import ini.ast.Executable;
import ini.ast.Expression;
import ini.ast.Function;
import ini.ast.ListExpression;
import ini.ast.NumberLiteral;
import ini.ast.StringLiteral;
import ini.ast.Variable;
import ini.eval.EvalException;
import ini.eval.IniEval;
import ini.parser.IniParser;
import ini.type.Type;

public class RawData implements Data {

	/*
	 * public static void main(String[] args) { String test =
	 * "coucou petit crétin ${i++} coucou {f(4)} tutu"; Pattern p =
	 * Pattern.compile("(\\{([^\\}]*)\\})"); Matcher m = p.matcher(test);
	 * StringBuffer result = new StringBuffer(test); result.set while(m.find())
	 * { m.appendReplacement(result, "EVAL("+m.group(2)+")"); }
	 * System.out.println(result); }
	 */

	// for serialization / deserialization
	private int typeInfo = 0;

	private Object value;

	private Map<Object, Data> references;

	private Kind kind = Kind.REGULAR;

	private transient boolean explodedString = false;

	private transient List<DataObserver> dataObservers;

	private RuntimeConstructor constructor;

	@Override
	public boolean isExecutable() {
		return kind == Kind.EXECUTABLE;
	}

	@Override
	public Data getIfAvailable() {
		return this;
	}

	@Override
	public RuntimeConstructor getConstructor() {
		return constructor;
	}

	@Override
	public void setConstructor(RuntimeConstructor constructor) {
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
	synchronized public void addDataObservers(List<DataObserver> observers) {
		if (dataObservers == null) {
			dataObservers = new ArrayList<DataObserver>();
		}
		dataObservers.addAll(observers);
	}

	@Override
	synchronized public void clearDataObservers() {
		if (dataObservers != null) {
			dataObservers.clear();
		}
	}

	@Override
	synchronized public List<DataObserver> getDataObservers() {
		return dataObservers;
	}

	synchronized private void notifyDataObservers(Object oldValue) {
		if (dataObservers == null)
			return;
		for (DataObserver dataObserver : dataObservers) {
			dataObserver.valueUpdated(this, oldValue);
		}
	}

	synchronized private void notifyDataObserversForCopy(Data oldData) {
		if (dataObservers == null)
			return;
		for (DataObserver dataObserver : dataObservers) {
			dataObserver.dataCopied(new RawData(value), oldData);
		}
	}

	synchronized private void notifyDataObserversForReferences(Map<Object, Data> oldReferences) {
		if (dataObservers == null)
			return;
		for (DataObserver dataObserver : dataObservers) {
			dataObserver.referencesUpdated(this, oldReferences);
		}
	}

	synchronized private void notifyDataObservers(Object key, Data oldData) {
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
		} else if (object instanceof List && object.getClass().getName().startsWith("java.")) {
			List<?> l = (List<?>) object;
			Data d = new RawData(null);
			d.setReferences(new Hashtable<Object, Data>());
			for (int i = 0; i < l.size(); i++) {
				d.set(i, objectToData(l.get(i)));
			}
			return d;
		} else if (object instanceof Set && object.getClass().getName().startsWith("java.")) {
			Set<?> s = (Set<?>) object;
			Data d = new RawData(null);
			d.setReferences(new Hashtable<Object, Data>());
			int i = 0;
			for (Object o : s) {
				d.set(i++, objectToData(o));
			}
			return d;
		} else if (object instanceof Map && object.getClass().getName().startsWith("java.")) {
			Map<?, ?> m = (Map<?, ?>) object;
			Data d = new RawData(null);
			d.setReferences(new Hashtable<Object, Data>());
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
			} else if (value instanceof Channel) {
				Channel c = (Channel) value;
				Variable v = new Variable(parser, null, c.name);
				v.channelLiteral = c;
				return v;
			} else if (value instanceof Function) {
				return (Function) value;
			} else {
				if (value != null) {
					throw new RuntimeException("unhandled data: " + value + " - " + value.getClass());
				} else {
					throw new RuntimeException("unhandled data: " + value);
				}
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

	public static Object dataToObject(IniEval eval, Type type, Data data) {
		if (data.isArray() && type != null && type.getName().equals("Map")) {
			try {
				Object array = Array.newInstance(type.getTypeParameters().get(1).toJavaType(), data.getSize());
				if (data.getReferences() != null) {
					for (int i = 0; i < (Integer) data.maxIndex(); i++) {
						Data d = data.getReferences().get(i);
						if (d != null) {
							Array.set(array, i, dataToObject(eval, null, d));
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
			if (value instanceof Function) {
				Type t = eval.attrib.getResolvedType(((Function) value).getType());
				if (t.getTypeParameters().size() == 2 && t.getTypeParameters().get(0).equals(eval.parser.types.VOID)) {
					return new Consumer<Object>() {
						public void accept(Object t) {
							eval.invoke((Function) value, new Object[] { t });
						}
					};
				} else if (t.getTypeParameters().size() == 2
						&& !t.getTypeParameters().get(0).equals(eval.parser.types.VOID)) {
					return new java.util.function.Function<Object, Object>() {
						public Object apply(Object t) {
							return eval.invoke((Function) value, new Object[] { t });
						}
					};
				} else if (t.getTypeParameters().size() == 1
						&& !t.getTypeParameters().get(0).equals(eval.parser.types.VOID)) {
					return new Supplier<Object>() {
						public Object get() {
							return eval.invoke((Function) value, new Object[] {});
						}
					};
				} else if (t.getTypeParameters().size() == 1
						&& t.getTypeParameters().get(0).equals(eval.parser.types.VOID)) {
					return new Runnable() {
						public void run() {
							eval.invoke((Function) value, new Object[] {});
						}
					};
				} else if (t.getTypeParameters().size() == 3
						&& t.getTypeParameters().get(0).equals(eval.parser.types.VOID)) {
					return new BiConsumer<Object, Object>() {
						public void accept(Object t1, Object t2) {
							eval.invoke((Function) value, new Object[] { t1, t2 });
						}
					};
				} else if (t.getTypeParameters().size() == 3
						&& !t.getTypeParameters().get(0).equals(eval.parser.types.VOID)) {
					return new BiFunction<Object, Object, Object>() {
						public Object apply(Object t1, Object t2) {
							return eval.invoke((Function) value, new Object[] { t1, t2 });
						}
					};
				} else {
					throw new EvalException(eval, "Unsupported lambda type convertion: " + t);
				}
			}
			return value;
		} else {
			if (data.isIndexedSet() && ((Integer) data.minIndex()) > 0) {
				List<Object> l = new ArrayList<Object>();
				for (int i = 0; i < (Integer) data.maxIndex(); i++) {
					if (i < (Integer) data.minIndex()) {
						l.add(null);
					} else {
						l.add(dataToObject(eval, null, data.getReferences().get(i)));
					}
				}
				return l;
			} else {
				Map<Object, Object> m = new Hashtable<Object, Object>();
				for (Entry<Object, Data> e : data.getReferences().entrySet()) {
					m.put(e.getKey(), dataToObject(eval, null, e.getValue()));
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

	public RawData() {
	}

	public RawData(Object value) {
		setValue(value);
	}

	synchronized public boolean isUndefined() {
		return (value == null) && references == null;
	}

	public boolean isBoolean() {
		return (value instanceof Boolean);
	}

	@Override
	synchronized public boolean isNumber() {
		return (value instanceof Number);
	}

	synchronized public boolean isTrueOrDefined() {
		if (isBoolean()) {
			return (Boolean) value;
		} else {
			return !isUndefined();
		}
	}

	synchronized public boolean isIndexedSet() {
		if (references == null)
			return false;
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (Object o : references.keySet()) {
			if (!(o instanceof Integer)) {
				return false;
			} else {
				if(((Integer)o).intValue() < min) {
					min = ((Integer)o).intValue();
				}
				if(((Integer)o).intValue() > max) {
					max = ((Integer)o).intValue();
				}
			}
		}
		return (max - min + 1) == references.size() && min == 0;
	}

	/**
	 * If this data is an integer set, tries to transform all keys into integers
	 * to make it a list. This is used mostly because JSON serialization of data
	 * will loose integer keys and transform them to strings. So we have to
	 * transform them back to integers if possible.
	 * 
	 * @return
	 */
	synchronized public RawData tryNumerizeKeys() {
		if (references != null && kind == Kind.INT_SET) {
			Map<Object, Data> newRefs = new Hashtable<>();
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

	synchronized public RawData applyTypeInfo(GsonBuilder b) {
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
		case TypeInfo.CHANNEL:
			value = new Gson().fromJson(new Gson().toJson(value), Channel.class);
			break;
		case TypeInfo.FUNCTION:
			value = b.create().fromJson(new Gson().toJson(value), Function.class);
			break;
		case TypeInfo.PROCESS:
			value = b.create().fromJson(new Gson().toJson(value), Process.class);
			break;
		default:
			Main.LOGGER.error("NO CONVERSION: " + typeInfo + " / " + value + " / " + value.getClass(), new Exception());
		}
		return this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	synchronized public Object minIndex() {
		if (references == null) {
			return 0;
		}
		if (references.containsKey(Data.LOWER_BOUND_KEY)) {
			return references.get(Data.LOWER_BOUND_KEY).getValue();
		} else {
			return Collections.min((Set) references.keySet());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	synchronized public Object maxIndex() {
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
	synchronized public <T> T getValue() {
		if (explodedString) {
			implodeString();
		}
		return (T) value;
	}

	@SuppressWarnings("unchecked")
	synchronized public void setValue(Object value) {
		if (explodedString) {
			references.clear();
			explodedString = false;
		}
		Object oldValue = this.value;
		if (value != null && ((value instanceof Collection && value.getClass().getName().startsWith("java."))
				|| value.getClass().isArray())) {
			this.value = null;
			this.typeInfo = 0;
			this.kind = Kind.INT_SET;
			int i = 0;
			if (value.getClass().isArray()) {
				for (Object o : ((Object[]) value)) {
					set(i++, new RawData(o));
				}
			} else {
				for (Object o : ((Collection<Object>) value)) {
					set(i++, new RawData(o));
				}
			}
		} else {
			this.value = value;
			this.typeInfo = TypeInfo.getTypeInfoForInstance(value);
			if (value instanceof Executable) {
				this.kind = Kind.EXECUTABLE;
			}
		}
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
	synchronized public Boolean getBoolean() {
		return value == null ? false : (Boolean) value;
	}

	synchronized public Number getNumber() {
		return value == null ? 0 : (Number) value;
	}

	synchronized public void copyData(Data data) {
		// System.out.println(this + ":" + data);
		Data oldData = new RawData(this.value);
		this.value = data.getValue();
		this.typeInfo = data.getTypeInfo();
		this.explodedString = false; // because getValue implodes the string
		this.kind = data.getKind();
		this.constructor = data.getConstructor();
		if (data.getReferences() != null) {
			for (Entry<Object, Data> e : data.getReferences().entrySet()) {
				Data rd = new RawData(null);
				rd.copyData(e.getValue());
				set(e.getKey(), rd);
			}
		}

		notifyDataObserversForCopy(oldData);
	}

	synchronized public void set(Object key, Data value) {
		if (key == null) {
			throw new RuntimeException("key cannot be null");
		}
		if (references == null) {
			references = new Hashtable<Object, Data>();
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

	synchronized public Data get(Object key) {
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
	synchronized public String toString() {
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

	synchronized public String toJson() {
		StringBuffer ret = new StringBuffer();
		boolean array = isIndexedSet() || isArray();
		if (references != null) {
			ret.append(array ? "[" : "{");
			for (Entry<Object, Data> e : getOrderedEntries()) {
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

	@Override
	synchronized public boolean isAvailable() {
		return true;
	}

	synchronized public String toPrettyString() {
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

	private List<Entry<Object, Data>> getOrderedEntries() {
		List<Entry<Object, Data>> l = new ArrayList<>(references.entrySet());
		Collections.sort(l, new Comparator<Entry<Object, Data>>() {
			@SuppressWarnings("unchecked")
			@Override
			public int compare(Entry<Object, Data> o1, Entry<Object, Data> o2) {
				return ((Comparable<Object>)o1.getKey()).compareTo((Comparable<Object>)o2.getKey());
			}
		});
		return l;
	}
	
	synchronized public void prettyPrint(PrintStream out) {
		if (explodedString) {
			implodeString();
		}
		if (getValue() != null) {
			if (getValue() instanceof Throwable) {
				if (getValue() instanceof EvalException) {
					out.println(((EvalException) getValue()).getMessage());
					out.println("Invocation stack:");
					((EvalException) getValue()).printInvocationStackTrace(out);
					out.println("Evaluation stack:");
					((EvalException) getValue()).printEvaluationStackTrace(out);
					out.println("JVM stack:");
					((EvalException) getValue()).printStackTrace(out);
				} else {
					((Throwable) getValue()).printStackTrace(out);
				}
			} else {
				out.print((Object) getValue());
			}
		} else {
			if (/*kind == Data.Kind.INT_SET || */isIndexedSet()) {
				if (references != null && references.containsKey(Data.LOWER_BOUND_KEY)) {
					int min = ((Number) minIndex()).intValue();
					int max = ((Number) maxIndex()).intValue();
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
					out.print("]");
					//out.print("](" + min + ".." + max + ")");
				}
			} else if (getReferences() == null) {
				out.print("null");
			} else {
				if (constructor != null) {
					out.print(constructor.name);
				}
				out.print("[");
				Iterator<Entry<Object, Data>> it = getOrderedEntries().iterator();
				while (it.hasNext()) {
					Entry<Object, Data> e = it.next();
					if (e.getKey() != null) {
						out.print(e.getKey() + "=");
						if (e.getValue() == null) {
							out.print("null");
						} else {
							e.getValue().prettyPrint(out);
						}
						if (it.hasNext()) {
							out.print(",");
						}
					}
				}
				out.print("]");
			}
		}

	}

	synchronized public Map<Object, Data> getReferences() {
		return references;
	}

	@Override
	synchronized public boolean isArray() {
		return references != null
				&& (!references.containsKey(Data.LOWER_BOUND_KEY) && references.containsKey(Data.UPPER_BOUND_KEY));
	}

	synchronized public int getSize() {
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

	synchronized public Kind getKind() {
		return kind;
	}

	synchronized public void setKind(Kind type) {
		this.kind = type;
	}

	@Override
	synchronized public boolean isPrimitive() {
		return value != null && ((value instanceof Boolean) || (Number.class.isAssignableFrom(value.getClass()))
				|| (value instanceof Character));
	}

	@Override
	synchronized public void setReferences(Map<Object, Data> references) {
		Map<Object, Data> oldReferences = this.references;
		this.references = references;
		notifyDataObserversForReferences(oldReferences);
	}

	@Override
	synchronized public boolean equals(Object object) {
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

	synchronized private void onMapAccess() {
		if (references == null) {
			references = new Hashtable<Object, Data>();
		}
		if (!explodedString && (this.value instanceof String)) {
			explodeString();
		}
	}

	@Override
	synchronized public Object keyOf(Data data) {
		onMapAccess();
		for (Entry<Object, Data> e : getReferences().entrySet()) {
			if (e.getValue().equals(data)) {
				return e.getKey();
			}
		}
		return null;
	}

	@Override
	synchronized public Data subArray(int min, int max) {
		onMapAccess();
		Data d = new RawData(null);
		int key = 0;
		for (int i = min; i <= max; i++) {
			d.set(key++, get(i));
		}
		return d;
	}

	@Override
	synchronized public Data concat(Data data) {
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
			if (data.getReferences() != null) {
				for (Entry<Object, Data> e : data.getReferences().entrySet()) {
					res.set(e.getKey(), e.getValue());
				}
			}
		}
		return res;
	}

	@Override
	synchronized public Data first() {
		return get(0);
	}

	@Override
	synchronized public Data rest() {
		return subArray(1, getSize() - 1);
	}

	synchronized public int getTypeInfo() {
		return typeInfo;
	}

}
