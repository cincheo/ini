package ini.test.eval;

public class TestEvents extends IniTestCase {

	public TestEvents(String name) {
		super(name);
	}

	public void testTimer() {
		testFile("ini/test/events/timer.ini",
				(p, out) -> assertEquals(
						"1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n",
						out));
	}

}
