package ini.broker;

import java.util.function.Consumer;

import ini.IniEnv;
import ini.eval.data.Data;

/**
 * A generic interface to produce or consume on channels (queues) managed by a
 * broker. The broker can be local or global.
 * 
 * @author Renaud Pawlak
 *
 * @param <T>
 *            the type of data that will be produced or consumed on the channels
 */
public interface BrokerClient<T> {

	/**
	 * Returns a default instance of this interface, to be used as a message
	 * broker for INI.
	 * 
	 * @param env
	 *            the INI env
	 * @param global
	 *            tells if we need to connect to the global broker (if false, we
	 *            return the local broker - for local-only channel)
	 * @return the default INI message broker
	 */
	public static BrokerClient<Data> getDefaultInstance(IniEnv env, boolean global) {
		if (global && env.coreBrokerClient != null) {
			return env.coreBrokerClient.getDefaultRemoteBrokerClient();
			// return RabbitMQBrokerClient.getDefaultInstance(env);
		} else {
			return LocalBrokerClient.getInstance("local-default", new ConsumerConfiguration<>());
		}
	}

	/**
	 * Gets the name this broker.
	 * @return the name
	 */
	String getName();
	
	/**
	 * Produce a data on a given channel (channel = queue).
	 * 
	 * @param channel
	 *            the channel name
	 * @param data
	 *            the data to be produced
	 */

	void produce(String channel, T data);

	/**
	 * Creates a consumer for the given channel (a thread that will consume from
	 * the channel and call the consume handler when a data is available). If
	 * several consumers are created for the same channel, then they will
	 * compete with an arbitrary election policy (random). This is a
	 * non-blocking method.
	 * 
	 * @param channel
	 *            the channel to consume from
	 * @param consumeHandler
	 *            the handler to call when a data is read from the channel
	 * @return a consumer id to stop the consumer with
	 */
	String consume(String channel, Consumer<T> consumeHandler);

	/**
	 * Stops the given consumer.
	 * 
	 * @param consumerId
	 *            the id of the consumer, as returned by
	 *            {@link #consume(String, Consumer)}
	 */
	void stopConsumer(String consumerId);

	/**
	 * Stops all started consumers for a given channel.
	 * 
	 * @param channel
	 *            the channel to stop the consumers
	 */
	void stopConsumers(String channel);

	/**
	 * Tells if the given consumer is running.
	 * 
	 * @param consumerId
	 *            the id of the consumer, as returned by
	 *            {@link #consume(String, Consumer)}
	 */
	boolean isConsumerRunning(String consumerId);

	/**
	 * Stops the broker client (stops all consumers).
	 */
	void stop();

}
