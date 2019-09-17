package ini.broker;

public class Channel {

	private static final String PUBLIC_PREFIX = "+";

	private boolean global = false;
	private String name;

	public Channel(String name) {
		if (name.startsWith(PUBLIC_PREFIX)) {
			global = true;
			this.name = name.substring(PUBLIC_PREFIX.length());
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
