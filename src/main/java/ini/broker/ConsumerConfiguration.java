package ini.broker;

import com.google.gson.GsonBuilder;

public class ConsumerConfiguration<T> {

	public static enum ConsumeStrategy {
		LATEST, EARLIEST
	}
	
	public ConsumerConfiguration(ConsumeStrategy consumeStrategy, long maxPollTime, GsonBuilder gsonBuilder, Class<T> dataType) {
		super();
		this.consumeStrategy = consumeStrategy;
		this.maxPollTime = maxPollTime;
		this.gsonBuilder = gsonBuilder;
		this.dataType = dataType;
	}
	
	private GsonBuilder gsonBuilder;
	private ConsumeStrategy consumeStrategy;
	private Class<T> dataType;
	private long maxPollTime = 1000;
	
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
	
}
