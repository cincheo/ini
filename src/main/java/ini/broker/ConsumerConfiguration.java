package ini.broker;

import com.google.gson.GsonBuilder;

public class ConsumerConfiguration<T> {

	public ConsumerConfiguration() {
		super();
	}

	public ConsumerConfiguration(GsonBuilder gsonBuilder, Class<T> dataType) {
		super();
		this.gsonBuilder = gsonBuilder;
		this.dataType = dataType;
	}

	private GsonBuilder gsonBuilder;
	private Class<T> dataType;
	private long maxPollTime = 10000;

	public GsonBuilder getGsonBuilder() {
		return gsonBuilder;
	}

	public Class<T> getDataType() {
		return dataType;
	}

	public long getMaxPollTime() {
		return maxPollTime;
	}

	@Override
	public String toString() {
		return String.format("builder=%s, dataType=%s, timeout=%s", gsonBuilder, dataType, maxPollTime);
	}

}
