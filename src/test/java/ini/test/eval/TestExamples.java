package ini.test.eval;

public class TestExamples extends IniTestCase {

	public TestExamples(String name) {
		super(name);
	}

	public void testAlgebraicExpressions() {
		testFile("ini/examples/algebraic_expressions.ini",
				(p, out) -> assertEquals("The value of -(((3.0*2.0)+1.0)) is -7.0\n", out));
	}

	public void testComparator() {
		testFile("ini/examples/comparator.ini", (p, out) -> assertEquals("result:    aginoorrssttt\n", out));
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

}
