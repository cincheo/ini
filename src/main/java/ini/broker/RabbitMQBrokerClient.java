package ini.broker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import ini.EnvironmentConfiguration;
import ini.Main;

public class RabbitMQBrokerClient implements BrokerClient {

	private Connection connection;
	private Map<String, String> tagToChannel = new HashMap<>();
	private ChannelConfiguration defaultChannelConfiguration;
	private Map<String, com.rabbitmq.client.Channel> channels = new HashMap<>();
	private Map<String, Collection<String>> channelConsumers = new Hashtable<>();

	private EnvironmentConfiguration configuration;
	private String name;

	public RabbitMQBrokerClient(String name, EnvironmentConfiguration configuration,
			ChannelConfiguration defaultChannelConfiguration) {
		Main.LOGGER.debug("creating broker client: " + name);
		this.name = name;
		this.configuration = configuration;
		this.defaultChannelConfiguration = defaultChannelConfiguration;
		ConnectionFactory factory = new ConnectionFactory();
		try {
			factory.setUri(this.configuration.brokerConnectionString);
			this.connection = factory.newConnection();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public String getName() {
		return name;
	}

	/*
	 * @Override public <T> void configureChannel(String channelName,
	 * ChannelConfiguration<T> configuration) {
	 * channelConfigurations.put(channelName, configuration); }
	 * 
	 * @SuppressWarnings("unchecked") private <T> ChannelConfiguration<T>
	 * getChannelConfiguration(String channelName) { ChannelConfiguration<?>
	 * configuration = channelConfigurations.get(channelName); if (configuration
	 * == null) { configuration = defaultChannelConfiguration; } return
	 * (ChannelConfiguration<T>)configuration; }
	 */

	public ChannelConfiguration getDefaultChannelConfiguration() {
		return defaultChannelConfiguration;
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

	private synchronized <T> com.rabbitmq.client.Channel getOrCreateChannel(Channel<T> channel) throws Exception {
		com.rabbitmq.client.Channel rmqChannel = channels.get(channel.getName());
		if (rmqChannel == null) {
			if (channel.getConfiguration() != null) {
				channel.getConfiguration().setParentConfiguration(defaultChannelConfiguration);
			}
			Main.LOGGER.debug(
					"creating channel '" + channel.getName() + "' (" + getConfiguration(channel).getSize() + ")");
			rmqChannel = connection.createChannel();
			if (getConfiguration(channel).getSize() > 0) {
				rmqChannel.basicQos(getConfiguration(channel).getSize());
			}
			channels.put(channel.getName(), rmqChannel);
			rmqChannel.queueDeclare(channel.getName(), false, false, false, null);
		}
		return rmqChannel;
	}

	private ChannelConfiguration getConfiguration(Channel<?> channel) {
		return channel.getConfiguration() == null ? defaultChannelConfiguration : channel.getConfiguration();
	}

	@Override
	public <T> String consume(Channel<T> channel, Function<T,Boolean> consumeHandler) {
		if (channel == null) {
			throw new RuntimeException("Cannot create consumer for null channel");
		}
		try {

			com.rabbitmq.client.Channel rmqChannel = getOrCreateChannel(channel);

			Main.LOGGER.debug("consumer polling from queue '" + channel.getName() + "'...");

			Consumer c = new DefaultConsumer(rmqChannel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					Main.LOGGER.debug(String.format("consumed: (queue=%s, consumerId=%s, value=%s, size=%s)",
							channel.getName(), consumerTag, message, getConfiguration(channel).getSize()));

					try {
						boolean result = true;
						if (consumeHandler != null) {
							result = consumeHandler.apply(getConfiguration(channel).getGsonBuilder().create().fromJson(message,
									channel.getDataType()));
						}
						if(result) {
							rmqChannel.basicAck(envelope.getDeliveryTag(), false);
						} else {
							Main.LOGGER.debug(String.format("reject: (queue=%s, consumerId=%s, value=%s)",
									channel.getName(), consumerTag, message));
							rmqChannel.basicReject(envelope.getDeliveryTag(), true);
						}
					} catch (Exception e) {
						Main.LOGGER.error("error deserializing: " + message + " - ignoring", e);
					}
				}
			};
			String tag = rmqChannel.basicConsume(channel.getName(), false, c);

			getOrCreateChannelCustomerIds(channel.getName()).add(tag);
			tagToChannel.put(tag, channel.getName());

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
	public <T> void produce(Channel<T> channel, T data) {
		long time = System.currentTimeMillis();
		try {
			com.rabbitmq.client.Channel rmqChannel = getOrCreateChannel(channel);
			String message = new Gson().toJson(data);
			rmqChannel.basicPublish("", channel.getName(), null, message.getBytes());
			long elapsedTime = System.currentTimeMillis() - time;
			Main.LOGGER.debug(
					String.format("produced: (queue=%s, value=%s) time=%d", channel.getName(), message, elapsedTime));

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
