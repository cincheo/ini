package ini.test.eval;

public class TestBroker extends IniTestCase {

	public TestBroker(String name) {
		super(name);
	}

	public void testCoordinator() {
		new Thread() {
			@Override
			public void run() {
				testFile(null, 10000, "n1", null);
			}
		}.start();
		testFile("ini/test/broker/coordinator.ini", 10000, "main", (p, out) -> {
			assertEquals("yeah\n%yeah%%yeah%\n1\nprocesses started\n", out);
		});
	}

}
