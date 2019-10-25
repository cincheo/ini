package ini.test.eval;

import ini.test.IniTestCase;

public class TestUserTypes extends IniTestCase {

	public TestUserTypes(String name) {
		super(name);
	}

	public void testBasicEnum() {
		testFile("ini/test/user_types/basic_enum.ini", (p, out) -> assertEquals("OK\nOK\nOK\nOK\n", out));
	}

}
