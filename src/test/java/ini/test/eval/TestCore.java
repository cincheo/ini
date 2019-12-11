package ini.test.eval;

import ini.test.IniTestCase;

public class TestCore extends IniTestCase {

	public TestCore(String name) {
		super(name);
	}

	public void testOperators() {
		testFile("ini/test/core/operators.ini",
				(p, out) -> assertEquals("F\n[C,D,F]\n[C,D,F]\ntrue\nfalse\nfalse\ntrue\ntrue\nfalse\nfalse\nA\n", out));
	}

	public void testMatches() {
		testFile("ini/test/core/matches.ini",
				(p, out) -> assertEquals("producing...\n8.0\nno match\nno match\n", out));
	}
	
}
