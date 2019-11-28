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

public class LocalBrokerClient implements BrokerClient {

	private static LocalBrokerClient instance;
	private String name;
	private ChannelConfiguration defaultChannelConfiguration;
	private Map<String, BlockingQueue<Object>> channels = new Hashtable<>();
	private Map<String, Collection<String>> channelConsumers = new Hashtable<>();
	private Map<String, Thread> consumers = new Hashtable<>();

	synchronized public static LocalBrokerClient getInstance(String name) {
		if (instance == null) {
			Main.LOGGER.debug("creating local broker client");
			instance = new LocalBrokerClient(name);
		}
		return instance;
	}

	private LocalBrokerClient(String name) {
		this.name = name;
		this.defaultChannelConfiguration = new ChannelConfiguration(1);
	}

	@Override
	public ChannelConfiguration getDefaultChannelConfiguration() {
		return defaultChannelConfiguration;
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
			consumers.remove(consumerId);
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
	public void stopConsumers(String channelName) {
		Collection<String> consumerIds = getOrCreateChannelCustomerIds(channelName);
		Main.LOGGER.debug("stopping consumers for channel " + channelName);
		for (String consumerId : consumerIds) {
			stopConsumer(consumerId);
		}
	}

	@Override
	synchronized public boolean isConsumerRunning(String consumerId) {
		return consumers.containsKey(consumerId);
	}

	private ChannelConfiguration getConfiguration(Channel<?> channel) {
		return channel.getConfiguration() == null ? defaultChannelConfiguration : channel.getConfiguration();
	}

	@SuppressWarnings("unchecked")
	synchronized private <T> BlockingQueue<T> getOrCreateChannel(Channel<T> channel) {
		BlockingQueue<Object> channelQueue = channels.get(channel.getName());
		if (channelQueue == null) {
			if (channel.getConfiguration() != null) {
				channel.getConfiguration().setParentConfiguration(defaultChannelConfiguration);
			}
			Main.LOGGER.debug("create channel " + channel + " / " + getConfiguration(channel).getSize());
			channels.put(channel.getName(),
					channelQueue = new LinkedBlockingQueue<>(getConfiguration(channel).getSize()));
		}
		return (BlockingQueue<T>) channelQueue;
	}

	@Override
	public <T> String consume(Channel<T> channel, java.util.function.Consumer<T> consumeHandler) {
		if (channel == null) {
			throw new RuntimeException("Cannot create consumer for null channel");
		}
		String id = channel + "-" + UUID.randomUUID().toString();
		getOrCreateChannelCustomerIds(channel.getName()).add(id);

		Main.LOGGER.debug("consumer polling from topic '" + channel + "'...");

		Thread thread = new Thread() {
			public void run() {
				while (true && !Thread.interrupted()) {
					try {
						BlockingQueue<T> queue = getOrCreateChannel(channel);
						T data = queue.poll(10000, TimeUnit.MILLISECONDS);
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
				getOrCreateChannelCustomerIds(channel.getName()).remove(id);
				consumers.remove(id);
				Main.LOGGER.debug("consumer '" + id + "' closed");
			}
		};

		consumers.put(id, thread);

		thread.start();

		return id;

	}

	@Override
	public <T> void produce(Channel<T> channel, T data) {
		try {
			Main.LOGGER.debug("producing on channel " + channel + " - data=" + data);
			getOrCreateChannel(channel).put(data);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	synchronized public void stop() {
		for (String channelName : channels.keySet()) {
			stopConsumers(channelName);
		}
		instance = null;
	}
}
