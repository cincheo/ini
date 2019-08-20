package ini.parser;

import ini.ast.Token;

public class SyntaxError {

	public Token origin;
	public String message;

	public SyntaxError(Token origin, String message) {
		super();
		this.origin = origin;
		this.message = message;
	}

	@Override
	public String toString() {
		return "SYNTAX ERROR" + ": " + message + (origin != null
				? " at '" + origin.toString() + "'" + " " + origin.getLocation() + " (token code=" + origin.getType() + ")"
				: "");
	}

}
