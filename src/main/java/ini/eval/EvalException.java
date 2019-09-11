package ini.eval;

import java.io.PrintStream;
import java.util.Stack;

import ini.ast.AstNode;

public class EvalException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Stack<Context> invocationStack;
	private Stack<AstNode> evaluationStack;

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
		this.invocationStack = (Stack<Context>) eval.invocationStack.clone();
		this.evaluationStack = (Stack<AstNode>) eval.evaluationStack.clone();
	}

	@SuppressWarnings("unchecked")
	public EvalException(IniEval eval, String message) {
		super(message);
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

}
