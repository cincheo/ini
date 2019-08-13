package ini.test.eval;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ini.parser.IniParser;
import junit.framework.TestCase;

public abstract class IniTestCase extends TestCase {

	static final Logger logger = LoggerFactory.getLogger("test");

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
		testFile(file, 0, null, assertions);
	}

	protected void testFile(String file, String node, BiConsumer<IniParser, String> assertions) {
		testFile(file, 0, node, assertions);
	}

	protected void testFile(String file, long sleepTime, BiConsumer<IniParser, String> assertions) {
		testFile(file, sleepTime, null, assertions);
	}

	protected void testFile(String file, long sleepTime, String node, BiConsumer<IniParser, String> assertions) {
		try {
			IniParser parser = file == null ? IniParser.parseCode("process main() {}") : IniParser.parseFile(file);
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
			if (node != null) {
				parser.deamon = true;
				parser.node = node;
			}
			parser.evalMainFunction();
			if (sleepTime > 0) {
				Thread.sleep(sleepTime);
			}
			if (assertions != null) {
				assertions.accept(parser, outputStream.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
