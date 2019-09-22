package ini.eval;

import ini.ast.AstNode;
import ini.ast.Rule;
import ini.eval.IniEval.KilledException;
import ini.eval.at.At;
import ini.eval.data.Data;

import java.util.Map;

public class IniThread extends Thread {

	public final IniEval parent;
	public final AstNode toEval;
	public IniEval child;
	public String atName;
	static int threadCount = 1;
	private At at;

	public IniThread(IniEval parent, At at, AstNode toEval) {
		this.parent = parent;
		this.toEval = toEval;
		this.at = at;
		if ((toEval instanceof Rule) && ((Rule) toEval).atPredicate != null) {
			this.setName(((Rule) toEval).atPredicate.toString() + ":" + threadCount++);
		}
		child = parent.fork();
	}

	public IniThread setVariables(Map<String, Data> variables) {
		if (variables != null) {
			for (String variable : variables.keySet()) {
				child.invocationStack.peek().bind(variable, variables.get(variable));
			}
		}
		return this;
	}

	@Override
	public void run() {
		if (at != null) {
			at.safelyEnter();
		}

		try {
			child.eval(toEval);
		} catch (KilledException e) {
			// swallow
		} catch (EvalException e) {
			e.printError(child.parser.out, true);
			child.parser.out.println("Java stack:");
			e.printStackTrace(child.parser.out);
		} finally {
			if (at != null) {
				// System.out.println("------pop: " + at);
				at.popThread();
			}
		}
	}

	public void kill() {
		child.kill = true;
		this.setName(this.getName() + ":killed");
	}

}
