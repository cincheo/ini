package ini.eval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ini.ast.AstNode;
import ini.ast.AtPredicate;
import ini.ast.Function;
import ini.ast.Rule;
import ini.eval.data.Data;
import ini.parser.IniParser;

public class IniDebug extends IniEval {

	boolean step = false;
	List<String> breakpoints;
	List<String> watchedVariables;

	public IniDebug(IniParser parser, Context context, List<String> breakpoints, List<String> watchedVariables) {
		super(parser, context);
		System.out.println("[INI] Entering debug mode...");
		step = true;
		this.breakpoints = new ArrayList<>();
		this.watchedVariables = new ArrayList<>();
		List<String> l = breakpoints;
		if (l != null) {
			breakpoints.addAll(l);
			System.out.println("[INI] Breakpoints: "
					+ (breakpoints.isEmpty() ? "no breakpoints defined"
							: breakpoints));
		}
		l = watchedVariables;
		if (l != null) {
			watchedVariables.addAll(l);
			System.out.println("[INI] Watched variables: "
					+ (watchedVariables.isEmpty() ? "no variables watched"
							: watchedVariables));
		}
	}

	//@SuppressWarnings("unchecked")
	public Data eval(AstNode node) {

		// System.out.println("node = " + node + ":" + node.getClass());

		boolean stepped = false;
		boolean nodeToBeChecked = node.token() != null
				&& !evaluationStack.isEmpty()
				&& !(node instanceof Rule)
				&& ((evaluationStack.peek() instanceof Rule) || (evaluationStack
						.peek() instanceof Function));

		if (node instanceof Rule) {
			Rule r = (Rule) node;
			if (r.atPredicate != null) {
				if (r.atPredicate.kind == AtPredicate.Kind.INIT
						|| r.atPredicate.kind == AtPredicate.Kind.END) {
					nodeToBeChecked = true;
				}
			}
		}

		// System.out.println("tobeckecked=" + nodeToBeChecked);

		if (nodeToBeChecked) {
			if (!step
					&& breakpoints.contains(node.token().getLocation().split(
							"\\(")[0])) {
				step = true;
			}

			while (step) {
				stepped = true;
				System.out.print("[INI] Executing: ");
				printNode(System.out, node);
				System.out.println();
				for (String v : watchedVariables) {
					System.out.print("    ");
					invocationStack.peek().prettyPrintVariable(System.out, v);
					System.out.println();
				}
				System.out
						.print("[INI] (S)tep | (r)un | (c)ontext | (b)reakpoint | (w)atch | (q)uit: ");
				String s = readKeyboard();
				if (s.equalsIgnoreCase("r")) {
					step = false;
					break;
				} else if (s.equalsIgnoreCase("c")) {
					System.out.println("Context:");
					invocationStack.peek().prettyPrint(System.out);
					System.out.println("Evaluation stack:");
					printEvaluationStackTrace(evaluationStack, System.out);
				} else if (s.equalsIgnoreCase("w")) {
					System.out.print("[INI] Enter variable name: ");
					String variableName = readKeyboard();
					if (watchedVariables.contains(variableName)) {
						watchedVariables.remove(variableName);
						System.out.println("[INI] Variable '" + variableName
								+ "' is not watched anymore");
					} else {
						watchedVariables.add(variableName);
						System.out.println("[INI] Variable '" + variableName
								+ "' is now watched");
					}
				} else if (s.equalsIgnoreCase("b")) {
					String current = node.token().getLocation().split("\\(")[0];
					if (breakpoints.contains(current)) {
						breakpoints.remove(current);
						System.out.println("[INI] Breakpoint removed: "
								+ current);
					} else {
						breakpoints.add(current);
						System.out
								.println("[INI] Breakpoint added: " + current);
					}
					System.out.println("[INI] Breakpoints: "
							+ (breakpoints.isEmpty() ? "no breakpoints defined"
									: breakpoints));
				} else if (s.equalsIgnoreCase("q")) {
					System.out.println("Bye.");
					System.exit(0);
				} else {
					break;
				}
			}
		}

		result = super.eval(node);

		if (stepped && result != null) {
			System.out.print("[INI] ");
			printNode(System.out, node);
			System.out.print(" returned '");
			result.prettyPrint(System.out);
			System.out.println("'");
		}
		return result;
	}

	String readKeyboard() {
		try {
			return new BufferedReader(new InputStreamReader(System.in))
					.readLine();
		} catch (IOException e) {
			throw new RuntimeException("Error reading keyboard for debugger.");
		}
	}
}
