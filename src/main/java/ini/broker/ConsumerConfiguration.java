package ini.broker;

import com.google.gson.GsonBuilder;

public class ConsumerConfiguration<T> {

	public static enum ConsumeStrategy {
		LATEST, EARLIEST
	}

	public ConsumerConfiguration() {
		super();
	}
	
	public ConsumerConfiguration(String consumerId, GsonBuilder gsonBuilder, Class<T> dataType) {
		super();
		this.consumerId = consumerId;
		this.gsonBuilder = gsonBuilder;
		this.dataType = dataType;
	}

	private String consumerId;
	private GsonBuilder gsonBuilder;
	private ConsumeStrategy consumeStrategy = ConsumeStrategy.LATEST;
	private Class<T> dataType;
	private long maxPollTime = 100;

	public GsonBuilder getGsonBuilder() {
		return gsonBuilder;
	}

	public ConsumeStrategy getConsumeStrategy() {
		return consumeStrategy;
	}

	public Class<T> getDataType() {
		return dataType;
	}

	public long getMaxPollTime() {
		return maxPollTime;
	}

	public String getConsumerId() {
		return consumerId;
	}

}
