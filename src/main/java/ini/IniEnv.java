package ini;

public class IniEnv {

	public boolean deamon = false;

	public ini.broker.CoreBrokerClient coreBrokerClient;

	public ConfigurationFile configuration;

	public String environment = "development";

	public String node = "main";
	
	public EnvironmentConfiguration getEnvironmentConfiguration() {
		return configuration.environments.get(environment);
	}
	
}
