package ini.broker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import ini.EnvironmentConfiguration;
import ini.Main;

public class RabbitMQBrokerClient<T> implements BrokerClient<T> {

	private Connection connection;
	private ConsumerConfiguration<T> consumerConfiguration;
	private Map<String, String> tagToChannel = new HashMap<>();
	private Map<String, Channel> channels = new HashMap<>();
	private Map<String, Collection<String>> channelConsumers = new Hashtable<>();

	private EnvironmentConfiguration configuration;
	private String name;

	public RabbitMQBrokerClient(String name, EnvironmentConfiguration configuration,
			ConsumerConfiguration<T> consumerConfiguration) {
		Main.LOGGER.debug("creating broker client: " + name);
		this.name = name;
		this.configuration = configuration;
		this.consumerConfiguration = consumerConfiguration;
		ConnectionFactory factory = new ConnectionFactory();
		try {
			factory.setHost("localhost");
			// factory.setUri(configuration.brokerConnectionString);
			this.connection = factory.newConnection();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public String getName() {
		return name;
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
	public void stopConsumer(String consumerId) {
		String channel = tagToChannel.get(consumerId);
		if (channel != null) {
			try {
				if (isConsumerRunning(consumerId)) {
					Main.LOGGER.debug("stopping consumer for channel " + channel);
					channels.get(channel).basicCancel(consumerId);
					;
				}
			} catch (Exception e) {
			} finally {
				tagToChannel.remove(consumerId);
				getOrCreateChannelCustomerIds(channel).remove(consumerId);
			}

		}
	}

	@Override
	public void stopConsumers(String channel) {
		Collection<String> consumerIds = new ArrayList<>(getOrCreateChannelCustomerIds(channel));
		Main.LOGGER.debug("stopping consumers for channel " + channel);
		for (String consumerId : consumerIds) {
			stopConsumer(consumerId);
		}
	}

	@Override
	synchronized public void stop() {
		Main.LOGGER.debug("stopping broker client: " + name);
		for (String channelName : channels.keySet()) {
			stopConsumers(channelName);
			try {
				channels.get(channelName).close();
			} catch (Exception e) {
				Main.LOGGER.debug("cannot close channel " + channelName, e);
			}
		}
		try {
			connection.close();
		} catch (Exception e) {
			Main.LOGGER.debug("cannot close broker connection ", e);
		}
	}

	@Override
	public boolean isConsumerRunning(String channel) {
		return channels.containsKey(channel);
	}

	@Override
	public String consume(String channelName, java.util.function.Consumer<T> consumeHandler) {
		if (channelName == null) {
			throw new RuntimeException("Cannot create consumer for null channel");
		}
		Channel channel = channels.get(channelName);
		try {

			if (channel == null) {
				channel = connection.createChannel();
				channels.put(channelName, channel);
				channel.queueDeclare(channelName, false, false, false, null);
			} else {
				Main.LOGGER.error("consumer polling existing channel '" + channelName + "'...", new Exception());
			}

			// System.out.println(" [*] Waiting for messages. To exit press
			// CTRL+C");

			Main.LOGGER.debug("consumer polling from queue '" + channelName + "'...");
			Main.LOGGER.debug("configuration: " + consumerConfiguration);

			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String message = new String(delivery.getBody(), "UTF-8");
				Main.LOGGER.debug("consumer polled message from queue '" + channelName + "'");
				try {
					if (consumeHandler != null) {
						consumeHandler.accept(consumerConfiguration.getGsonBuilder().create().fromJson(message,
								consumerConfiguration.getDataType()));
					}
				} catch (Exception e) {
					Main.LOGGER.error("error deserializing: " + message + " - ignoring", e);
				}
			};

			String tag = channel.basicConsume(channelName, true, deliverCallback, consumerTag -> {
			});

			getOrCreateChannelCustomerIds(channelName).add(tag);
			tagToChannel.put(tag, channelName);

			return tag;

			/*
			 * GetResponse get = channel.basicGet(channelName, false); if
			 * (consumeHandler != null) { String message = new
			 * String(get.getBody(), "UTF-8");
			 * consumeHandler.accept(consumerConfiguration.getGsonBuilder().
			 * create().fromJson(message, consumerConfiguration.getDataType()));
			 * }
			 */

			// Main.LOGGER.debug("end basic consume");

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void produce(String channelName, Object data) {
		long time = System.currentTimeMillis();
		try {
			Main.LOGGER.debug("producing on channel " + channelName);
			try (Channel channel = connection.createChannel()) {
				channel.queueDeclare(channelName, false, false, false, null);
				String message = new Gson().toJson(data);
				channel.basicPublish("", channelName, null, message.getBytes());
				long elapsedTime = System.currentTimeMillis() - time;
				Main.LOGGER.debug(
						String.format("produced: (queue=%s, value=%s) time=%d", channelName, message, elapsedTime));
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
