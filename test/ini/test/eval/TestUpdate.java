package ini.test.eval;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;

import ini.eval.function.SleepFunction;
import ini.parser.IniParser;
import ini.type.AstAttrib;
import junit.framework.TestCase;

public class TestUpdate extends TestCase {

	ByteArrayOutputStream outputStream;
	PrintStream out;

	public TestUpdate(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		outputStream = new ByteArrayOutputStream();
		out = new PrintStream(outputStream);
		System.setOut(out);
	}

	public void testSimpleUpdate1() {
		try {
			IniParser parser = IniParser.parseCode("function main() {"
					+ "	@init() {" + "		print(\"test: \")" + "		v = 5" + "	}"
					+ "	!x {" + "		v = 6" + "		x = true" + "	}"
					+ "	@update[variable = v](a,b) {"
					+ "		print(\"v is changed: \")" + "		print(a)"
					+ "		print(\",\")" + "		print(b)" + "	}	" + "}");
			assertEquals("expected 0 errors: " + parser.errors, 0,
					parser.errors.size());
			parser.evalMainFunction();
			Thread.sleep(50);
			assertEquals("expected output is wrong: ",
					"test: v is changed: 5,6", outputStream.toString());
		} catch (Exception e) {
			fail();
		}
	}

	public void testListUpdate() {
		try {
			IniParser parser = IniParser.parseCode("function main() {"
					+ "	@init() {" + "		print(\"test: \")" + "		l = []" + "	}"
					+ "	!x {" + "		l[0] = 6" + "		x = true" + "	}"
					+ "	@update[variable = l](a,b) {"
					+ "		print(\"l is changed: \")" + "		print(a)"
					+ "		print(\",\")" + "		print(b)" + "	}	" + "}");
			assertEquals("expected 0 errors: " + parser.errors, 0,
					parser.errors.size());
			parser.evalMainFunction();
			Thread.sleep(50);
			assertEquals("expected output is wrong: ",
					"test: v is changed: false,6", outputStream.toString());
		} catch (Exception e) {
			fail();
		}
	}

	public void testSimpleUpdate3() {
		try {
			IniParser parser = IniParser.parseCode("function main() {"
					+ "	@init() {" + "		print(\"test: \")" + "		v = 5" + "	}"
					+ "	@update[variable = v](a,b) {"
					+ "		print(\"v is changed: \")" + "		print(a)"
					+ "		print(\",\")" + "		print(b)" + "	}	" + "}");
			assertEquals("expected 0 errors: " + parser.errors, 0,
					parser.errors.size());
			parser.evalMainFunction();
			Thread.sleep(50);
			assertEquals("expected output is wrong: ", "test: ",
					outputStream.toString());
		} catch (Exception e) {
			fail();
		}
	}

	public void testSimpleUpdate2() {
		try {
			IniParser parser = IniParser.parseCode("function main() {"
					+ "	@init() {" + "		print(\"test: \")" + "	}" + "	!x {"
					+ "		v = 6" + "		x = true" + "	}"
					+ "	@update[variable = v](a,b) {"
					+ "		print(\"v is changed: \")" + "		print(a)"
					+ "		print(\",\")" + "		print(b)" + "	}	" + "}");
			assertEquals("expected 0 errors: " + parser.errors, 0,
					parser.errors.size());
			parser.evalMainFunction();
			Thread.sleep(20);
			assertEquals("expected output is wrong: ",
					"test: v is changed: false,6", outputStream.toString());
		} catch (Exception e) {
			fail();
		}
	}

	public void testPingPongUpdate() {
		try {
			final IniParser parser = IniParser.parseCode(
					"function main() {" +
					"	@init() {" +
					"		i = 0" +
					"		j = 0" +
					"	}" +
					"	j == 0  {" +
					"		j = 1" +
					"	}" +
					"	 u1:@update[variable = i](oldv, newv) {" +
					"		j = 1" +
					"		print(\"update 1\")" +
					"	}" +
					"	 u2:@update[variable = j](oldv, newv) {" +
					"		i = 2" +
					"		print(\"update 2\")" +
					"	}" +
					"}");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			Thread t = new Thread() {
				public void run() {
					try {
						parser.evalMainFunction();
					} catch (Exception e) {
						e.printStackTrace();
					}
				};				
			};
			t.start();
			Thread.sleep(1000);
			if(t.getState() == Thread.State.TERMINATED) {
				fail("Infinite loop was expected" + outputStream.toString());
			}
		} catch (Exception e) {
			fail();
		}
	}
}
