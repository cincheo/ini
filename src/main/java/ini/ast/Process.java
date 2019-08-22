package ini.ast;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import ini.parser.IniParser;

public class Process extends Function {

	public List<Rule> initRules=new ArrayList<Rule>();
	public List<Rule> atRules=new ArrayList<Rule>();
	public List<Rule> endRules=new ArrayList<Rule>();
	public List<Rule> errorRules=new ArrayList<Rule>();

	public boolean isProcess() {
		return true;
	}

	public Process(IniParser parser, Token token, String name,
			List<Parameter> parameters, List<Rule> rules) {
		super(parser, token, name, parameters, rules);
		for(Rule r:new ArrayList<Rule>(rules)) {
			if(r.atPredicate!=null) {
				switch(r.atPredicate.kind) {
				case INIT:
					this.initRules.add(r);
					this.rules.remove(r);
					break;
				case END:
					this.endRules.add(r);
					this.rules.remove(r);
					break;
				case ERROR:
					this.errorRules.add(r);
					this.rules.remove(r);
					break;
				default:
					this.atRules.add(r);
					this.rules.remove(r);
				}
			}
		}
		this.nodeTypeId = AstNode.PROCESS;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("process " + name + "(");
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
		return "process " + name;
	}
	
}
