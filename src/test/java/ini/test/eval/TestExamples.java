package ini.test.eval;

import java.io.ByteArrayOutputStream;

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
		testFile("ini/examples/data_structures/algebraic_expressions.ini",
				(p, out) -> assertEquals("The value of -(((3.0*2.0)+1.0)) is -7.0\n", out));
	}

	public void testLCSS() {
		testFile("ini/examples/calculus/lcss.ini", (p, out) -> assertEquals("2.0\n", out));
	}

	public void testComparator() {
		testFile("ini/examples/data_structures/comparator.ini",
				(p, out) -> assertEquals("result:    aginoorrssttt\nresult:    aginoorrssttt\n", out));
	}

	public void testLambdas() {
		testFile("ini/examples/calculus/lambdas.ini", (p, out) -> assertEquals("234567246", out));
	}

	public void testCountOccurences() {
		testFile("ini/examples/calculus/count_occurences.ini",
				(p, out) -> assertEquals(
						"Counting 'This is the string we will count'\nNumber of e(s): 2\nNumber of a(s): null\nNumber of s(s): 3\nNumber of i(s): 4\nNumber of spaces: 6\nCounting '[1,2,1,7]'\nNumber of 1: 2\nNumber of 7: 1\nNumber of 3: null\n",
						out));
	}

	public void testFac() {
		testFile("ini/examples/calculus/fac.ini",
				(p, out) -> assertEquals("fac1(10)=3628800\nfac2(10)=3628800\n", out));
	}

	public void testPrettyPrinter() {
		testFile("ini/examples/data_structures/pretty_printer.ini",
				(p, out) -> assertEquals(
						"<html>\n    <head>\n    </head>\n    <body bgcolor=\"white\" width=\"100%\">\nthis is a body text example \n        <b>\nthis is a strong text\n        </b>\n    </body>\n</html>\n{content:[{name:\"head\"},{attr:[{name:\"bgcolor\",value:\"white\"},{name:\"width\",value:\"100%\"}],content:[{text:\"this is a body text example \"},{content:[{text:\"this is a strong text\"}],name:\"b\"}],name:\"body\"}],name:\"html\"}\n",
						out));
	}

	public void _testProcessCommunication() {
		testFile("ini/examples/io/simple_pipeline.ini",
				(p, out) -> assertEquals("processes started\nc1: 1\nc2: 2.0\nend of pipeline: 3.0\n", out));
	}

	public void testHttpServer() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		testFile("ini/examples/io/http_server.ini", os, null);
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
					os.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testWebService() {
		new Thread() {
			@Override
			public void run() {
				testFile("ini/examples/io/web_service.ini", null);
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
		testFile("ini/examples/data_structures/fibonacci.ini",
				(p, out) -> assertEquals(
						"Fib(8)=34\n" + "Here is a flat Fibonacci tree for 5:\n"
								+ "[1=5,2=4,3=3,4=3,5=2,6=2,7=1,8=2,9=1,10=1,11=1,12=1,13=1,14=null,15=null,16=1,17=1,18=null,19=null,20=null,21=null,22=null,23=null,24=null,25=null,26=null,27=null,28=null,29=null,30=null,31=null,32=null]\n"
								+ "Here is a non-flat Fibonacci tree for 5:\n"
								+ "Tree1[left=Tree1[left=Tree1[left=Tree1[left=Tree1[value=1],right=Tree1[value=1],value=2],right=Tree1[value=1],value=3],right=Tree1[left=Tree1[value=1],right=Tree1[value=1],value=2],value=4],right=Tree1[left=Tree1[left=Tree1[value=1],right=Tree1[value=1],value=2],right=Tree1[value=1],value=3],value=5]\n"
								+ "Fibtree(5).left.value = 4\n" + "Fibtree(5).right.value = 3\n"
								+ "Fibtree(5).left.left.value = 3\n" + "Fibtree(5).left.right.value = 2\n"
								+ "Fibtree(5).right.left.value = 2\n" + "Fibtree(5).right.right.value = 1\n"
								+ "Here is a non-flat Fibonacci tree for 5:\n"
								+ "Node[left=Node[left=Node[left=Node[left=Leaf[value=1],right=Leaf[value=1],value=2],right=Node[value=1],value=3],right=Node[left=Leaf[value=1],right=Leaf[value=1],value=2],value=4],right=Node[left=Node[left=Leaf[value=1],right=Leaf[value=1],value=2],right=Node[value=1],value=3],value=5]\n"
								+ "Here is a non-flat Fibonacci tree for 5:\n"
								+ "Node[left=Node[left=Node[left=Node[left=Leaf[value=1],right=Leaf[value=1],value=2],right=Node[value=1],value=3],right=Node[left=Leaf[value=1],right=Leaf[value=1],value=2],value=4],right=Node[left=Node[left=Leaf[value=1],right=Leaf[value=1],value=2],right=Node[value=1],value=3],value=5]\n"
								+ "Here is a non-flat Fibonacci tree for 5:\n"
								+ "Node[left=Node[left=Node[left=Node[left=Leaf[value=1],right=Leaf[value=1]],right=Node[value=1]],right=Node[left=Leaf[value=1],right=Leaf[value=1]]],right=Node[left=Node[left=Leaf[value=1],right=Leaf[value=1]],right=Node[value=1]]]\n"
								+ "Here is a non-flat Fibonacci tree for 5:\n"
								+ "Node[left=Node[left=Node[left=Node[left=Leaf[value=1],right=Leaf[value=1],value=2],right=Leaf[value=1],value=3],right=Node[left=Leaf[value=1],right=Leaf[value=1],value=2],value=4],right=Node[left=Node[left=Leaf[value=1],right=Leaf[value=1],value=2],right=Leaf[value=1],value=3],value=5]\n",
						out));
	}

	public void testFibonacciFunction() {
		testFile("ini/examples/calculus/fibonacci.ini", (p, out) -> assertEquals("Fib(8)=21\n", out));
	}

	public void testFibonacciProcess() {
		testFile("ini/examples/processes/fibonacci.ini", (p, out) -> assertEquals("Fib(8)=21\n", out));
	}

	public void testOperators() {
		testFile("ini/examples/channels/operators.ini", 2400,
				(p, out) -> assertEquals("e: 0\ne: 0\ne: 1\ne: 2\ne: 2\ne: 4\ne: 3\n"
						+ "e: 6\ne: 4\ne: 8\ne: 5\ne: 10\ne: 6\ne: 12\ne: 7\n" + "e: 14\ne: 8\ne: 16\ne: 9\ne: 18\n",
						out));
	}

	public void testReduce1() {
		testFile("ini/examples/channels/reduce1.ini",
				(p, out) -> assertEquals("[ =3,a=1,e=1,h=1,i=2,s=2,t=3,x=1]\n", out));
	}

	public void testReduce2() {
		for (int i = 0; i < 100; i++) {
			final int it = i;
			testFile("ini/examples/channels/reduce2.ini", (p, out) -> assertEquals("messed up at " + it,
					"[ =[ , , ],a=[a],e=[e],h=[h],i=[i,i],s=[s,s],t=[t,t,t],x=[x]]\n", out));
			// testFile("ini/examples/channels/reduce2.ini", (p, out) ->
			// assertEquals("messed up at "+it,"[ =[ , ,
			// ],a=[a],s=[s,s],t=[t,t,t],e=[e],h=[h],x=[x],i=[i,i]]\n", out));
		}
	}

	public void testReduce3() {
		testFile("ini/examples/channels/reduce3.ini", (p, out) -> assertEquals("[6=[Sacha],23=[Renaud,Joris]]\n", out));
	}

	public void testMapReduce() {
		testFile("ini/examples/distributed_computing/map_reduce.ini",
				(p, out) -> assertEquals(
						"[20=[Charlène,Fabien,Dany],21=[Sacha,Yann],22=[Yoann,Carlos],23=[Renaud,Joris,Laurentiu,Paul]]\n",
						out));
	}

}
