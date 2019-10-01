package ini.eval.function;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ini.Main;
import ini.ast.Binding;
import ini.ast.Executable;
import ini.ast.Parameter;
import ini.ast.TypeVariable;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.eval.data.RawData;
import ini.type.AstAttrib;
import ini.type.Type;

public class BoundJavaFunction extends Executable {

	public Binding binding;

	public BoundJavaFunction(Binding binding) {
		super(binding.parser, binding.token, binding.name, new ArrayList<>());
		this.binding = binding;
		int i = 0;
		if (binding.parameterTypes != null) {
			for (TypeVariable t : binding.parameterTypes) {
				parameters.add(new Parameter(binding.parser, t.token, "arg" + (i++)));
			}
		}
	}

	@Override
	public void eval(IniEval eval) {
		// TODO: FOR PERFORMANCE, MOVE JAVA MEMBER LOOKUP IN THE CONSTRUCTOR

		Object result = null;
		try {
			Object[] args = new Object[parameters.size()];
			List<TypeVariable> ts = binding.parameterTypes;
			for (int i = 0; i < parameters.size(); i++) {
				// System.out.println("-- constructing param "+e+" -
				// "+binding.getFunctionalType());
				Data d = getArgument(eval, i);
				Object o = null;
				if (ts == null) {
					o = RawData.dataToObject(eval, null, d);
				} else {
					o = RawData.dataToObject(eval, ts.get(i).getType(), d);
				}
				// System.out.println("-- "+o);
				// TODO: handle data structure (at least collections)
				args[i] = o;
			}
			Class<?> c = "this".equals(binding.className)?args[0].getClass():Class.forName(binding.className);
			boolean invoked = false;
			Exception cause = null;

			switch (binding.getKind()) {
			case CONSTRUCTOR:
				// TODO: MOVE IN BINDING'S CONSTRUCTOR
				for (Constructor<?> constr : c.getConstructors()) {
					try {
						result = constr.newInstance(args);
						invoked = true;
						break;
					} catch (Exception e) {
						if (e instanceof InvocationTargetException) {
							throw ((InvocationTargetException) e).getTargetException();
						}
						// swallow
					}
				}
				if (!invoked) {
					throw new RuntimeException(
							"Cannot instantiate object for binding " + binding + ", args = " + getArgsString(args));
				}
				break;
			case METHOD:
				// TODO: MOVE IN BINDING'S CONSTRUCTOR
				for (Method m : c.getMethods()) {
					if (m.getName().equals(binding.getMemberName())) {
						try {
							if (Modifier.isStatic(m.getModifiers())) {
								result = m.invoke(null, args);
								invoked = true;
								break;
							} else {
								if (args[0] == null) {
									cause = new Exception(
											"cannot invoke '" + binding.getMemberName() + "()' on null object");
								}
								Main.LOGGER.debug("invoking " + binding.getMemberName() + " on " + args[0]
										+ " from thread " + Thread.currentThread().getName());
								result = m.invoke(args[0], Arrays.copyOfRange(args, 1, args.length));
								invoked = true;
								break;
							}
						} catch (Exception e) {
							if (e instanceof InvocationTargetException) {
								throw ((InvocationTargetException) e).getTargetException();
							}
							// swallow
						}
					}
				}
				if (!invoked) {
					if (cause == null) {
						throw new RuntimeException(
								"Cannot invoke method for binding " + binding + ", args = " + getArgsString(args));
					} else {
						throw new RuntimeException(
								"Cannot invoke method for binding " + binding + ", args = " + getArgsString(args),
								cause);
					}
				}
				break;
			case FIELD:
				Field f = c.getField(binding.getMemberName());
				if (f != null) {
					try {
						if (Modifier.isStatic(f.getModifiers())) {
							result = f.get(null);
							invoked = true;
							break;
						} else {
							result = f.get(args[0]);
							invoked = true;
							break;
						}
					} catch (Exception e) {
						// swallow
					}
				}
				if (!invoked) {
					throw new RuntimeException("Cannot access field in " + binding + ", args = " + getArgsString(args));
				}
				break;
			}
		} catch (Throwable e) {
			throw new RuntimeException("Cannot invoke " + binding, e);
		}
		eval.result = RawData.objectToData(result);
	}

	private String getArgsString(Object[] args) {
		String s = "";
		for (int i = 0; i < args.length; i++) {
			s += args[i];
			if (args[i] != null) {
				s += " (" + args[i].getClass() + ")";
			}
			if (i < args.length - 1) {
				s += ", ";
			}
		}
		return s;
	}

	@Override
	public Type getFunctionalType(AstAttrib attrib) {
		return binding.getFunctionalType(attrib);
	}

}
