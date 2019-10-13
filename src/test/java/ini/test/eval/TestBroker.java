package ini.test.eval;

import ini.test.IniTestCase;

public class TestBroker extends IniTestCase {

	public TestBroker(String name) {
		super(name);
		System.err.println("WARINING: this test case will take a while");
	}

	public void testCoordinator() {
		if(skipTestsUsingBroker) {
			return;
		}
		new Thread() {
			@Override
			public void run() {
				testFile(null, 10000, "n1", null);
			}
		}.start();
		testFile("ini/test/broker/coordinator.ini", 10000, "main", (p, out) -> {
			assertTrue(out, "yeah\n%yeah%%yeah%\n1\nprocesses started\n".equals(out) ||
					"1\nyeah\n%yeah%%yeah%\nprocesses started\n".equals(out) ||
					"%yeah%%yeah%\nyeah\n1\nprocesses started\n".equals(out) ||
					"%yeah%%yeah%\n1\nyeah\nprocesses started\n".equals(out));
		});
	}

	public void testDeploymentClient() {
		if(skipTestsUsingBroker) {
			return;
		}
		StringBuffer result = new StringBuffer();
		new Thread() {
			@Override
			public void run() {
				testFile("ini/test/broker/broker_server.ini", "main", null);
			}
		}.start();
		testFile("ini/test/broker/broker_client.ini", "client", (p, out) -> {
			result.append(out);
		});
		assertEquals("%test%\n%test2%\n%test3%\n", result.toString());
	}

	public void testRemoteBinding() {
		if(skipTestsUsingBroker) {
			return;
		}
		StringBuffer result = new StringBuffer();
		new Thread() {
			@Override
			public void run() {
				testFile(null, 3000, "n1", null);
			}
		}.start();
		testFile("ini/test/broker/remote_binding.ini", "main", (p, out) -> {
			result.append(out);
		});
		assertEquals("2.0\nstopped\n", result.toString());
	}
	
}
