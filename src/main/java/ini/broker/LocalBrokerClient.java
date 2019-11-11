package ini.broker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import ini.Main;

public class LocalBrokerClient<T> implements BrokerClient<T> {

	private static LocalBrokerClient<?> instance;
	private String name;
	private Map<String, BlockingQueue<Object>> channels = new Hashtable<>();
	private Map<String, Collection<String>> channelConsumers = new Hashtable<>();
	private Map<String, Thread> consumers = new Hashtable<>();
	private ConsumerConfiguration<T> consumerConfiguration;

	@SuppressWarnings("unchecked")
	synchronized public static <T> LocalBrokerClient<T> getInstance(String name,
			ConsumerConfiguration<T> consumerConfiguration) {
		if (instance == null) {
			Main.LOGGER.debug("creating local broker client");
			instance = new LocalBrokerClient<>(name, consumerConfiguration);
		}
		return (LocalBrokerClient<T>) instance;
	}

	private LocalBrokerClient(String name, ConsumerConfiguration<T> consumerConfiguration) {
		this.name = name;
		this.consumerConfiguration = consumerConfiguration;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	synchronized public void stopConsumer(String consumerId) {
		if (consumers.containsKey(consumerId)) {
			Main.LOGGER.debug("stopping consumer " + consumerId);
			consumers.get(consumerId).interrupt();
		}
	}

	synchronized private Collection<String> getOrCreateChannelCustomerIds(String channel) {
		Collection<String> consumerIds = channelConsumers.get(channel);
		if (consumerIds == null) {
			consumerIds = new ArrayList<>();
			channelConsumers.put(channel, consumerIds);
		}
		return consumerIds;
	}

	@Override
	synchronized public void stopConsumers(String channel) {
		Collection<String> consumerIds = getOrCreateChannelCustomerIds(channel);
		Main.LOGGER.debug("stopping consumers for channel " + channel);
		for (String consumerId : consumerIds) {
			stopConsumer(consumerId);
		}
	}

	@Override
	synchronized public boolean isConsumerRunning(String consumerId) {
		return consumers.containsKey(consumerId);
	}

	@SuppressWarnings("unchecked")
	synchronized private BlockingQueue<T> getOrCreateChannel(String channel) {
		BlockingQueue<Object> channelQueue = channels.get(channel);
		if (channelQueue == null) {
			channels.put(channel, channelQueue = new LinkedBlockingQueue<>());
		}
		return (BlockingQueue<T>) channelQueue;
	}

	@Override
	synchronized public String consume(String channel, java.util.function.Consumer<T> consumeHandler) {
		if (channel == null) {
			throw new RuntimeException("Cannot create consumer for null channel");
		}
		String id = channel + "-" + UUID.randomUUID().toString();
		getOrCreateChannelCustomerIds(channel).add(id);

		Main.LOGGER.debug("consumer polling from topic '" + channel + "'...");

		Thread thread = new Thread() {
			public void run() {
				while (true) {
					try {
						T data = getOrCreateChannel(channel).poll(consumerConfiguration.getMaxPollTime(),
								TimeUnit.MILLISECONDS);
						if (data != null) {
							Main.LOGGER.debug("consumed from '" + channel + "': " + data);
							if (consumeHandler != null) {
								consumeHandler.accept(data);
							}
						}
					} catch (InterruptedException e) {
						Main.LOGGER.debug("woke up consumer for " + channel);
						break;
					} finally {
					}
				}
				Main.LOGGER.debug("consumer '" + id + "' out of consume loop");
				getOrCreateChannelCustomerIds(channel).remove(id);
				consumers.remove(id);
				Main.LOGGER.debug("consumer '" + id + "' closed");
			}
		};

		consumers.put(id, thread);

		thread.start();

		return id;

	}

	@Override
	synchronized public void produce(String channel, T data) {
		try {
			getOrCreateChannel(channel).put(data);
			Main.LOGGER.debug("produced on channel " + channel + " - data=" + data);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	synchronized public void stop() {
		// TODO
	}
}
