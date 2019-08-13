package ini.broker;

import java.util.List;

import ini.eval.data.Data;

public class SpawnRequest extends Request {

	public String spawnedProcessName;
	public List<Data> parameters;

	public SpawnRequest(String sourceNode, String spawnedProcessName, List<Data> parameters) {
		super(sourceNode);
		this.spawnedProcessName = spawnedProcessName;
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		return "spawn request for process " + spawnedProcessName + " with parameters: " + parameters;
	}

}
