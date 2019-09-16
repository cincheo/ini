package ini.broker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.google.gson.Gson;

import ini.EnvironmentConfiguration;
import ini.Main;

public class KafkaBrokerClient<T> implements BrokerClient<T> {

	private static Producer<Long, String> producer;
	private ConsumerConfiguration<T> consumerConfiguration;
	private Map<String, Consumer<Long, String>> consumers = new HashMap<>();
	private Map<String, AtomicBoolean> consumerCloseStates = new HashMap<>();
	boolean verbose = false;
	private EnvironmentConfiguration configuration;

	private static synchronized Producer<Long, String> getProducerInstance(EnvironmentConfiguration configuration) {
		if (producer == null) {
			Properties props = new Properties();
			props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.bootstrapBrokerServers);
			props.put(ProducerConfig.CLIENT_ID_CONFIG, "IniProducer");
			props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
			props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
			producer = new KafkaProducer<>(props);
		}
		return producer;
	}

	public KafkaBrokerClient(boolean verbose, EnvironmentConfiguration configuration,
			ConsumerConfiguration<T> consumerConfiguration) {
		this.verbose = verbose;
		this.configuration = configuration;
		this.consumerConfiguration = consumerConfiguration;
	}

	private Consumer<Long, String> createConsumer(String channel) {
		Main.LOGGER.debug("creating '" + consumerConfiguration.getConsumerId() + "' consumer for channel " + channel);
		if (consumers.containsKey(channel)) {
			stopConsumer(channel);
		}
		Consumer<Long, String> consumer = null;
		final Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.bootstrapBrokerServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerConfiguration.getConsumerId());
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		if (consumerConfiguration.getConsumeStrategy() == ConsumerConfiguration.ConsumeStrategy.EARLIEST) {
			Main.LOGGER.debug("consume strategy is 'earliest'");
			props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		}

		Main.LOGGER.debug("creating consumer");
		// Create the consumer using props.
		consumer = new KafkaConsumer<>(props);
		Main.LOGGER.debug("done creating consumer");

		if (channel != null) {
			consumers.put(channel, consumer);
			consumerCloseStates.put(channel, new AtomicBoolean(false));
			// Subscribe to the topic.
			consumer.subscribe(Collections.singletonList(channel));
			Main.LOGGER.debug("subscribed consumer to " + channel);
		}
		return consumer;
	}

	@Override
	public void stopConsumer(String channel) {
		if (consumers.containsKey(channel)) {
			Main.LOGGER.debug("stopping consumer for channel " + channel);
			consumerCloseStates.get(channel).set(true);
			try {
				Thread.sleep(consumerConfiguration.getMaxPollTime() * 2);
				if (isConsumerRunning(channel)) {
					Main.LOGGER.debug("wakeup");
					consumers.get(channel).wakeup();
				}
			} catch (Exception e) {
			}
		}
	}

	@Override
	public boolean isConsumerRunning(String channel) {
		return consumers.containsKey(channel);
	}

	@Override
	public void consume(String channel, java.util.function.Consumer<T> consumeHandler) {
		if (channel == null) {
			throw new RuntimeException("Cannot create consumer for null channel");
		}
		Consumer<Long, String> consumer = createConsumer(channel);

		Main.LOGGER.debug("consumer polling from topic '" + channel + "'...");

		while (!consumerCloseStates.get(channel).get()) {
			try {
				final ConsumerRecords<Long, String> consumerRecords = consumer
						.poll(java.time.Duration.ofMillis(consumerConfiguration.getMaxPollTime()));

				if (consumerRecords.count() == 0) {
					// Main.LOGGER.debug("consumer timeout: "+channel);
					continue;
				} else {
					Main.LOGGER.debug(
							"consumer polled " + consumerRecords.count() + " records from topic '" + channel + "'");
					consumerRecords.forEach(record -> {
						Main.LOGGER.debug(String.format("accept: (topic=%s, %d, %s, %d, %d)", channel, record.key(),
								record.value(), record.partition(), record.offset()));
						try {
							if (consumeHandler != null) {
								consumeHandler.accept(consumerConfiguration.getGsonBuilder().create()
										.fromJson(record.value(), consumerConfiguration.getDataType()));
							}
						} catch (Exception e) {
							Main.LOGGER.error("error deserializing: " + record.value() + " - ignoring", e);
						}
					});
					Main.LOGGER.debug("commiting consume from '" + channel + "'");
					consumer.commitSync();
				}
			} catch (WakeupException wakeupException) {
				Main.LOGGER.debug("woke up consumer for " + channel);
				consumerCloseStates.get(channel).set(true);
			} finally {
			}
		}
		Main.LOGGER.debug("consumer '" + channel + "' out of consume loop");
		consumers.remove(channel);
		consumerCloseStates.remove(channel);
		try {
			consumer.close();
		} catch (Exception e) {
			Main.LOGGER.error("consumer raised an exception while closing");
		}
		Main.LOGGER.debug("consumer '" + channel + "' closed");

	}

	@Override
	public void produce(String channel, Object data) {
		long time = System.currentTimeMillis();

		try {
			final ProducerRecord<Long, String> record = new ProducerRecord<>(channel, new Gson().toJson(data));

			Main.LOGGER.debug("producing on channel " + channel);
			RecordMetadata metadata = getProducerInstance(configuration).send(record).get();

			long elapsedTime = System.currentTimeMillis() - time;
			Main.LOGGER.debug(String.format(
					"produced: (topic=%s, key=%s, value=%s) " + "meta(partition=%d, offset=%d) time=%d", channel,
					record.key(), record.value(), metadata.partition(), metadata.offset(), elapsedTime));

		} catch (Exception e) {
			throw new RuntimeException(e);
		}// finally {
		//	producer.flush();
		//}
	}

}
