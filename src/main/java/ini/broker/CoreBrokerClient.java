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

	private static final boolean VERBOSE = false;

	public static final String SPAWN_REQUEST_SUFFIX = "_spawn_request";
	public static final String FETCH_REQUEST_SUFFIX = "_fetch_request";
	public static final String DEPLOY_REQUEST_SUFFIX = "_deploy_request";

	private BrokerClient<SpawnRequest> spawnRequestBrokerClient;
	private BrokerClient<FetchRequest> fetchRequestBrokerClient;
	private BrokerClient<DeployRequest> deployRequestBrokerClient;

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

	private synchronized BrokerClient<SpawnRequest> getSpawnRequestBrokerClient() {
		if (spawnRequestBrokerClient == null) {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Data.class, new JsonDeserializer<RawData>() {
				@Override
				public RawData deserialize(JsonElement json, Type type, JsonDeserializationContext context)
						throws JsonParseException {
					return gsonBuilder.create().fromJson(json, RawData.class).tryNumerizeKeys().applyTypeInfo();
				}
			});
			spawnRequestBrokerClient = new KafkaBrokerClient<>(VERBOSE, env.getEnvironmentConfiguration(),
					new ConsumerConfiguration<>(env.getEnvironmentConfiguration().coreConsumerGroupId, gsonBuilder,
							SpawnRequest.class));
		}
		return spawnRequestBrokerClient;
	}

	private synchronized BrokerClient<DeployRequest> getDeployRequestBrokerClient() {
		if (deployRequestBrokerClient == null) {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(AstNode.class, new AstNodeDeserializer())
					.registerTypeAdapter(VariableAccess.class, new AstNodeDeserializer())
					.registerTypeAdapter(Expression.class, new AstNodeDeserializer())
					.registerTypeAdapter(Executable.class, new AstNodeDeserializer())
					.registerTypeAdapter(Statement.class, new AstNodeDeserializer());

			gsonBuilder.registerTypeAdapter(Data.class, new JsonDeserializer<RawData>() {
				@Override
				public RawData deserialize(JsonElement json, Type type, JsonDeserializationContext context)
						throws JsonParseException {
					return gsonBuilder.create().fromJson(json, RawData.class).tryNumerizeKeys().applyTypeInfo();
				}
			});

			gsonBuilder.registerTypeAdapter(NumberLiteral.class, new JsonDeserializer<NumberLiteral>() {

				@Override
				public NumberLiteral deserialize(JsonElement json, Type type, JsonDeserializationContext context)
						throws JsonParseException {
					// context.deserialize(json, type);
					return new Gson().fromJson(json, NumberLiteral.class).applyTypeInfo();
				}
			});

			deployRequestBrokerClient = new KafkaBrokerClient<>(VERBOSE, env.getEnvironmentConfiguration(),
					new ConsumerConfiguration<>(env.getEnvironmentConfiguration().coreConsumerGroupId, gsonBuilder,
							DeployRequest.class));
		}
		return deployRequestBrokerClient;
	}

	private synchronized BrokerClient<FetchRequest> getFetchRequestBrokerClient() {
		if (fetchRequestBrokerClient == null) {
			GsonBuilder gsonBuilder = new GsonBuilder();
			fetchRequestBrokerClient = new KafkaBrokerClient<>(VERBOSE, env.getEnvironmentConfiguration(),
					new ConsumerConfiguration<>(env.getEnvironmentConfiguration().coreConsumerGroupId, gsonBuilder,
							FetchRequest.class));
		}
		return fetchRequestBrokerClient;
	}

	public void startSpawnRequestConsumer(Consumer<SpawnRequest> handler) {
		new Thread() {
			public void run() {
				getSpawnRequestBrokerClient().consume(env.node + SPAWN_REQUEST_SUFFIX, request -> {
					Main.LOGGER.debug("" + request);
					handler.accept(request);
				});
			}
		}.start();
	}

	public void sendSpawnRequest(String targetNode, SpawnRequest request) {
		getSpawnRequestBrokerClient().produce(targetNode + SPAWN_REQUEST_SUFFIX, request);
	}

	public void startFetchRequestConsumer(Consumer<FetchRequest> handler) {
		new Thread() {
			public void run() {
				getFetchRequestBrokerClient().consume(env.node + FETCH_REQUEST_SUFFIX, request -> {
					Main.LOGGER.debug("" + request);
					handler.accept(request);
				});
			}
		}.start();
	}

	public void sendFetchRequest(String targetNode, FetchRequest request) {
		Main.LOGGER.debug("send " + request + " to " + targetNode);
		getFetchRequestBrokerClient().produce(targetNode + FETCH_REQUEST_SUFFIX, request);
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
		getDeployRequestBrokerClient().produce(targetNode + DEPLOY_REQUEST_SUFFIX, request);
	}

	public void startDeployRequestConsumer(Consumer<DeployRequest> handler) {
		new Thread() {
			public void run() {
				getDeployRequestBrokerClient().consume(env.node + DEPLOY_REQUEST_SUFFIX, deployRequest -> {
					Main.LOGGER.debug("" + deployRequest);
					handler.accept(deployRequest);
				});
			}
		}.start();
	}

}
