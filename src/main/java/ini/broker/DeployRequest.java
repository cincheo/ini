package ini.broker;

import ini.ast.Function;

public class DeployRequest extends Request {

	public DeployRequest(Function function) {
		this.function = function;
	}

	public Function function;

	@Override
	public String toString() {
		return "deploy request for function " + function.name;
	}

}
