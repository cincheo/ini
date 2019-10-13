package ini.ast;

import ini.eval.function.BoundExecutable;
import ini.type.Type;

import java.io.PrintStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

public interface AstNode {

	int NODE = 0;
	int ARRAY_ACCESS = NODE + 1;
	int ASSIGNMENT = ARRAY_ACCESS + 1;
	int AT_BINDING = ASSIGNMENT + 1;
	int AT_PREDICATE = AT_BINDING + 1;
	int BINARY_OPERATOR = AT_PREDICATE + 1;
	int BINDING = BINARY_OPERATOR + 1;
	int BOOLEAN_LITERAL = BINDING + 1;
	int BOUND_EXECUTABLE = BOOLEAN_LITERAL + 1;
	int CASE_STATEMENT = BOUND_EXECUTABLE + 1;
	int CHANNEL = CASE_STATEMENT + 1;
	int CHAR_LITERAL = CHANNEL + 1;
	int CONSTRUCTOR = CHAR_LITERAL + 1;
	int CONSTRUCTOR_MATCH_EXPRESSION = CONSTRUCTOR + 1;
	int FIELD_ACCESS = CONSTRUCTOR_MATCH_EXPRESSION + 1;
	int FUNCTION = FIELD_ACCESS + 1;
	int IMPORT = FUNCTION + 1;
	int INVOCATION = IMPORT + 1;
	int LIST_EXPRESSION = INVOCATION + 1;
	int NUMBER_LITERAL = LIST_EXPRESSION + 1;
	int PARAMETER = NUMBER_LITERAL + 1;
	int PREDICATE = PARAMETER + 1;
	int PROCESS = PREDICATE + 1;
	int RETURN_STATEMENT = PROCESS + 1;
	int RULE = RETURN_STATEMENT + 1;
	int SET_CONSTRUCTOR = RULE + 1;
	int SET_DECLARATION = SET_CONSTRUCTOR + 1;
	int SET_EXPRESSION = SET_DECLARATION + 1;
	int STRING_LITERAL = SET_EXPRESSION + 1;
	int SUB_ARRAY_ACCESS = STRING_LITERAL + 1;
	int THIS_LITERAL = SUB_ARRAY_ACCESS + 1;
	int TYPE_VARIABLE = THIS_LITERAL + 1;
	int UNARY_OPERATOR = TYPE_VARIABLE + 1;
	int USER_TYPE = UNARY_OPERATOR + 1;
	int VARIABLE = USER_TYPE + 1;

	void accept(Visitor visitor);
	
	void prettyPrint(PrintStream out);

	int nodeTypeId();

	Token token();

	Type getType();

	void setType(Type type);
	
	String getAnnotationValue(String... keys);
	
	AstNode getAnnotationNode(String... keys);

	public static Class<?> getClass(int nodeTypeId) {
		switch (nodeTypeId) {
		case AstNode.ARRAY_ACCESS:
			return ArrayAccess.class;
		case AstNode.ASSIGNMENT:
			return Assignment.class;
		case AstNode.AT_BINDING:
			return AtBinding.class;
		case AstNode.AT_PREDICATE:
			return AtPredicate.class;
		case AstNode.BINARY_OPERATOR:
			return BinaryOperator.class;
		case AstNode.BINDING:
			return Binding.class;
		case AstNode.BOOLEAN_LITERAL:
			return BooleanLiteral.class;
		case AstNode.BOUND_EXECUTABLE:
			return BoundExecutable.class;
		case AstNode.CASE_STATEMENT:
			return CaseStatement.class;
		case AstNode.CHANNEL:
			return Channel.class;
		case AstNode.CHAR_LITERAL:
			return CharLiteral.class;
		case AstNode.CONSTRUCTOR:
			return Constructor.class;
		case AstNode.CONSTRUCTOR_MATCH_EXPRESSION:
			return ConstructorMatchExpression.class;
		case AstNode.FIELD_ACCESS:
			return FieldAccess.class;
		case AstNode.FUNCTION:
			return Function.class;
		case AstNode.IMPORT:
			return Import.class;
		case AstNode.INVOCATION:
			return Invocation.class;
		case AstNode.LIST_EXPRESSION:
			return ListExpression.class;
		case AstNode.NUMBER_LITERAL:
			return NumberLiteral.class;
		case AstNode.PARAMETER:
			return Parameter.class;
		case AstNode.PROCESS:
			return Process.class;
		case AstNode.RETURN_STATEMENT:
			return ReturnStatement.class;
		case AstNode.RULE:
			return Rule.class;
		case AstNode.SET_CONSTRUCTOR:
			return SetConstructor.class;
		case AstNode.SET_DECLARATION:
			return SetDeclaration.class;
		case AstNode.SET_EXPRESSION:
			return SetExpression.class;
		case AstNode.STRING_LITERAL:
			return StringLiteral.class;
		case AstNode.SUB_ARRAY_ACCESS:
			return SubArrayAccess.class;
		case AstNode.THIS_LITERAL:
			return ThisLiteral.class;
		case AstNode.TYPE_VARIABLE:
			return TypeVariable.class;
		case AstNode.UNARY_OPERATOR:
			return UnaryOperator.class;
		case AstNode.USER_TYPE:
			return UserType.class;
		case AstNode.VARIABLE:
			return Variable.class;
		default:
			return null;
		}
	}
	
	public static String toJson(AstNode node) {
		return new Gson().toJson(node);
	}
	
	public static AstNode fromJson(String json, Class<? extends AstNode> nodeType) {
		return new GsonBuilder().registerTypeAdapter(AstNode.class, new AstNodeDeserializer())
				.registerTypeAdapter(VariableAccess.class, new AstNodeDeserializer())
				.registerTypeAdapter(Expression.class, new AstNodeDeserializer())
				.registerTypeAdapter(Statement.class, new AstNodeDeserializer()).create()
				.fromJson(json, nodeType);
	}
}

class AstNodeDeserializer implements JsonDeserializer<AstNode> {

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

