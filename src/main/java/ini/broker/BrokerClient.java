package ini.broker;

import java.lang.reflect.Type;
import java.util.function.Consumer;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import ini.eval.data.Data;
import ini.eval.data.RawData;
import ini.parser.IniParser;

public interface BrokerClient<T> {

	boolean VERBOSE = false;

	public static BrokerClient<Data> createDefaultInstance(IniParser parser, boolean global) {
		if (global) {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Data.class, new JsonDeserializer<RawData>() {
				@Override
				public RawData deserialize(JsonElement json, Type type, JsonDeserializationContext context)
						throws JsonParseException {
					RawData data = gsonBuilder.create().fromJson(json, RawData.class).tryNumerizeKeys().applyTypeInfo();
					return data;
				}
			});

			return new KafkaBrokerClient<>(VERBOSE, parser.getEnvironmentConfiguration(), new ConsumerConfiguration<>(
					parser.getEnvironmentConfiguration().consumerGroupId, gsonBuilder, Data.class));
		} else {
			return new LocalBrokerClient<>(new ConsumerConfiguration<>());
		}
	}

	void produce(String channel, T data);

	void consume(String channel, Consumer<T> consumeHandler);

	void stopConsumer(String channel);

	boolean isConsumerRunning(String channel);

}
