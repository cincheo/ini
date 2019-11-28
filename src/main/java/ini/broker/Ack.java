package ini.broker;

public class Ack {

	public String requestId;

	public Ack(AckedRequest request) {
		this.requestId = request.id;
	}


}
