package ini.test.eval;

import ini.test.IniTestCase;

public class TestProcesses extends IniTestCase {

	public TestProcesses(String name) {
		super(name);
	}

	public void testWait() {
		testFile("ini/test/processes/wait.ini", (p, out) -> assertEquals("1\n2\n3\n4\n5\n6\n6\n", out));
	}

	public void testWaitIndirect() {
		testFile("ini/test/processes/wait_indirect.ini", (p, out) -> assertEquals("1\n2\n3\n4\n3\n5\n", out));
	}
	
}
