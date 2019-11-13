package ini.broker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ini.ast.Executable;
import ini.eval.data.Data;

public class SpawnRequest extends Request {

	public String spawnedExecutableName;
	public Executable executable;
	public List<Data> parameters;

	public SpawnRequest(String sourceNode, Executable executable, List<Data> parameters) {
		super(sourceNode);
		this.spawnedExecutableName = executable.name;
		if(this.spawnedExecutableName==null) {
			// lambda case
			this.executable = executable;
		}
		if (parameters != null) {
			this.parameters = new ArrayList<Data>(
					parameters.stream().map(d -> d.getIfAvailable()).collect(Collectors.toList()));
		}
	}

	@Override
	public String toString() {
		return "spawn request for process " + spawnedExecutableName + " with parameters: " + parameters;
	}

}
