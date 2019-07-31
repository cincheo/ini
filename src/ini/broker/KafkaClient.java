package ini.broker;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import ini.eval.data.Data;
import ini.eval.data.RawData;

public class KafkaClient {

	/*
	 * static class TypedObject { private String type; private String json;
	 * public TypedObject(Object object) { this.type =
	 * object.getClass().getTypeName(); this.json = new Gson().toJson(object); }
	 * public Object getObject() { try { return new Gson().fromJson(json,
	 * Class.forName(type)); } catch (Exception e) { e.printStackTrace(); return
	 * new Gson().fromJson(json, String.class); } } }
	 */

	private final static boolean VERBOSE = false;
	// private final static String TOPIC = "my-example-topic";
	private final static String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";

	private final static GsonBuilder gsonBuilder = new GsonBuilder();

	static {
		gsonBuilder.registerTypeAdapter(Data.class, new JsonDeserializer<RawData>() {
			@Override
			public RawData deserialize(JsonElement json, Type type, JsonDeserializationContext context)
					throws JsonParseException {
				return gsonBuilder.create().fromJson(json, RawData.class).tryNumerizeKeys();
			}
		});

	}

	/*
	 * static {
	 * 
	 * Gson gson = new GsonBuilder(); TypeAdapter<RawData> typeAdapter = new
	 * JsonDeserializer<RawData>() {
	 * 
	 * @Override public RawData deserialize(JsonElement json, Type type,
	 * JsonDeserializationContext context) throws JsonParseException {
	 * JsonObject jsonObject = json.getAsJsonObject();
	 * 
	 * return null; }
	 * 
	 * }).create();
	 * 
	 * }
	 */

	private static Producer<Long, String> createProducer() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "IniProducer");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		return new KafkaProducer<>(props);
	}

	private static Consumer<Long, String> createConsumer(String topic) {
		final Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "IniConsumer");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		// Create the consumer using props.
		final Consumer<Long, String> consumer = new KafkaConsumer<>(props);

		// Subscribe to the topic.
		consumer.subscribe(Collections.singletonList(topic));
		return consumer;
	}

	public static void runProducer(final String topic, final RawData message) throws Exception {
		final Producer<Long, String> producer = createProducer();
		long time = System.currentTimeMillis();

		try {
			final ProducerRecord<Long, String> record = new ProducerRecord<>(topic, new Gson().toJson(message));

			RecordMetadata metadata = producer.send(record).get();

			long elapsedTime = System.currentTimeMillis() - time;
			if (VERBOSE) {
				System.out.printf("sent record(topic=%s key=%s value=%s) " + "meta(partition=%d, offset=%d) time=%d\n",
						topic, record.key(), record.value(), metadata.partition(), metadata.offset(), elapsedTime);
			}

		} finally {
			producer.flush();
			producer.close();
		}
	}

	public static List<Data> runConsumer(final String topic) throws InterruptedException {
		final Consumer<Long, String> consumer = createConsumer(topic);
		List<Data> result = new ArrayList<>();
		while (true) {
			if (VERBOSE) {
				System.out.println("Consumer polling from topic " + topic);
			}
			final ConsumerRecords<Long, String> consumerRecords = consumer.poll(java.time.Duration.ofMillis(1000));

			if (consumerRecords.count() == 0) {
				continue;
			} else {
				consumerRecords.forEach(record -> {
					if (VERBOSE) {
						System.out.printf("Consumer Record:(%d, %s, %d, %d)\n", record.key(), record.value(),
								record.partition(), record.offset());
					}
					try {
						result.add(gsonBuilder.create().fromJson(record.value(), Data.class));
					} catch (Exception e) {
						System.err.println("error deserializing: " + record.value() + " - ignoring");
						e.printStackTrace();
					}
				});
				consumer.commitAsync();
				break;
			}
		}
		consumer.close();
		if (VERBOSE) {
			System.out.println("DONE");
		}
		return result;
	}
}
