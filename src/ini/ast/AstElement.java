package ini.ast;

import ini.parser.IniParser;
import ini.type.Type;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public abstract class AstElement implements AstNode {

	public IniParser parser;
	public Token token;
	public int nodeTypeId = -1;
	public Type type;
	
	@Override
	public int nodeTypeId() {
		return nodeTypeId;
	}
	
	public AstElement(IniParser parser, Token token) {
		super();
		//if(token==null) {
		//	throw new RuntimeException("token cannot be null");
		//}
		this.parser = parser;
		this.token = token;
	}

	static public void prettyPrintList(PrintStream out, List<? extends AstNode> nodes, String separator) {
		if(nodes==null) {
			return;
		}
		for(int i=0;i<nodes.size();i++) {
			nodes.get(i).prettyPrint(out);
			if(i<nodes.size()-1) {
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

