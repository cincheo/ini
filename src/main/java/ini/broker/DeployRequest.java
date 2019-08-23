package ini.broker;

import ini.ast.Executable;

public class DeployRequest extends Request {

	public DeployRequest(String sourceNode, Executable executable) {
		super(sourceNode);
		this.executable = executable;
	}

	public Executable executable;

	@Override
	public String toString() {
		return "deploy request for function " + executable.name;
	}

}
