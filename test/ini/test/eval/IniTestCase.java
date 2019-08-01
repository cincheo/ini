package ini.test.eval;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.BiConsumer;

import ini.parser.IniParser;
import junit.framework.TestCase;

public abstract class IniTestCase extends TestCase {

	private ByteArrayOutputStream outputStream;
	private PrintStream out;

	public IniTestCase(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		outputStream = new ByteArrayOutputStream();
		out = new PrintStream(outputStream);
		System.setOut(out);
	}

	protected void testFile(String file, BiConsumer<IniParser, String> assertions) {
		testFile(file, 0, assertions);
	}

	protected void testFile(String file, long sleepTime, BiConsumer<IniParser, String> assertions) {
		try {
			IniParser parser = IniParser.parseFile(file);
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
			parser.evalMainFunction();
			if(sleepTime>0) {
				Thread.sleep(sleepTime);
			}
			if(assertions!=null) {
				assertions.accept(parser, outputStream.toString());;
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
