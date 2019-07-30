package ini.type;

import ini.ast.AstNode;

public class TypingError {

	public enum Level {
		ERROR, WARNING, MESSAGE
	}

	public AstNode origin;
	public String message;
	public Level level;

	public TypingError(Level level, AstNode origin, String message) {
		super();
		this.level = level;
		this.origin = origin;
		this.message = message;
	}

	public TypingError(AstNode origin, String message) {
		this(Level.ERROR, origin, message);
	}

	@Override
	public String toString() {
		return level.name()
				+ ": "
				+ message
				+ (origin != null ? " at '"
						+ origin.toString()
						+ "'"
						+ (origin.token() != null ? " "
								+ origin.token().getLocation() : "") : "");
	}

}
