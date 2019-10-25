package ini.broker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ini.eval.data.Data;

public class SpawnRequest extends Request {

	public String spawnedProcessName;
	public List<Data> parameters;

	public SpawnRequest(String sourceNode, String spawnedProcessName, List<Data> parameters) {
		super(sourceNode);
		this.spawnedProcessName = spawnedProcessName;
		if (parameters != null) {
			this.parameters = new ArrayList<Data>(
					parameters.stream().map(d -> d.getIfAvailable()).collect(Collectors.toList()));
		}
	}

	@Override
	public String toString() {
		return "spawn request for process " + spawnedProcessName + " with parameters: " + parameters;
	}

}
