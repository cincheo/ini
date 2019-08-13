package ini.broker;

import ini.ast.Function;

public class DeployRequest extends Request {

	public DeployRequest(String sourceNode, Function function) {
		super(sourceNode);
		this.function = function;
	}

	public Function function;

	@Override
	public String toString() {
		return "deploy request for function " + function.name;
	}

}
