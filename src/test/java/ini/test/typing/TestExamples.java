package ini.test.typing;

import ini.test.IniTestCase;

public class TestExamples extends IniTestCase {

	public TestExamples(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testAlgebraicExpressions() {
		parseAndAttribFile("ini/examples/data_structures/algebraic_expressions.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 0 error: " + attrib.errors, 0, attrib.errors.size());
		});
	}

	public void testComparator() {
		parseAndAttribFile("ini/examples/data_structures/comparator.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 0 error: " + attrib.errors, 0, attrib.errors.size());
		});
	}

	public void testCountOccurences() {
		parseAndAttribFile("ini/examples/calculus/count_occurences.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 0 error: " + attrib.errors, 0, attrib.errors.size());
		});
	}

	public void testFac() {
		parseAndAttribFile("ini/examples/calculus/fac.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 0 error: " + attrib.errors, 0, attrib.errors.size());
		});
	}

	public void testFibonacci() {
		parseAndAttribFile("ini/examples/data_structures/fibonacci.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 0 error: " + attrib.errors, 0, attrib.errors.size());
		});
	}

	public void testSort() {
		parseAndAttribFile("ini/examples/data_structures/sort.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 0 error: " + attrib.errors, 0, attrib.errors.size());
		});
	}

	public void testPrettyPrinter() {
		parseAndAttribFile("ini/examples/data_structures/pretty_printer.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 0 error: " + attrib.errors, 0, attrib.errors.size());
		});
	}

	public void testHttpServer() {
		parseAndAttribFile("ini/examples/unpublished/http_server.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 0 error: " + attrib.errors, 0, attrib.errors.size());
		});
	}

	public void testUpdate() {
		parseAndAttribFile("ini/examples/unpublished/update.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 0 error: " + attrib.errors, 0, attrib.errors.size());
		});
	}

	public void testWebService() {
		parseAndAttribFile("ini/examples/unpublished/web_service.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 0 error: " + attrib.errors, 0, attrib.errors.size());
		});
	}

	public void testIO() {
		parseAndAttribFile("ini/examples/unpublished/io.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 0 error: " + attrib.errors, 0, attrib.errors.size());
		});
	}

	public void testLCSS() {
		parseAndAttribFile("ini/examples/calculus/lcss.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 0 error: " + attrib.errors, 0, attrib.errors.size());
		});
	}

}
