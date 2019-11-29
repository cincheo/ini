package ini.broker;

import java.lang.reflect.Type;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import ini.IniEnv;
import ini.Main;
import ini.ast.AstNode;
import ini.ast.Executable;
import ini.ast.Expression;
import ini.ast.NumberLiteral;
import ini.ast.Statement;
import ini.ast.VariableAccess;
import ini.eval.data.Data;
import ini.eval.data.RawData;

public class CoreBrokerClient {

	private IniEnv env;

	public CoreBrokerClient(IniEnv env) {
		this.env = env;
	}

	private static final String SPAWN_REQUEST_SUFFIX = "_spawn_request";
	private static final String SPAWN_ACK_SUFFIX = "_spawn_ack";
	private static final String FETCH_REQUEST_SUFFIX = "_fetch_request";
	private static final String DEPLOY_REQUEST_SUFFIX = "_deploy_request";

	private BrokerClient coreRemoteBrokerClient;
	private BrokerClient defaultRemoteBrokerClient;

	private Channel<SpawnRequest> getSpawnChannel(String node) {
		return new Channel<>(node + SPAWN_REQUEST_SUFFIX, SpawnRequest.class, null);
	}

	private Channel<FetchRequest> getFetchChannel(String node) {
		return new Channel<>(node + FETCH_REQUEST_SUFFIX, FetchRequest.class, null);
	}

	private Channel<DeployRequest> getDeployChannel(String node) {
		return new Channel<>(node + DEPLOY_REQUEST_SUFFIX, DeployRequest.class, null);
	}

	public synchronized BrokerClient getDefaultRemoteBrokerClient() {
		if (defaultRemoteBrokerClient == null) {
			defaultRemoteBrokerClient = createDefaultInstance(env);
		}
		return defaultRemoteBrokerClient;
	}

	public void stop() {
		if (coreRemoteBrokerClient != null) {
			coreRemoteBrokerClient.stop();
		}
		if (defaultRemoteBrokerClient != null) {
			defaultRemoteBrokerClient.stop();
		}
	}

	/*
	 * public static void clearCoreChannels() { Main.parseConfiguration();
	 * Main.LOGGER.info("clearing core channels: " +
	 * Main.getEnvironmentConfiguration().coreConsumerGroupId);
	 * KafkaBrokerClient<Object> client = new KafkaBrokerClient<Object>(true,
	 * new ConsumerConfiguration<Object>(
	 * Main.getEnvironmentConfiguration().coreConsumerGroupId, new
	 * GsonBuilder(), Object.class)); Consumer<Long, String> c =
	 * client.createConsumer(null); System.out.println(c.listTopics());
	 * 
	 * for (String topic : c.listTopics().keySet()) { if
	 * (topic.endsWith(CoreBrokerClient.DEPLOY_REQUEST_SUFFIX) ||
	 * topic.endsWith(CoreBrokerClient.FETCH_REQUEST_SUFFIX) ||
	 * topic.endsWith(CoreBrokerClient.SPAWN_REQUEST_SUFFIX)) {
	 * Main.LOGGER.info("clearing core channel: " + topic); new Thread() {
	 * public void run() { client.consume(topic, null); } }.start(); try {
	 * Thread.sleep(100); } catch (InterruptedException e) {
	 * e.printStackTrace(); } client.stopConsumer(topic); } } }
	 */

	private synchronized BrokerClient getCoreRemoteBrokerClient() {
		if (coreRemoteBrokerClient == null) {
			GsonBuilder coreGsonBuilder = new GsonBuilder();
			coreGsonBuilder.registerTypeAdapter(AstNode.class, new AstNodeDeserializer())
					.registerTypeAdapter(VariableAccess.class, new AstNodeDeserializer())
					.registerTypeAdapter(Expression.class, new AstNodeDeserializer())
					.registerTypeAdapter(Executable.class, new AstNodeDeserializer())
					.registerTypeAdapter(Statement.class, new AstNodeDeserializer());
			coreGsonBuilder.registerTypeAdapter(Data.class, new JsonDeserializer<RawData>() {
				@Override
				public RawData deserialize(JsonElement json, Type type, JsonDeserializationContext context)
						throws JsonParseException {
					return coreGsonBuilder.create().fromJson(json, RawData.class).tryNumerizeKeys()
							.applyTypeInfo(coreGsonBuilder);
				}
			});
			coreGsonBuilder.registerTypeAdapter(NumberLiteral.class, new JsonDeserializer<NumberLiteral>() {

				@Override
				public NumberLiteral deserialize(JsonElement json, Type type, JsonDeserializationContext context)
						throws JsonParseException {
					// context.deserialize(json, type);
					return new Gson().fromJson(json, NumberLiteral.class).applyTypeInfo();
				}
			});

			coreRemoteBrokerClient = new RabbitMQBrokerClient("core", env.getEnvironmentConfiguration(),
					new ChannelConfiguration(coreGsonBuilder, -1));
		}
		return coreRemoteBrokerClient;
	}

