package ini.eval;

import java.io.PrintStream;
import java.util.Stack;

import ini.ast.AstNode;

public class EvalException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Stack<Context> invocationStack;
	private Stack<AstNode> evaluationStack;
	private IniEval eval;

	/*
	 * public EvalException(Stack<Context> invocationStack, Stack<AstNode>
	 * evaluationStack, Exception cause) { super(cause.getMessage(), cause);
	 * this.invocationStack = invocationStack; this.evaluationStack =
	 * evaluationStack; }
	 * 
	 * public EvalException(String message, Stack<Context> invocationStack,
	 * Stack<AstNode> evaluationStack) { super(message); this.invocationStack =
	 * invocationStack; this.evaluationStack = evaluationStack; }
	 */

	@SuppressWarnings("unchecked")
	public EvalException(IniEval eval, String message, Throwable cause) {
		super(message, cause);
		this.eval = eval;
		this.invocationStack = (Stack<Context>) eval.invocationStack.clone();
		this.evaluationStack = (Stack<AstNode>) eval.evaluationStack.clone();
	}

	@SuppressWarnings("unchecked")
	public EvalException(IniEval eval, String message) {
		super(message);
		this.eval = eval;
		this.invocationStack = (Stack<Context>) eval.invocationStack.clone();
		this.evaluationStack = (Stack<AstNode>) eval.evaluationStack.clone();
	}

	public EvalException(IniEval eval, Exception cause) {
		this(eval, cause.getMessage(), cause);
	}

	public Stack<Context> getInvocationStack() {
		return invocationStack;
	}

	public Stack<AstNode> getEvaluationStack() {
		return evaluationStack;
	}

	public void printInvocationStackTrace(PrintStream out) {
		IniEval.printInvocationStackTrace(invocationStack, out);
	}

	public void printEvaluationStackTrace(PrintStream out) {
		IniEval.printEvaluationStackTrace(evaluationStack, out);
	}

	public void printError(PrintStream out, boolean thread) {
		out.println("Error: " + this.getMessage());
		out.println("Evaluation stack:");
		printEvaluationStackTrace(out);
		out.println("Context:");
		invocationStack.peek().prettyPrint(out);
		if (!thread) {
			if (invocationStack.peek() != eval.getRootContext()) {
				out.println("Root context:");
				eval.getRootContext().prettyPrint(out);
			}
		}
		/*
		 * int i = 1; for (IniEval eval : eval.forkedEvals) { out.println(
		 * "==== THREAD #" + i + " ===="); eval.printError(out, e, true); i++; }
		 */

	}

}
