package ini.broker;

import com.google.gson.GsonBuilder;

public class ChannelConfiguration {

	public ChannelConfiguration(int size) {
		super();
		this.size = size;
	}

	public ChannelConfiguration(GsonBuilder gsonBuilder, int size) {
		super();
		this.gsonBuilder = gsonBuilder;
		this.size = size;
	}

	private ChannelConfiguration parentConfiguration = null;
	private GsonBuilder gsonBuilder;
	private int size = -1;

	public ChannelConfiguration setParentConfiguration(ChannelConfiguration parentConfiguration) {
		this.parentConfiguration = parentConfiguration;
		return this;
	}

	public GsonBuilder getGsonBuilder() {
		if (gsonBuilder == null && parentConfiguration != null) {
			return parentConfiguration.getGsonBuilder();
		} else {
			return gsonBuilder;
		}
	}

	@Override
	public String toString() {
		return String.format("builder=%s, size=%s", gsonBuilder, size);
	}

	public int getSize() {
		if (size <= 0 && parentConfiguration != null) {
			return parentConfiguration.getSize();
		} else {
			return size;
		}
	}

}
