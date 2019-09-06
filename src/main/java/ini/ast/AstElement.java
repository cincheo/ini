package ini.ast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import ini.parser.IniParser;
import ini.type.Type;

public abstract class AstElement implements AstNode {

	transient public IniParser parser;
	transient public Token token;
	public int nodeTypeId = -1;
	transient public Type type;
	public String owner;
	public List<Expression> annotations;

	public String getAnnotationValue(String key) {
		if (annotations != null && !annotations.isEmpty()) {
			for (Expression e : annotations) {
				if (e instanceof Assignment) {
					Assignment a = (Assignment) e;
					String name = a.assignee.toString();
					if (key.equals(name)) {
						if (a.assignment instanceof StringLiteral) {
							return ((StringLiteral) a.assignment).value;
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public int nodeTypeId() {
		return nodeTypeId;
	}

	public AstElement(IniParser parser, Token token) {
		super();
		// if(token==null) {
		// throw new RuntimeException("token cannot be null");
		// }
		this.parser = parser;
		this.token = token;
		if (parser != null) {
			this.owner = parser.env.node;
		}
	}

	static public void prettyPrintList(PrintStream out, List<? extends AstNode> nodes, String separator) {
		if (nodes == null) {
			return;
		}
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).prettyPrint(out);
			if (i < nodes.size() - 1) {
				out.print(separator);
			}
		}
	}

	@Override
	public Token token() {
		return token;
	}

	@Override
	public String toString() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		prettyPrint(new PrintStream(os));
		String s = os.toString();
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public void setType(Type type) {
		this.type = type;
	}

}
