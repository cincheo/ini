package ini.broker;

import java.lang.reflect.Type;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import ini.broker.ConsumerConfiguration.ConsumeStrategy;
import ini.eval.data.Data;
import ini.eval.data.RawData;

public class DefaultBrokerClient {

	private static final boolean VERBOSE = false;

	private static BrokerClient<Data> instance;

	synchronized public static BrokerClient<Data> getInstance() {
		if (instance == null) {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Data.class, new JsonDeserializer<RawData>() {
				@Override
				public RawData deserialize(JsonElement json, Type type, JsonDeserializationContext context)
						throws JsonParseException {
					RawData data = gsonBuilder.create().fromJson(json, RawData.class).tryNumerizeKeys().applyTypeInfo();
					return data;
				}
			});

			instance = new KafkaBrokerClient<>(VERBOSE,
					new ConsumerConfiguration<>(ConsumeStrategy.EARLIEST, 1000, gsonBuilder, Data.class));
		}
		return instance;
	}

}
