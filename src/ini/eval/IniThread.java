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
	public Map<String, Data> variables;
	public String atName;
	static int threadCount = 1;
	private At at;

	public IniThread(IniEval parent, At at, AstNode toEval) {
		this.parent = parent;
		this.toEval = toEval;
		this.at = at;
		if ((toEval instanceof Rule) && ((Rule) toEval).atPredicate != null) {
			this.setName(((Rule) toEval).atPredicate.toString() + ":"
					+ threadCount++);
		}
	}

	public IniThread(IniEval parent, At at, AstNode toEval,
			Map<String, Data> variables) {
		this(parent, at, toEval);
		this.variables = variables;
	}

	public IniThread(IniEval parent, At at, AstNode toEval,
			Map<String, Data> variables, String atName) {
		this(parent, at, toEval);
		this.variables = variables;
		this.atName = atName;
	}

	@Override
	public void run() {
		if (at != null) {
			at.safelyEnter();
		}

		try {
			child = parent.fork();
			if (variables != null) {
				for (String variable : variables.keySet()) {
					child.invocationStack.peek().bind(variable,
							variables.get(variable));
				}
			}
			child.eval(toEval);
		} catch (KilledException e) {
		} finally {
			if (at != null) {
				//System.out.println("------pop: " + at);
				at.popThread();
			}
		}
	}

	public void kill() {
		child.kill = true;
		this.setName(this.getName() + ":killed");
	}

}
