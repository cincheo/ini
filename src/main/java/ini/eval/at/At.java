package ini.eval.at;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import ini.ast.Assignment;
import ini.ast.AstNode;
import ini.ast.AtPredicate;
import ini.ast.Expression;
import ini.ast.Rule;
import ini.ast.Variable;
import ini.eval.IniEval;
import ini.eval.IniThread;
import ini.eval.data.Data;
import ini.type.AstAttrib;
import ini.type.Type;
import ini.type.TypingConstraint.Kind;
import ini.type.TypingError;

public abstract class At {
	protected boolean terminated = false;
	public static Map<String, Class<? extends At>> atPredicates = new HashMap<String, Class<? extends At>>();
	public List<At> synchronizedAts = new ArrayList<At>();
	private ThreadPoolExecutor threadExecutor;
	private int currentThreadCount = 0;
	private Map<String, Data> inContext = new HashMap<String, Data>();
	private Rule rule;
	private AtPredicate atPredicate;

	public static boolean checkAllTerminated(List<At> ats) {
		if (ats == null)
			return true;
		for (At at : ats) {
			if (!at.checkTerminated()) {
				return false;
			}
		}
		return true;
	}

	public static void destroyAll(List<At> ats) {
		if (ats == null)
			return;
		for (At at : ats) {
			at.destroy();
		}
	}

	static int currentId = 1;
	int id;
	static {
		atPredicates.put("update", AtUpdate.class);
		atPredicates.put("every", AtEvery.class);
		atPredicates.put("cron", AtCron.class);
		atPredicates.put("read_keyboard", AtReadKeyboard.class);
		atPredicates.put("consume", AtConsume.class);
	}

	public abstract void eval(IniEval eval);

	public At() {
		id = currentId++;
	}

	public int getId() {
		return id;
	}

	public boolean checkTerminated() {
		return terminated;
	}

	public void terminate() {
		terminated = true;
		// we stop the executor in another thread in case we are in the tread of
		// a running task that would prevent proper shutdown
		new Thread() {
			public void run() {
				if (threadExecutor != null) {
					try {
						threadExecutor.shutdown();
						while (!threadExecutor.awaitTermination(100, TimeUnit.MILLISECONDS)) {
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void restart(IniEval eval) {
		terminated = false;
		threadExecutor = null;
		eval(eval);
	}

	public void execute(IniThread thread) {
		// System.out.println(">>>> Excute: " + eval);
		getThreadExecutor().execute(thread);
		// System.out.println(">>>> Excute 2: " + this);
	}

	public ThreadPoolExecutor getThreadExecutor() {
		if (threadExecutor == null) {
			threadExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
			threadExecutor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
				@Override
				public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
					// System.out.println("REJECTED");
				}
			});
		}
		return threadExecutor;
	}

	public void destroy() {
		// we stop the executor in another thread in case we are in the tread of
		// a running task that would prevent proper shutdown
		new Thread() {
			public void run() {
				if (threadExecutor != null) {
					if (threadExecutor != null && !threadExecutor.isShutdown()) {
						threadExecutor.shutdownNow();
						// try {
						// threadExecutor.shutdown();
						// while (!threadExecutor.awaitTermination(100,
						// TimeUnit.MILLISECONDS)) {
						//
						// }
						// } catch (InterruptedException e) {
						// e.printStackTrace();
						// }
					}
				}
			}
		}.start();
	}

	private void pushThread() {
		// System.out.println("enter " + this);
		currentThreadCount++;
		// System.out.println("push: " + this + "," + currentThreadCount);
	}

	public synchronized void popThread() {
		// System.out.println("exit " + this);
		currentThreadCount--;
		// System.out.println("pop: " + this + "," + currentThreadCount);
		notifyAll();
	}

	private synchronized void isEmpty() {
		while (currentThreadCount > 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void isEmptySynchronizedAts() {
		// System.out.println("all ats: " + synchronizedAts);
		for (At at : synchronizedAts) {
			at.isEmpty();
		}
	}

	Object monitor = new Object();

	public void safelyEnter() {
		// System.out.println("safely enter 1 " + this + " >>>");
		synchronized (monitor) {
			// System.out.println("safely enter 2 " + this + " >>>");
			isEmptySynchronizedAts();
			pushThread();
			monitor.notifyAll();
		}
		// System.out.println("safely enter 3 " + this + " >>>");
	}

	public void parseInParameters(final IniEval eval, List<Expression> inParameters) {
		if (inParameters == null) {
			return;
		}
		for (Expression e : inParameters) {
			Assignment a = (Assignment) e;
			inContext.put(((Variable) a.assignee).name, eval.eval(a.assignment));
		}
	}

	public Map<String, Data> getInContext() {
		return inContext;
	}

	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public AtPredicate getAtPredicate() {
		return atPredicate;
	}

	public void setAtPredicate(AtPredicate atPredicate) {
		this.atPredicate = atPredicate;
	}

	protected final void typeInParameters(AstAttrib attrib, boolean mandatory, Type type, String... parameters) {
		AstNode target = getAtPredicate().getAnnotationNode(parameters);
		if (mandatory && target == null) {
			attrib.addError(new TypingError(attrib.getEnclosingNode(),
					"cannot find mandatory annotation: " + StringUtils.join(parameters, " or ")));
		}
		if (target != null) {
			attrib.addTypingConstraint(Kind.EQ, attrib.eval(target), type, target, target);
		}
	}

	public abstract void evalType(AstAttrib attrib);

}
