package ini.broker;

/**
 * Defines a communication channel in a broker.
 * 
 * @author Renaud Pawlak
 *
 * @param <T>
 *            the type of data that can be exchanged in that channel
 */
public class Channel<T> {

	private String name;
	private ChannelConfiguration configuration;
	private Class<T> dataType;

	/**
	 * Creates a new broker channel with a name and no configuration (will use
	 * the default channel configuration of the broker).
	 * 
	 * @param name
	 *            the channel's name
	 * @param dataType
	 *            the data type of the channel (for typing)
	 */
//	public Channel(String name, Class<T> dataType) {
//		super();
//		this.name = name;
//		this.dataType = dataType;
//	}

	/**
	 * Creates a new broker channel with a name and a specific configuration
	 * that will try to override the default broker channel configuration. Note
	 * that the configuration may not be applicable once the channel has been
	 * used once, since most configuration operation are done at channel's
	 * creation time.
	 * 
	 * @param name
	 *            the channel's name
	 * @param dataType
	 *            the data type of the channel (for typing)
	 * @param configuration
	 *            the channel's configuration (can be null)
	 */
	public Channel(String name, Class<T> dataType, ChannelConfiguration configuration) {
		super();
		this.name = name;
		this.dataType = dataType;
		this.configuration = configuration;
	}

	/**
	 * Returns the channel's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the channel's configuration if any (can be null).
	 */
	public ChannelConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Returns the channel's data type.
	 */
	public Class<T> getDataType() {
		return dataType;
	}

	@Override
	public String toString() {
		return name;
	}

}
