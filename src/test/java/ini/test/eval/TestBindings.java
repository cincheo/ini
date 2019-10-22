package ini.test.eval;

import ini.test.IniTestCase;

public class TestBindings extends IniTestCase {

	public TestBindings(String name) {
		super(name);
	}

	public void testOverload() {
		testFile("ini/test/bindings/overload.ini",
				(p, out) -> assertEquals("method of test1\nmethod of test2 - suffix\n", out));
	}

	public void testOverload2() {
		testFile("ini/test/bindings/overload2.ini",
				(p, out) -> assertEquals("method of test1\nmethod of test2\nmethod of test2 - suffix\n", out));
	}

	public void testIllegalOverload1() {
		parseAndAttribFile("ini/test/bindings/illegalOverload1.ini", //
				parser -> {
					assertFalse(parser.hasErrors());
				}, //
				attrib -> {
					assertTrue(attrib.hasErrors());
					assertEquals("cannot overload an existing function or variable", attrib.errors.get(0).message);
				});
	}

	public void testIllegalOverload2() {
		parseAndAttribFile("ini/test/bindings/illegalOverload2.ini", //
				parser -> {
					assertFalse(parser.hasErrors());
				}, //
				attrib -> {
					assertTrue(attrib.hasErrors());
					assertEquals("cannot overload a remote binding", attrib.errors.get(0).message);
				});
	}

	public void testIllegalOverload3() {
		parseAndAttribFile("ini/test/bindings/illegalOverload3.ini", //
				parser -> {
					assertFalse(parser.hasErrors());
				}, //
				attrib -> {
					assertTrue(attrib.hasErrors());
					assertEquals("cannot override existing binding", attrib.errors.get(0).message);
				});
	}

	public void testOverloadWrongInvocation() {
		parseAndAttribFile("ini/test/bindings/overloadWrongInvocation.ini", //
				parser -> {
					assertFalse(parser.hasErrors());
				}, //
				attrib -> {
					assertTrue(attrib.hasErrors());
					assertEquals("type mismatch: 'T1' is not compatible with 'String'", attrib.errors.get(0).message);
				});
	}
	
	public void testOverloadWrongInvocation2() {
		parseAndAttribFile("ini/test/bindings/overloadWrongInvocation2.ini", //
				parser -> {
					assertFalse(parser.hasErrors());
				}, //
				attrib -> {
					assertTrue(attrib.hasErrors());
					assertEquals("type mismatch: 'T1|T2' is not compatible with 'String'", attrib.errors.get(0).message);
				});
	}

}
