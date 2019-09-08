package ini.test.eval;

import ini.test.IniTestCase;

public class TestLambdas extends IniTestCase {

	public TestLambdas(String name) {
		super(name);
	}

	public void testHiddenFunction() {
		testFile("ini/test/lambdas/hiddenFunction.ini", (p, out) -> assertEquals("2", out));
	}

	public void testHiddenFunctionWrongParameters() {
		parseAndAttribFile("ini/test/lambdas/hiddenFunctionWrongParameters.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 1 errors: " + attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: " + attrib.errors, "wrong number of arguments",
					attrib.errors.get(0).message);
		});
	}

	public void testHiddenFunctionWrongParameters2() {
		parseAndAttribFile("ini/test/lambdas/hiddenFunctionWrongParameters2.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 1 errors: " + attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: " + attrib.errors, "wrong number of arguments",
					attrib.errors.get(0).message);
		});
	}

	public void testHiddenFunctionDefaultValue() {
		testFile("ini/test/lambdas/hiddenFunctionDefaultValue.ini", (p, out) -> assertEquals("4", out));
	}
	
	public void testHiddenFunctionWrongDefaultValue() {
		parseAndAttribFile("ini/test/lambdas/hiddenFunctionWrongDefaultValue.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 2 errors: " + attrib.errors, 2, attrib.errors.size());
			assertEquals("wrong type of error: " + attrib.errors, "type mismatch: 'Int' is not compatible with 'String'",
					attrib.errors.get(0).message);
			assertEquals("wrong type of error: " + attrib.errors, "type mismatch: 'Int' is not compatible with 'String'",
					attrib.errors.get(1).message);
		});
	}
	
	public void testLambda() {
		testFile("ini/test/lambdas/lambda.ini", (p, out) -> assertEquals("1", out));
	}

	public void testWrongLambda() {
		parseAndAttribFile("ini/test/lambdas/wrongLambda.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 2 errors: " + attrib.errors, 2, attrib.errors.size());
			assertEquals("wrong type of error: " + attrib.errors, "type mismatch: 'Int' is not compatible with 'Double'",
					attrib.errors.get(0).message);
			assertEquals("wrong type of error: " + attrib.errors, "type mismatch: 'Int' is not compatible with 'Double'",
					attrib.errors.get(1).message);
		});
	}

	public void testWrongLambdaPassedAsArgument() {
		parseAndAttribFile("ini/test/lambdas/wrongLambdaPassedAsArgument.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 2 errors: " + attrib.errors, 2, attrib.errors.size());
			assertEquals("wrong type of error: " + attrib.errors, "type mismatch: 'Int' is not compatible with 'Double'",
					attrib.errors.get(0).message);
			assertEquals("wrong type of error: " + attrib.errors, "type mismatch: 'Int' is not compatible with 'Double'",
					attrib.errors.get(1).message);
		});
	}
	
	public void testLambdaPassedAsArgument() {
		testFile("ini/test/lambdas/lambdaPassedAsArgument.ini", (p, out) -> assertEquals("2", out));
	}

	public void testLambdaHiddingFunction() {
		testFile("ini/test/lambdas/lambdaHiddingFunction.ini", (p, out) -> assertEquals("1", out));
	}
	
}
