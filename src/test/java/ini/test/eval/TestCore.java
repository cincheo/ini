package ini.test.eval;

import ini.test.IniTestCase;

public class TestCore extends IniTestCase {

	public TestCore(String name) {
		super(name);
	}

	public void testOperators() {
		testFile("ini/test/core/operators.ini",
				(p, out) -> assertEquals("F\n[C,D,F]\n[C,D,F]\ntrue\nfalse\nfalse\ntrue\ntrue\nfalse\nfalse\nA\ntrue\nfalse\ntrue\n", out));
	}

	public void testMatches() {
		testFile("ini/test/core/matches.ini",
				(p, out) -> assertEquals("producing...\n8.0\nno match\nno match\nlist:[a,b]\n", out));
	}

	public void testWrongMatches() {
		parseAndAttribFile("ini/test/core/wrong_matches1.ini", 
				p -> {
					
				}, a -> {
					assertEquals(2, a.errors.size());
					assertEquals("undeclared type 'Strgin'", a.errors.get(0).message);
					assertEquals("undeclared type 'Strgin*'", a.errors.get(1).message);
				});
	}
	
}
