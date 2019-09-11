package ini.test.eval;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import ini.test.IniTestCase;

public class TestExamples extends IniTestCase {

	public TestExamples(String name) {
		super(name);
	}

	public void testAlgebraicExpressions() {
		testFile("ini/examples/algebraic_expressions.ini",
				(p, out) -> assertEquals("The value of -(((3.0*2.0)+1.0)) is -7.0\n", out));
	}

	public void testLCSS() {
		testFile("ini/examples/lcss.ini", (p, out) -> assertEquals("2.0\n", out));
	}

	public void testComparator() {
		testFile("ini/examples/comparator.ini",
				(p, out) -> assertEquals("result:    aginoorrssttt\nresult:    aginoorrssttt\n", out));
	}

	public void testLambdas() {
		testFile("ini/examples/lambdas.ini", (p, out) -> assertEquals("234567246", out));
	}

	public void testCountOccurences() {
		testFile("ini/examples/count_occurences.ini",
				(p, out) -> assertEquals(
						"Counting 'This is the string we will count'\nNumber of e(s): 2\nNumber of a(s): null\nNumber of s(s): 3\nNumber of i(s): 4\nNumber of spaces: 6\nCounting '[1,2,1,7](0..3)'\nNumber of 1: 2\nNumber of 7: 1\nNumber of 3: null\n",
						out));
	}

	public void testFac() {
		testFile("ini/examples/fac.ini", (p, out) -> assertEquals("fac1(10)=3628800\nfac2(10)=3628800\n", out));
	}

	public void testPrettyPrinter() {
		testFile("ini/examples/pretty_printer.ini",
				(p, out) -> assertEquals(
						"<html>\n    <head>\n    </head>\n    <body bgcolor=\"white\" width=\"100%\">\nthis is a body text example \n        <b>\nthis is a strong text\n        </b>\n    </body>\n</html>\n{name:\"html\",content:[{name:\"head\"},{name:\"body\",attr:[{name:\"bgcolor\",value:\"white\"},{name:\"width\",value:\"100%\"}],content:[{name:\"false\",text:\"this is a body text example \"},{name:\"b\",content:[{name:\"false\",text:\"this is a strong text\"}]}]}]}\n",
						out));
	}

	public void _testProcessCommunication() {
		testFile("ini/examples/process_communication.ini",
				(p, out) -> assertEquals("processes started\nc1: 1\nc2: 2.0\nend of pipeline: 3.0\n", out));
	}

	public void testHttpServer() {
		testFile("ini/examples/unpublished/http_server.ini", null);
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			Thread.sleep(50);
			HttpGet request = new HttpGet("http://localhost:8080");
			HttpResponse response = httpclient.execute(request);
			assertEquals("hello world-wide web @/", IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
			Thread.sleep(50);
			request = new HttpGet("http://localhost:8080/stop");
			response = httpclient.execute(request);
			assertEquals("bye!", IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
			Thread.sleep(50);
			request = new HttpGet("http://localhost:8080");
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(100).setConnectTimeout(100)
					.setConnectionRequestTimeout(100).build();
			request.setConfig(requestConfig);
			try {
				httpclient.execute(request);
				fail("cannot send http request on stopped server");
			} catch (Exception e) {
				// swallow
			}
			assertEquals(
					"Server started on 8080\nlocalhost connected to server\nGET REQUEST: / - HTTP/1.1\nlocalhost connected to server\nGET REQUEST: /stop - HTTP/1.1\nClosing server\n",
					getOut());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testWebService() {
		new Thread() {
			@Override
			public void run() {
				testFile("ini/examples/unpublished/web_service.ini", null);
			}
		}.start();
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			Thread.sleep(50);
			HttpGet request = new HttpGet("http://localhost:8080/test");
			HttpResponse response = httpclient.execute(request);
			assertEquals("hello world wide web!\n<p>bye!\n",
					IOUtils.toString(response.getEntity().getContent(), "UTF-8"));
			Thread.sleep(1000);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testFibonacci() {
		testFile("ini/examples/fibonacci.ini",
				(p, out) -> assertEquals(
						"Fib(8)=34\n" + "Here is a flat Fibonacci tree for 5:\n"
								+ "[5,4,3,3,2,2,1,2,1,1,1,1,1,null,null,1,1,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null](1..32)\n"
								+ "Here is a non-flat Fibonacci tree for 5:\n"
								+ "Tree1[left=Tree1[left=Tree1[left=Tree1[left=Tree1[value=1],right=Tree1[value=1],value=2],right=Tree1[value=1],value=3],right=Tree1[left=Tree1[value=1],right=Tree1[value=1],value=2],value=4],right=Tree1[left=Tree1[left=Tree1[value=1],right=Tree1[value=1],value=2],right=Tree1[value=1],value=3],value=5]\n"
								+ "Fibtree(5).left.value = 4\n" + "Fibtree(5).right.value = 3\n"
								+ "Fibtree(5).left.left.value = 3\n" + "Fibtree(5).left.right.value = 2\n"
								+ "Fibtree(5).right.left.value = 2\n" + "Fibtree(5).right.right.value = 1\n"
								+ "Here is a non-flat Fibonacci tree for 5:\n"
								+ "Node[left=Node[left=Node[left=Node[left=Leaf[value=1],right=Leaf[value=1],value=2],right=Node[left=null,right=null,value=1],value=3],right=Node[left=Leaf[value=1],right=Leaf[value=1],value=2],value=4],right=Node[left=Node[left=Leaf[value=1],right=Leaf[value=1],value=2],right=Node[left=null,right=null,value=1],value=3],value=5]\n"
								+ "Here is a non-flat Fibonacci tree for 5:\n"
								+ "Node[left=Node[left=Node[left=Node[left=Leaf[value=1],right=Leaf[value=1],value=2],right=Node[left=null,right=null,value=1],value=3],right=Node[left=Leaf[value=1],right=Leaf[value=1],value=2],value=4],right=Node[left=Node[left=Leaf[value=1],right=Leaf[value=1],value=2],right=Node[left=null,right=null,value=1],value=3],value=5]\n"
								+ "Here is a non-flat Fibonacci tree for 5:\n"
								+ "Node[left=Node[left=Node[left=Node[left=Leaf[value=1],right=Leaf[value=1]],right=Node[left=null,right=null,value=1]],right=Node[left=Leaf[value=1],right=Leaf[value=1]]],right=Node[left=Node[left=Leaf[value=1],right=Leaf[value=1]],right=Node[left=null,right=null,value=1]]]\n"
								+ "Here is a non-flat Fibonacci tree for 5:\n"
								+ "Node[left=Node[left=Node[left=Node[left=Leaf[value=1],right=Leaf[value=1],value=2],right=Leaf[value=1],value=3],right=Node[left=Leaf[value=1],right=Leaf[value=1],value=2],value=4],right=Node[left=Node[left=Leaf[value=1],right=Leaf[value=1],value=2],right=Leaf[value=1],value=3],value=5]\n",
						out));
	}

}
