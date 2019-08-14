package ini.eval.function;

import ini.ast.Binding;
import ini.ast.Expression;
import ini.ast.Invocation;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.eval.data.RawData;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class BoundJavaFunction extends IniFunction {

	public Binding binding;

	public BoundJavaFunction(Binding binding) {
		this.binding = binding;
	}

	@Override
	public Data eval(IniEval eval, List<Expression> params) {
		Object result = null;
		try {
			Object[] args = new Object[params.size()];
			int i = 0;
			for (Expression e : params) {
				//System.out.println("-- constructing param "+e+" - "+binding.getFunctionalType());
				Data d = eval.eval(e);
				Object o=null;
				List<Type> ts = binding.getFunctionalType().getTypeParameters();
				if(ts==null) {
					o = RawData.dataToObject(null, d);
				} else {
					o = RawData.dataToObject(ts.get(i) , d);
				}
				//System.out.println("-- "+o);
				// TODO: handle data structure (at least collections)
				args[i] = o;
				i++;
			}
			Class<?> c = Class.forName(binding.className);
			boolean invoked = false;
			
			
			switch(binding.getKind()) {
			case CONSTRUCTOR:
				for (Constructor<?> constr : c.getConstructors()) {
					try {
						result = constr.newInstance(args);
						invoked = true;
						break;
					} catch (Exception e) {
						if(e instanceof InvocationTargetException) {
							throw ((InvocationTargetException)e).getTargetException();
						}
						// swallow
					}
				}
				if (!invoked) {
					throw new RuntimeException("Cannot instantiate object (" + binding
							+ ") with " + getArgsString(args));
				}
				break;
			case METHOD:
				for (Method m : c.getMethods()) {
					if (m.getName().equals(binding.getMemberName())) {
						try {
							if (Modifier.isStatic(m.getModifiers())) {
								result = m.invoke(null, args);
								invoked = true;
								break;
							} else {
								result = m.invoke(args[0], Arrays.copyOfRange(
										args, 1, args.length));
								invoked = true;
								break;
							}
						} catch (Exception e) {
							if(e instanceof InvocationTargetException) {
								throw ((InvocationTargetException)e).getTargetException();
							}
							// swallow
						}
					}
				}
				if (!invoked) {
					throw new RuntimeException("Cannot invoke method (" + binding
							+ ") with " + getArgsString(args));
				}
				break;
			case FIELD:
				for (Field f : c.getFields()) {
					if (f.getName().equals(binding.getMemberName())) {
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
				}
				if (!invoked) {
					throw new RuntimeException("Cannot access field (" + binding
							+ ") with " + getArgsString(args));
				}
				break;
			}
		} catch (Throwable e) {
			throw new RuntimeException("Cannot invoke " + binding, e);
		}
		return RawData.objectToData(result);
	}

	private String getArgsString(Object[] args) {
		String s="";
		for(int i=0;i<args.length;i++) {
			s+=args[i];
			if(args[i]!=null) {
				s+=" ("+args[i].getClass()+")";
			}
			if(i<args.length-1) {
				s+=", ";
			}
		}
		return s;
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints, Invocation invocation) {
		return binding.getFunctionalType();
	}
	
}
