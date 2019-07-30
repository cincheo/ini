package ini.eval.at;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ini.ast.Expression;
import ini.ast.Variable;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.eval.data.RawData;

public class AtReadKeyboard extends At {

	int numInput;
	boolean isCurrentEvent = false;
	
	@Override
	public void eval(final IniEval eval) {
		getKeyboardThread().register(new AtContext(eval, this));
	}

	private static KeyboardThread keyboardThread;

	public static KeyboardThread getKeyboardThread() {
		if (keyboardThread == null) {
			keyboardThread = new KeyboardThread();
			keyboardThread.start();
		}
		return keyboardThread;
	}
}

class KeyboardThread extends Thread {
	String input;
	BufferedReader br;
	List<AtContext> observers = new ArrayList<AtContext>();

	public KeyboardThread() {
		br = new BufferedReader(new InputStreamReader(System.in));
	}

	@Override
	public void run() {
		while (true) {
			try {
				input = br.readLine();
				for (AtContext atContext : observers) {
					atContext.variables.put(atContext.variables.keySet()
							.iterator().next(), new RawData(input));
					atContext.eval();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void register(AtContext atContext) {
		observers.add(atContext);
	}
}

class AtContext {
	public IniEval eval;
	public At at;
	public Map<String, Data> variables;

	public AtContext(IniEval eval, At at) {
		this.eval = eval;
		this.at = at;
		variables = new HashMap<String, Data>();
		for (Expression e : at.getRule().atPredicate.outParameters) {
			variables.put(((Variable) e).name, null);
		}
	}

	public void eval() {
		at.execute(eval,variables);
	}

	@Override
	public String toString() {
		return eval + ":" + at.getRule() + ":" + variables;
	}
}
