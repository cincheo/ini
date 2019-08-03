package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;

public class Import extends AstElement implements Comparable<Import> {

	public String filePath;
	
	public Import(IniParser parser, Token token, String filePath) {
		super(parser, token);
		this.filePath=filePath;
		this.nodeTypeId=IMPORT;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("import \""+filePath+"\"");
	}

	@Override
	public int compareTo(Import o) {
		return filePath.compareTo(o.filePath);
	}
}
