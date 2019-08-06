package ini.broker;

public class FetchRequest extends Request {

	public String fetchedName;

	public FetchRequest(String fetchedName) {
		this.fetchedName = fetchedName;
	}

	@Override
	public String toString() {
		return "fetch request for " + fetchedName;
	}

}
