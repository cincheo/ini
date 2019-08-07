package ini.ast;

import ini.parser.IniParser;
import ini.type.Type;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Function extends NamedElement {

	public List<Rule> initRules=new ArrayList<Rule>();
	public List<Rule> rules;
	public List<Rule> atRules=new ArrayList<Rule>();
	public List<Rule> endRules=new ArrayList<Rule>();
	public List<Rule> errorRules=new ArrayList<Rule>();
	public List<Parameter> parameters;
	public boolean process;

	public boolean isProcess() {
		return process;
	}

	public void setProcess(boolean process) {
		this.process = process;
	}

	public Function(IniParser parser, Token token, String name,
			List<Parameter> parameters, List<Rule> rules) {
		super(parser, token, name);
		// TODO: fix ugly hack
		this.process = token.text.equals("process");
		this.parameters = parameters;
		this.rules = rules;
		for(Rule r:new ArrayList<Rule>(rules)) {
			if(r.atPredicate!=null) {
				switch(r.atPredicate.kind) {
				case INIT:
					initRules.add(r);
					rules.remove(r);
					break;
				case END:
					endRules.add(r);
					rules.remove(r);
					break;
				case ERROR:
					errorRules.add(r);
					rules.remove(r);
					break;
				default:
					atRules.add(r);
					rules.remove(r);
				}
			}
		}
		this.nodeTypeId = AstNode.FUNCTION;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("function " + name + "(");
		prettyPrintList(out, parameters, ",");
		out.println(") {");
		for (Rule r : initRules) {
			r.prettyPrint(out);
			out.println();
		}
		for (Rule r : rules) {
			r.prettyPrint(out);
			out.println();
		}
		for (Rule r : atRules) {
			r.prettyPrint(out);
			out.println();
		}
		for (Rule r : endRules) {
			r.prettyPrint(out);
			out.println();
		}
		for (Rule r : errorRules) {
			r.prettyPrint(out);
			out.println();
		}
		out.println("}");
	}
	
	@Override
	public String toString() {
		return "function " + name;
	}
	
	transient public Type functionType=null;
	
	public Type getFunctionType() {
		functionType = new Type(parser,"function");
		if (name.equals("main")) {
			if(parameters!=null && parameters.size()==1) {
				functionType.addTypeParameter(parser.ast.getDependentType("Map", parser.ast.INT, parser.ast.STRING));
			}
			functionType.setReturnType(parser.ast.VOID);
		} else {
			functionType.setReturnType(new Type(parser));
		}
		for(int i=0;i<parameters.size();i++) {
			functionType.addTypeParameter(new Type(parser));
		}
		return functionType;
	}
	
}
