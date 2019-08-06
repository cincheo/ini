package ini.broker;

import java.util.function.Consumer;

public interface BrokerClient<T> {
	
	void produce(String channel, T data);
	
	void consume(String channel, Consumer<T> consumeHandler) throws InterruptedException;

	void close();
	
}
