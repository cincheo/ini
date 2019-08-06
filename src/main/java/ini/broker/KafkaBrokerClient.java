package ini.broker;

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

import ini.Main;

public class KafkaBrokerClient<T> implements BrokerClient<T> {

	Producer<Long, String> producer;
	ConsumerConfiguration<T> consumerConfiguration;
	boolean verbose = false;

	public KafkaBrokerClient(boolean verbose, ConsumerConfiguration<T> consumerConfiguration) {
		this.verbose = verbose;
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
				Main.configuration.environments.get(Main.environment).bootstrapBrokerServers);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "IniProducer");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		this.producer = new KafkaProducer<>(props);
		this.consumerConfiguration = consumerConfiguration;
	}

	private Consumer<Long, String> createConsumer(String channel) {
		Consumer<Long, String> consumer = null;
		final Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
				Main.configuration.environments.get(Main.environment).bootstrapBrokerServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "IniConsumer");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		if (consumerConfiguration.getConsumeStrategy() == ConsumerConfiguration.ConsumeStrategy.EARLIEST) {
			props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		}

		// Create the consumer using props.
		consumer = new KafkaConsumer<>(props);

		// Subscribe to the topic.
		consumer.subscribe(Collections.singletonList(channel));
		return consumer;
	}
	
	@Override
	public void consume(String channel, java.util.function.Consumer<T> consumeHandler) throws InterruptedException {
		Consumer<Long, String> consumer = createConsumer(channel);

		List<T> results = new ArrayList<>();

		if (verbose) {
			System.out.println("[KAFKA] Consumer polling from topic " + channel);
		}
		while (true) {
			
			final ConsumerRecords<Long, String> consumerRecords = consumer.poll(java.time.Duration.ofMillis(1000));

			if (consumerRecords.count() == 0) {
				continue;
			} else {
				consumerRecords.forEach(record -> {
					if (verbose) {
						System.out.printf("[KAFKA] Consuming: (topic=%s, %d, %s, %d, %d)\n", channel, record.key(),
								record.value(), record.partition(), record.offset());
					}
					try {
						results.add(consumerConfiguration.getGsonBuilder().create().fromJson(record.value(),
								consumerConfiguration.getDataType()));
					} catch (Exception e) {
						System.err.println("[KAFKA] Error deserializing: " + record.value() + " - ignoring");
						e.printStackTrace();
					}
				});
				if (verbose) {
					System.out.println("[KAFKA] Commit consume from '" + channel + "'");
				}
				consumer.commitAsync();
				break;
			}
		}
		if (verbose) {
			System.out.println("[KAFKA] Consumer '" + channel + "' closed");
		}
		if (consumer != null) {
			consumer.close();
		}
		for(T data : results) {
			consumeHandler.accept(data);
		}
	}

	@Override
	public void produce(String channel, Object data) {
		long time = System.currentTimeMillis();

		try {
			final ProducerRecord<Long, String> record = new ProducerRecord<>(channel, new Gson().toJson(data));

			RecordMetadata metadata = producer.send(record).get();

			long elapsedTime = System.currentTimeMillis() - time;
			if (verbose) {
				System.out.printf(
						"[KAFKA] Produced: (topic=%s, key=%s, value=%s) " + "meta(partition=%d, offset=%d) time=%d\n",
						channel, record.key(), record.value(), metadata.partition(), metadata.offset(), elapsedTime);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			producer.flush();
		}
	}

	@Override
	public void close() {
		producer.close();
	}

}
