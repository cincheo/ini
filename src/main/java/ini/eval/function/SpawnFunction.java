package ini.eval.function;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import ini.ast.AstNode;
import ini.ast.Expression;
import ini.ast.Function;
import ini.ast.Invocation;
import ini.ast.Statement;
import ini.ast.VariableAccess;
import ini.eval.IniEval;
import ini.eval.data.Data;
import ini.parser.IniParser;
import ini.type.Type;
import ini.type.TypingConstraint;

public class SpawnFunction extends IniFunction {

	public class AstNodeDeserializer implements JsonDeserializer<AstNode> {

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

	@Override
	public Data eval(final IniEval eval, final List<Expression> params) {
		String function = eval.eval(params.get(0)).getValue();
		List<Expression> args = params.subList(1, params.size());
		System.out.println("SERIALIZED0: " + new Gson().toJson(args.get(0)));
		System.out.println("SERIALIZED1: " + new Gson().toJson(args.get(1)));
		Function f = eval.parser.parsedFunctionMap.get(function);
		String json = new Gson().toJson(f);
		System.out.println("SERIALIZED: " + json);
		Function fd = new GsonBuilder().registerTypeAdapter(AstNode.class, new AstNodeDeserializer())
				.registerTypeAdapter(VariableAccess.class, new AstNodeDeserializer())
				.registerTypeAdapter(Expression.class, new AstNodeDeserializer())
				.registerTypeAdapter(Statement.class, new AstNodeDeserializer()).create()
				.fromJson(json, Function.class);
		eval.eval(f);
		// Invocation i = new
		// Invocation(null,eval.evaluationStack.peek().token(),function,args);
		return null;
	}

	@Override
	public Type getType(IniParser parser, List<TypingConstraint> constraints, Invocation invocation) {
		return parser.ast.ANY;
	}

}
