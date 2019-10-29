package ini.broker;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import ini.Main;

public class LocalBrokerClient<T> implements BrokerClient<T> {

	private static LocalBrokerClient<?> instance;
	private Map<String, BlockingQueue<Object>> channels = new Hashtable<>();
	private Map<String, Thread> consumers = new Hashtable<>();
	private Map<String, AtomicBoolean> consumerCloseStates = new Hashtable<>();
	private ConsumerConfiguration<T> consumerConfiguration;

	@SuppressWarnings("unchecked")
	synchronized public static <T> LocalBrokerClient<T> getInstance(ConsumerConfiguration<T> consumerConfiguration) {
		if (instance == null) {
			instance = new LocalBrokerClient<>(consumerConfiguration);
		}
		return (LocalBrokerClient<T>) instance;
	}

	private LocalBrokerClient(ConsumerConfiguration<T> consumerConfiguration) {
		this.consumerConfiguration = consumerConfiguration;
	}

	@Override
	synchronized public void stopConsumer(String channel) {
		if (consumers.containsKey(channel)) {
			Main.LOGGER.debug("stopping consumer for channel " + channel);
			consumerCloseStates.get(channel).set(true);
		}
	}

	@Override
	synchronized public boolean isConsumerRunning(String channel) {
		return consumers.containsKey(channel);
	}

	@SuppressWarnings("unchecked")
	synchronized private BlockingQueue<T> getOrCreateChannel(String channel) {
		BlockingQueue<Object> channelQueue = channels.get(channel);
		if (channelQueue == null) {
			channels.put(channel, channelQueue = new ArrayBlockingQueue<>(1000));
		}
		return (BlockingQueue<T>) channelQueue;
	}

	@Override
	public void consume(String channel, java.util.function.Consumer<T> consumeHandler) {
		if (channel == null) {
			throw new RuntimeException("Cannot create consumer for null channel");
		}
		consumers.put(channel, Thread.currentThread());
		consumerCloseStates.put(channel, new AtomicBoolean(false));

		Main.LOGGER.debug("consumer polling from topic '" + channel + "'...");

		while (!consumerCloseStates.get(channel).get()) {
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
				consumerCloseStates.get(channel).set(true);
			} finally {
			}
		}
		Main.LOGGER.debug("consumer '" + channel + "' out of consume loop");
		consumers.remove(channel);
		consumerCloseStates.remove(channel);
		Main.LOGGER.debug("consumer '" + channel + "' closed");
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

}
