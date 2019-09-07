package ini.test.typing;

import ini.test.IniTestCase;

public class TestArrays extends IniTestCase {

	public TestArrays(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testWrongReturnTypeInRecursiveFunction() {
		parseAndAttribCode("process main() { @init() {" + "	a[\"a\"] = 1\n" + "	a[\"b\"] = 1.2\n" + "} }", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 2 error: " + attrib.errors, 2, attrib.errors.size());
			assertEquals("wrong type of error: " + attrib.errors, "type mismatch: 'Int' is not compatible with 'Double'",
					attrib.errors.get(0).message);
			assertEquals("wrong type of error: " + attrib.errors, "type mismatch: 'Int' is not compatible with 'Double'",
					attrib.errors.get(1).message);
		});
	}

	public void testWrongParameterType() {
		parseAndAttribCode("process main() { @init() { f(1.2) } }\n" + "process f(n) { @end() { return n+1 } }", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 2 error: " + attrib.errors, 2, attrib.errors.size());
			assertEquals("wrong type of error: " + attrib.errors, "type mismatch: 'Double' is not compatible with 'Int'",
					attrib.errors.get(0).message);
			assertEquals("wrong type of error: " + attrib.errors, "type mismatch: 'Double' is not compatible with 'Int'",
					attrib.errors.get(1).message);
		});
	}

	public void testPolymorphicFunction() {
		parseAndAttribCode("process main() { @init() { println(f([1,3]))\n println(f(\"13\")) } }\n"
				+ "function f(l) { swap(l[0],l[1])\n return l }", parser -> {

					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 0 error: " + attrib.errors, 0, attrib.errors.size());
				});
	}

	public void testWrongPolymorphicFunctionInvocation() {
		parseAndAttribCode("process main() { @init() {\n println(f([1,3]))\n println(f(\"13\"))\n println(f(1.2))\n } }\n"
				+ "process f(l) { @end() {\n swap(l[0],l[1])\n return l\n } }", parser -> {

					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 3 error: " + attrib.errors, 3, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors, "type mismatch",
							attrib.errors.get(0).message.substring(0, 13));
					assertEquals("wrong type of error: " + attrib.errors, "type mismatch",
							attrib.errors.get(1).message.substring(0, 13));
					assertEquals("wrong type of error: " + attrib.errors, "type mismatch",
							attrib.errors.get(2).message.substring(0, 13));
				});
	}

}
