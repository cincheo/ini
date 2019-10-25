package ini.ast;

import java.io.PrintStream;

import ini.Main;
import ini.eval.data.TypeInfo;
import ini.parser.IniParser;

public class NumberLiteral extends AstElement implements Expression {

	public Number value;
	public int typeInfo;
	
	public NumberLiteral(IniParser parser, Token token, Number value) {
		super(parser, token);
		this.value=value;
		this.typeInfo = TypeInfo.getTypeInfoForInstance(value);
		if(value instanceof Byte) {
			this.type = parser.types.BYTE;
		} if(value instanceof Integer) {
			this.type = parser.types.INT;
		} else if(value instanceof Float) {
			this.type = parser.types.FLOAT;
		} else if(value instanceof Double) {
			this.type = parser.types.DOUBLE;
		}
		this.nodeTypeId=AstNode.NUMBER_LITERAL;
	}

	@Override
	public void prettyPrint(PrintStream out) {
		out.print(value);
	}
	
	public NumberLiteral applyTypeInfo() {
		if (value == null || typeInfo == 0) {
			return this;
		}
		switch (typeInfo) {
		case TypeInfo.INTEGER:
			value = ((Number) value).intValue();
			break;
		case TypeInfo.LONG:
			value = ((Number) value).longValue();
			break;
		case TypeInfo.DOUBLE:
			value = ((Number) value).doubleValue();
			break;
		case TypeInfo.FLOAT:
			value = ((Number) value).floatValue();
			break;
		case TypeInfo.STRING:
			break;
		case TypeInfo.BOOLEAN:
			break;
		default:
			Main.LOGGER.error("NO CONVERSION: " + typeInfo + " / " + value + " / " + value.getClass(), new Exception());
		}
		return this;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitNumberLiteral(this);
	}
	
}