	public void startSpawnRequestConsumer(Consumer<SpawnRequest> handler) {
		getCoreRemoteBrokerClient().consume(getSpawnChannel(env.node), request -> {
			Main.LOGGER.debug("" + request);
			handler.accept(request);
		});
	}

	/*
	 * public void startSpawnAckConsumer(Consumer<Ack> handler) {
	 * getSpawnRequestBrokerClient().consume(env.node + SPAWN_ACK_SUFFIX,
	 * request -> { Main.LOGGER.debug("" + request); handler.accept(request);
	 * }); }
	 */

	public void sendSpawnRequest(String targetNode, SpawnRequest request) {
		getCoreRemoteBrokerClient().produce(getSpawnChannel(targetNode), request);
	}

	/*
	 * public void sendSpawnAck(SpawnRequest request) {
	 * getSpawnRequestBrokerClient().produce(request.sourceNode +
	 * SPAWN_ACK_SUFFIX, new Ack(request)); }
	 */

	public void startFetchRequestConsumer(Consumer<FetchRequest> handler) {
		getCoreRemoteBrokerClient().consume(getFetchChannel(env.node), request -> {
			Main.LOGGER.debug("" + request);
			handler.accept(request);
		});
	}

	public void sendFetchRequest(String targetNode, FetchRequest request) {
		getCoreRemoteBrokerClient().produce(getFetchChannel(targetNode), request);
	}

	private static class AstNodeDeserializer implements JsonDeserializer<AstNode> {

		// private static final String CLASSNAME = "className";

		@Override
		public AstNode deserialize(final JsonElement jsonElement, final java.lang.reflect.Type type,
				final JsonDeserializationContext deserializationContext) throws JsonParseException {

			final JsonObject jsonObject = jsonElement.getAsJsonObject();
			final JsonPrimitive prim = (JsonPrimitive) jsonObject.get("nodeTypeId");
			final int nodeTypeId = prim.getAsInt();
			final Class<?> clazz = AstNode.getClass(nodeTypeId);
			if (clazz == null) {
				throw new JsonParseException("target class is null for nodeTypeId=" + nodeTypeId);
			}
			return deserializationContext.deserialize(jsonObject, clazz);
		}

	}

	public void sendDeployRequest(String targetNode, DeployRequest request) {
		getCoreRemoteBrokerClient().produce(getDeployChannel(targetNode), request);
	}

	public void startDeployRequestConsumer(Consumer<DeployRequest> handler) {
		getCoreRemoteBrokerClient().consume(getDeployChannel(env.node), deployRequest -> {
			Main.LOGGER.debug("" + deployRequest);
			handler.accept(deployRequest);
		});
	}

	private synchronized BrokerClient createDefaultInstance(IniEnv env) {
		Main.LOGGER.debug("creating default remote broker client");
		GsonBuilder defaultGsonBuilder = new GsonBuilder();
		defaultGsonBuilder.registerTypeAdapter(Data.class, new JsonDeserializer<RawData>() {
			@Override
			public RawData deserialize(JsonElement json, Type type, JsonDeserializationContext context)
					throws JsonParseException {
				RawData data = defaultGsonBuilder.create().fromJson(json, RawData.class).tryNumerizeKeys()
						.applyTypeInfo(defaultGsonBuilder);
				return data;
			}
		});
		return new RabbitMQBrokerClient("default-remote", env.getEnvironmentConfiguration(),
				new ChannelConfiguration(defaultGsonBuilder, 1));
	}

}
