package ini;

import java.util.Map;

/**
 * An object containing the configuration read from the configuration file.
 * 
 * @author Renaud Pawlak
 */
public class ConfigurationFile {

	/**
	 * The node name.
	 */
	public String node;

	/**
	 * The configurations for all the environments (development, test, production).
	 */
	public Map<String, EnvironmentConfiguration> environments;
	
}
