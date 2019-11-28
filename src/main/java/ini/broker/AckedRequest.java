package ini.broker;

import java.util.UUID;

public class AckedRequest extends Request {

	public AckedRequest(String sourceNode) {
		super(sourceNode);
		this.id = UUID.randomUUID().toString();
	}

	public String id;

}
