package ini.test.eval;

import java.io.ByteArrayOutputStream;

import ini.test.IniTestCase;

public class TestBroker extends IniTestCase {

	public TestBroker(String name) {
		super(name);
	}

	public void testCoordinator() {
		// OK
		if(skipTestsUsingBroker) {
			return;
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		new Thread() {
			@Override
			public void run() {
				testFile(null, 500, "n1", os, (p, out) -> {
					p.env.coreBrokerClient.stop();
				});
			}
		}.start();
		testFile("ini/test/broker/coordinator.ini", 500, "main", os, (p, out) -> {
			p.env.coreBrokerClient.stop();
		});
		assertEquals("yeah\n%yeah%%yeah%\n1\nprocesses started\n", os.toString());
		
	}

	public void testDeploymentClient() {
		// OK
		if(skipTestsUsingBroker) {
			return;
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		new Thread() {
			@Override
			public void run() {
				testFile("ini/test/broker/broker_server.ini", "main", os, (p, out) -> {
					p.env.coreBrokerClient.stop();
				});
			}
		}.start();
		testFile("ini/test/broker/broker_client.ini", "client", os, (p, out) -> {
			p.env.coreBrokerClient.stop();
		});
		assertEquals("%test%\n%test2%\n%test3%\n", os.toString());
	}

	public void testRemoteBinding() {
		// OK
		if(skipTestsUsingBroker) {
			return;
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		new Thread() {
			@Override
			public void run() {
				testFile(null, 100, "n1", os, (p, out) -> {
					p.env.coreBrokerClient.stop();
				});
			}
		}.start();
		testFile("ini/test/broker/remote_binding.ini", 100, "main", os, (p, out) -> {
			p.env.coreBrokerClient.stop();
		});
		assertEquals("2.0\nstopped\n", os.toString());
	}

	public void testRemoteFunction() {
		// OK
		if(skipTestsUsingBroker) {
			return;
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		new Thread() {
			@Override
			public void run() {
				testFile(null, 500, "n1", os, (p, out) -> {
					p.env.coreBrokerClient.stop();
				});
			}
		}.start();
		testFile("ini/test/broker/remote_function.ini", 500, "main", os, (p, out) -> {
			p.env.coreBrokerClient.stop();
		});
		assertEquals("hello remote 2\n", os.toString());
	}

	public void testChannels() {
		// OK
		if(skipTestsUsingBroker) {
			return;
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		new Thread() {
			@Override
			public void run() {
				testFile(null, 500, "n1", os, (p, out) -> {
					p.env.coreBrokerClient.stop();
				});
			}
		}.start();
		testFile("ini/test/broker/channels.ini", 500, "main", os, (p, out) -> {
			p.env.coreBrokerClient.stop();
		});
		assertEquals("consumed from chan: hello1\nconsumed from g: hello2\n", os.toString());
	}

	public void testRemoteLambda() {
		// OK
		if(skipTestsUsingBroker) {
			return;
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		new Thread() {
			@Override
			public void run() {
				testFile(null, 200, "n1", os, (p, out) -> {
					p.env.coreBrokerClient.stop();
				});
			}
		}.start();
		testFile("ini/test/broker/remote_lambda.ini", 200, "main", os, (p, out) -> {
			p.env.coreBrokerClient.stop();
		});
		assertEquals("hello remote 1\n", os.toString());
	}
	
}
