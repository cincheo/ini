package ini.test.eval;

import ini.test.IniTestCase;

public class TestExecutables extends IniTestCase {

	public TestExecutables(String name) {
		super(name);
	}

	public void testLegalOverload2() {
		testFile("ini/test/executables/legalOverload2.ini", (p, out) -> assertEquals("1:test1\n2:test22\n", out));
	}

	public void testWrongOverload() {
		parseAndAttribFile("ini/test/executables/wrongOverload.ini", //
				parser -> {
					assertFalse(parser.hasErrors());
				}, //
				attrib -> {
					assertTrue(attrib.hasErrors());
					assertEquals("ambiguous overload(s) [f(s)]", attrib.errors.get(0).message);
				});
	}

	public void testOverride() {
		testFile("ini/test/executables/override.ini", (p, out) -> assertEquals("prefix2\n", out));
	}

	public void testLegalOverload1() {
		testFile("ini/test/executables/legalOverload1.ini", (p, out) -> assertEquals("prefix1\nprefix2\n", out));
	}

	public void testExplicitExtension() {
		testFile("ini/test/executables/explicit_extension.ini", (p, out) -> assertEquals("prefix_suffix\n3\n", out));
	}

}
