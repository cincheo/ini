package testl.parser.ast;

public class Num implements Expression {

	public Double value;

	public Num(Double value) {
		System.out.println("==> num created : "+value);
		this.value = value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
