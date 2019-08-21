package ini.broker;

public class Channel {

	private boolean global = false;
	private String name;
	
	public Channel(String name) {
		if(name.startsWith("global:")) {
			global = true;
			this.name = name.substring(7);
		} else {
			this.name = name;
		}
	}

	public boolean isGlobal() {
		return global;
	}

	public String getName() {
		return name;
	}
	
}
