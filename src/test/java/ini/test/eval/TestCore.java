package ini.test.eval;

import ini.test.IniTestCase;

public class TestCore extends IniTestCase {

	public TestCore(String name) {
		super(name);
	}

	public void testOperators() {
		testFile("ini/test/core/operators.ini",
				(p, out) -> assertEquals("F\n[C,D,F]\n[C,D,F]\n", out));
	}

}
