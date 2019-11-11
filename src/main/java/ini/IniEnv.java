package ini;

import ini.broker.CoreBrokerClient;

/**
 * The environment object for the INI instance.
 * 
 * @author Renaud Pawlak
 */
public class IniEnv {

	/**
	 * Tells if INI runs in deamon mode (as part of an INI cluster).
	 */
	public boolean deamon = false;

	/**
	 * The broker client, an object used to access the broker.
	 */
	public CoreBrokerClient coreBrokerClient;

	/**
	 * The configuration read from the INI configuration file if any.
	 */
	public ConfigurationFile configuration;

	/**
	 * The environment of the current INI instance ("development", "test", or "production").
	 */
	public String environment = "development";

	/**
	 * The INI instance's node name. 
	 */
	public String node = "main";

	/**
	 * Gets the configuration that corresponds to the currently active environment.
	 * @return
	 */
	public EnvironmentConfiguration getEnvironmentConfiguration() {
		return configuration.environments.get(environment);
	}
	
}
