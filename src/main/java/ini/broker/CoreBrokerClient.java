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

import ini.Main;
import ini.ast.AstNode;
import ini.ast.Expression;
import ini.ast.NumberLiteral;
import ini.ast.Statement;
import ini.ast.VariableAccess;
import ini.eval.data.Data;
import ini.eval.data.RawData;

public class CoreBrokerClient {

	private static final boolean VERBOSE = false;

	public static final String SPAWN_REQUEST_SUFFIX = "_spawn_request";
	public static final String FETCH_REQUEST_SUFFIX = "_fetch_request";
	public static final String DEPLOY_REQUEST_SUFFIX = "_deploy_request";

	private static BrokerClient<SpawnRequest> spawnRequestBrokerClient;
	private static BrokerClient<FetchRequest> fetchRequestBrokerClient;
	private static BrokerClient<DeployRequest> deployRequestBrokerClient;

	private static synchronized BrokerClient<SpawnRequest> getSpawnRequestBrokerClient() {
		if (spawnRequestBrokerClient == null) {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Data.class, new JsonDeserializer<RawData>() {
				@Override
				public RawData deserialize(JsonElement json, Type type, JsonDeserializationContext context)
						throws JsonParseException {
					return gsonBuilder.create().fromJson(json, RawData.class).tryNumerizeKeys().applyTypeInfo();
				}
			});
			spawnRequestBrokerClient = new KafkaBrokerClient<>(VERBOSE,
					new ConsumerConfiguration<>(
							Main.getEnvironmentConfiguration().coreConsumerGroupId, gsonBuilder,
							SpawnRequest.class));
		}
		return spawnRequestBrokerClient;
	}

	private static synchronized BrokerClient<DeployRequest> getDeployRequestBrokerClient() {
		if (deployRequestBrokerClient == null) {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(AstNode.class, new AstNodeDeserializer())
					.registerTypeAdapter(VariableAccess.class, new AstNodeDeserializer())
					.registerTypeAdapter(Expression.class, new AstNodeDeserializer())
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

			deployRequestBrokerClient = new KafkaBrokerClient<>(VERBOSE, new ConsumerConfiguration<>(
					Main.getEnvironmentConfiguration().coreConsumerGroupId, gsonBuilder, DeployRequest.class));
		}
		return deployRequestBrokerClient;
	}

	private static synchronized BrokerClient<FetchRequest> getFetchRequestBrokerClient() {
		if (fetchRequestBrokerClient == null) {
			GsonBuilder gsonBuilder = new GsonBuilder();
			fetchRequestBrokerClient = new KafkaBrokerClient<>(VERBOSE, new ConsumerConfiguration<>(
					Main.getEnvironmentConfiguration().coreConsumerGroupId, gsonBuilder, FetchRequest.class));
		}
		return fetchRequestBrokerClient;
	}

	public static void startSpawnRequestConsumer(Consumer<SpawnRequest> handler) {
		new Thread() {
			public void run() {
				getSpawnRequestBrokerClient().consume(Main.node + SPAWN_REQUEST_SUFFIX, request -> {
					Main.LOGGER.info("" + request);
					handler.accept(request);
				});
			}
		}.start();
	}

	public static void sendSpawnRequest(String targetNode, SpawnRequest request) {
		getSpawnRequestBrokerClient().produce(targetNode + SPAWN_REQUEST_SUFFIX, request);
	}

	public static void startFetchRequestConsumer(Consumer<FetchRequest> handler) {
		new Thread() {
			public void run() {
				getFetchRequestBrokerClient().consume(Main.node + FETCH_REQUEST_SUFFIX, request -> {
					Main.LOGGER.info("" + request);
					handler.accept(request);
				});
			}
		}.start();
	}

	public static void sendFetchRequest(String targetNode, FetchRequest request) {
		Main.LOGGER.info("send " + request + " to " + targetNode);
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
			return deserializationContext.deserialize(jsonObject, clazz);
		}

	}

	public static void sendDeployRequest(String targetNode, DeployRequest request) {
		getDeployRequestBrokerClient().produce(targetNode + DEPLOY_REQUEST_SUFFIX, request);
	}

	public static void startDeployRequestConsumer(Consumer<DeployRequest> handler) {
		new Thread() {
			public void run() {
				getDeployRequestBrokerClient().consume(Main.node + DEPLOY_REQUEST_SUFFIX, deployRequest -> {
					Main.LOGGER.info("" + deployRequest);
					handler.accept(deployRequest);
				});
			}
		}.start();
	}
	
}
