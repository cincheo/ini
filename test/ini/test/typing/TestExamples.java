package ini.test.typing;

import ini.parser.IniParser;
import ini.type.AstAttrib;
import junit.framework.TestCase;

public class TestExamples extends TestCase {

	public TestExamples(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testAlgebraicExpressions() {
		try {
			IniParser parser = IniParser.parseFile("ini/examples/algebraic_expressions.ini");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 0 error: "+attrib.errors, 0, attrib.errors.size());
		} catch (Exception e) {
			fail();
		}
	}

	public void testComparator() {
		try {
			IniParser parser = IniParser.parseFile("ini/examples/comparator.ini");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 0 error: "+attrib.errors, 0, attrib.errors.size());
		} catch (Exception e) {
			fail();
		}
	}

	public void testCountOccurences() {
		try {
			IniParser parser = IniParser.parseFile("ini/examples/count_occurences.ini");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 0 error: "+attrib.errors, 0, attrib.errors.size());
		} catch (Exception e) {
			fail();
		}
	}

	public void testFac() {
		try {
			IniParser parser = IniParser.parseFile("ini/examples/fac.ini");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 0 error: "+attrib.errors, 0, attrib.errors.size());
		} catch (Exception e) {
			fail();
		}
	}

	public void testFibonacci() {
		try {
			IniParser parser = IniParser.parseFile("ini/examples/fibonacci.ini");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 0 error: "+attrib.errors, 0, attrib.errors.size());
		} catch (Exception e) {
			fail();
		}
	}

	public void testSort() {
		try {
			IniParser parser = IniParser.parseFile("ini/examples/sort.ini");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 0 error: "+attrib.errors, 0, attrib.errors.size());
		} catch (Exception e) {
			fail();
		}
	}

	public void testPrettyPrinter() {
		try {
			IniParser parser = IniParser.parseFile("ini/examples/pretty_printer.ini");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 0 error: "+attrib.errors, 0, attrib.errors.size());
		} catch (Exception e) {
			fail();
		}
	}
	
	public void testHttpServer() {
		try {
			IniParser parser = IniParser.parseFile("ini/examples/unpublished/http_server.ini");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 0 error: "+attrib.errors, 0, attrib.errors.size());
		} catch (Exception e) {
			fail();
		}
	}

	public void testUpdate() {
		try {
			IniParser parser = IniParser.parseFile("ini/examples/unpublished/update.ini");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 0 error: "+attrib.errors, 0, attrib.errors.size());
		} catch (Exception e) {
			fail();
		}
	}
	
	public void testWebService() {
		try {
			IniParser parser = IniParser.parseFile("ini/examples/unpublished/web_service.ini");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 0 error: "+attrib.errors, 0, attrib.errors.size());
		} catch (Exception e) {
			fail();
		}
	}
	
	public void testIO() {
		try {
			IniParser parser = IniParser.parseFile("ini/examples/unpublished/io.ini");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 0 error: "+attrib.errors, 0, attrib.errors.size());
		} catch (Exception e) {
			fail();
		}
	}

	public void testLCSS() {
		try {
			IniParser parser = IniParser.parseFile("ini/examples/lcss.ini");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 0 error: "+attrib.errors, 0, attrib.errors.size());
		} catch (Exception e) {
			fail();
		}
	}
	
	
}
