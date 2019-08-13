package ini;

import ini.parser.IniParser;

public class Ini {

	public Configuration configuration;

	public String environment = "development";

	public String node = "main";
	
	public EnvironmentConfiguration getEnvironmentConfiguration() {
		return configuration.environments.get(environment);
	}


	
}
