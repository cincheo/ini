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

	public void testUpdate() {
		testFile("ini/test/events/update.ini",
				(p, out) -> assertEquals(
						"1->2\n2->3\n3->4\nend\n",
						out));
	}

	public void testUpdateAsync() {
		testFile("ini/test/events/update_async.ini", 50, 
				(p, out) -> assertEquals(
						"end\n***",
						out));
	}
	
}
