package ini.test.eval;

public class TestBroker extends IniTestCase {

	public TestBroker(String name) {
		super(name);
		System.err.println("WARINING: this test case will take a while");
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

	public void testDeploymentClient() {
		new Thread() {
			@Override
			public void run() {
				testFile("ini/test/broker/broker_server.ini", 10000, "main", null);
			}
		}.start();
		testFile("ini/test/broker/broker_client.ini", 10000, "client", (p, out) -> {
			assertEquals("%test%\n%test2%\n%test3%\n", out);
		});
	}
	
}
