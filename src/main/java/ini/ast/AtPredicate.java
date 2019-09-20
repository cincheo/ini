package ini.ast;

import ini.parser.IniParser;

import java.io.PrintStream;
import java.util.List;

public class AtPredicate extends NamedElement {

	public enum Kind {
		UPDATE, EVERY, CRON, INIT, END, ERROR, UPDATE_SYNC, CONSUME, USER_DEFINED
	}
	
	public List<Expression> outParameters;
	public Kind kind=null;
	public String identifier;
	
	public AtPredicate(IniParser parser, Token token, String name, List<Expression> configurationArguments, List<Expression> runtimeArguments, String identifier) {
		super(parser, token, name);
		this.identifier = identifier;
		this.annotations = configurationArguments;
		this.outParameters = runtimeArguments;
		if(name.equals("init")) {
			kind=Kind.INIT;
		}
		if(name.equals("consume")) {
			kind=Kind.CONSUME;
		}
		if(name.equals("end")) {
			kind=Kind.END;
		}
		if(name.equals("update")) {
			kind=Kind.UPDATE;
		}
		if(name.equals("update_sync")) {
			kind=Kind.UPDATE_SYNC;
		}
		if(name.equals("every")) {
			kind=Kind.EVERY;
		}
		if(name.equals("cron")) {
			kind=Kind.CRON;
		}
		if(name.equals("error")) {
			kind=Kind.ERROR;
		}
		if(kind == null) {
			kind=Kind.USER_DEFINED;
		}
		this.nodeTypeId=AT_PREDICATE;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print("@"+name);
		out.print("[");
		prettyPrintList(out, annotations, ",");
		out.print("]");
		out.print("(");
		prettyPrintList(out, outParameters, ",");
		out.print(")");
	}

}
