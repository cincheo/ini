package ini.test.typing;

import ini.test.IniTestCase;

public class TestTypingRules extends IniTestCase {

	public TestTypingRules(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testStringByStringDivisionNotAllowed() {
		parseAndAttribCode("process main() {" //
				+ "	@init() {" //
				+ "		println(f(\"a\",\"b\"))" //
				+ "	}" //
				+ "}\n"
				+ "function f(i,j) {" //
				+ "	case 0.0 == i/j {" //
				+ "		return 0" //
				+ "	}" //
				+ "}", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 2 error: " + attrib.errors, 2, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'String' is not compatible with 'Double'", attrib.errors.get(0).message);
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'String' is not compatible with 'Double'", attrib.errors.get(1).message);
				});
	}

	public void testStringByStringMultNotAllowed() {
		parseAndAttribCode("process main() {" //
				+ "	@init() {" //
				+ "		println(\"a\"*\"b\")" //
				+ "	}" //
				+ "}",
				parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 1 error: " + attrib.errors, 1, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'String' is not compatible with 'Double'", attrib.errors.get(0).message);
				});
	}

	public void testUnaryOpOnlyWorksOnNumbers() {
		parseAndAttribCode(
				"function main() {" //
				+ "	println(f(\"a\"))" //
				+ "}\n" //
				+ "function f(i) {" //
				+ "	return -i" //
				+ "}",
				parser -> {

					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 2 error: " + attrib.errors, 2, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'String' is not compatible with 'Double'", attrib.errors.get(0).message);
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'String' is not compatible with 'Double'", attrib.errors.get(1).message);
				});
	}

}
