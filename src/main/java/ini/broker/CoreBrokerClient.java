package ini.broker;

import java.lang.reflect.Type;
import java.util.function.Consumer;

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
import ini.ast.Statement;
import ini.ast.VariableAccess;
import ini.broker.ConsumerConfiguration.ConsumeStrategy;
import ini.eval.data.Data;
import ini.eval.data.RawData;

public class CoreBrokerClient {

	private static final boolean VERBOSE = false;

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
					RawData data = gsonBuilder.create().fromJson(json, RawData.class).tryNumerizeKeys();
					if (data.isNumber()) {
						if (!json.getAsJsonObject().get("value").toString().contains(".")) {
							data.setValue(json.getAsJsonObject().get("value").getAsLong());
						}
					}
					return data;
				}
			});
			spawnRequestBrokerClient = new KafkaBrokerClient<>(VERBOSE,
					new ConsumerConfiguration<>(ConsumeStrategy.LATEST, 1000, gsonBuilder, SpawnRequest.class));
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
					RawData data = gsonBuilder.create().fromJson(json, RawData.class).tryNumerizeKeys();
					if (data.isNumber()) {
						if (!json.getAsJsonObject().get("value").toString().contains(".")) {
							data.setValue(json.getAsJsonObject().get("value").getAsLong());
						}
					}
					return data;
				}
			});

			deployRequestBrokerClient = new KafkaBrokerClient<>(VERBOSE,
					new ConsumerConfiguration<>(ConsumeStrategy.LATEST, 1000, gsonBuilder, DeployRequest.class));
		}
		return deployRequestBrokerClient;
	}

	private static synchronized BrokerClient<FetchRequest> getFetchRequestBrokerClient() {
		if (fetchRequestBrokerClient == null) {
			GsonBuilder gsonBuilder = new GsonBuilder();
			fetchRequestBrokerClient = new KafkaBrokerClient<>(VERBOSE,
					new ConsumerConfiguration<>(ConsumeStrategy.LATEST, 1000, gsonBuilder, FetchRequest.class));
		}
		return fetchRequestBrokerClient;
	}

	public static void startSpawnRequestConsumer(Consumer<SpawnRequest> handler) {
		new Thread() {
			public void run() {
				while (true) {
					try {
						getSpawnRequestBrokerClient().consume(Main.node + "_spawn_request", request -> {
							Main.log("" + request);
							handler.accept(request);
						});
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();
	}

	public static void sendSpawnRequest(String targetNode, SpawnRequest request) {
		getSpawnRequestBrokerClient().produce(targetNode + "_spawn_request", request);
	}

	public static void startFetchRequestConsumer(Consumer<FetchRequest> handler) {
		new Thread() {
			public void run() {
				while (true) {
					try {
						getFetchRequestBrokerClient().consume(Main.node + "_fetch_request", request -> {
							Main.log("" + request);
							handler.accept(request);
						});
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();
	}

	public static void sendFetchRequest(String targetNode, FetchRequest request, Consumer<DeployRequest> handler) {
		Main.log("send " + request + " to " + targetNode);
		getFetchRequestBrokerClient().produce(targetNode + "_fetch_request", request);
		boolean[] handled = { false };
		while (!handled[0]) {
			try {
				getDeployRequestBrokerClient().consume(Main.node + "_deploy_request", deployRequest -> {
					Main.log("" + deployRequest);
					handled[0] = true;
					handler.accept(deployRequest);
				});
			} catch (InterruptedException e) {
			}
		}
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
		getDeployRequestBrokerClient().produce(targetNode + "_deploy_request", request);
	}

}
