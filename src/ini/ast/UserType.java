package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserType extends NamedElement {

	private static int index = 1;
	
	public List<Constructor> constructors = new ArrayList<Constructor>();
	public Map<String, Constructor> constructorMap = new HashMap<String, Constructor>();
	
	public UserType(IniParser parser, Token token, String name, List<Constructor> constructors) {
		super(parser, token, name);
		
		if (this.name == null) {
			this.name = "_T" + index++;
		}

		this.constructors = constructors;
		for(Constructor constructor:constructors) {
			// name anonymous constructor after the type name
			if(constructor.name.startsWith("_C")) {
				constructor.name=this.name;
			}
			if(constructorMap.containsKey(constructor.name)) {
				throw new RuntimeException("duplicate name in constructors for type '"+this.name+"'");
			}
			constructorMap.put(constructor.name, constructor);
			constructor.userType = this;
			parser.ast.register(constructor);
		}
		parser.ast.userTypes.add(this);
		parser.ast.userTypeMap.put(this.name, this);
		this.nodeTypeId = AstNode.USER_TYPE;
	}
	
	@Override
	public void prettyPrint(PrintStream out) {
		out.print("type "+name+" = ");
		constructors.get(0).prettyPrint(out);
		for(int i=1;i<constructors.size();i++) {
			out.println();
			out.print("    | ");
			constructors.get(i).prettyPrint(out);
		}
		out.println();
	}

}
