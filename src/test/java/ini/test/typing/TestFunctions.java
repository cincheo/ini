package ini.test.typing;

import org.junit.Ignore;
import org.junit.Test;

import ini.test.IniTestCase;

public class TestFunctions extends IniTestCase {

	public TestFunctions(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testWrongReturnTypeInRecursiveFunction() {
		parseAndAttribCode("declare type Tree = Leaf[value:Int]" //
				+ "| Node[value:Int,left:Tree,right:Tree]\n" //
				+ "process fibtree3(n) {" //
				+ "	n ~	Node[value>2,!left,!right] {" //
				+ "		n.left = fibtree3(Node[value=n.value - 1])" //
				+ "	}\n" //
				+ "	@end() {" //
				+ "		return 2" //
				+ "	}" //
				+ "}", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 2 error: " + attrib.errors, 2, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'Int' is not compatible with 'Tree[value:Int]'",
							attrib.errors.get(0).message);
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'Int' is not compatible with 'Tree[value:Int]'",
							attrib.errors.get(1).message);
				});
	}

	public void testParametersForRecursiveFunction() {
		parseAndAttribCode("function f() { f(a,b) }", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 1 error: " + attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: " + attrib.errors, "wrong number of arguments",
					attrib.errors.get(0).message);
		});
	}

	public void testWrongParameterType() {
		parseAndAttribCode("process main() { @init() { f(1.2) } }\n" //
				+ "process f(n) { @end() { return n+1 } }", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {

					assertEquals("expected 2 error: " + attrib.errors, 2, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'Double' is not compatible with 'Int'", attrib.errors.get(0).message);
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'Double' is not compatible with 'Int'", attrib.errors.get(1).message);
				});
	}

	public void testPolymorphicFunction() {
		parseAndAttribCode("process main() { @init() { println(f([1,3]))\n println(f(\"13\")) } }\n"
				+ "process f(l) { @end() { swap(l[0],l[1])\n return l } }", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 0 error: " + attrib.errors, 0, attrib.errors.size());
				});
	}

	public void testWrongPolymorphicFunctionInvocation() {
		parseAndAttribCode("process main() { @init() { println(f([1,3]))\n println(f(\"13\"))\n println(f(1.2))\n } }\n"
				+ "process f(l) { @end() { swap(l[0],l[1])\n return l } }", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {

					assertEquals("expected 3 error: " + attrib.errors, 3, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors, "type mismatch",
							attrib.errors.get(0).message.substring(0, 13));
					assertEquals("wrong type of error: " + attrib.errors, "type mismatch",
							attrib.errors.get(1).message.substring(0, 13));
					assertEquals("wrong type of error: " + attrib.errors, "type mismatch",
							attrib.errors.get(2).message.substring(0, 13));
				});
	}

	public void testBindingInvocationParameterType() {
		parseAndAttribCode("declare f1(Int)=>Void : [class=\"any\", member=\"any\"]\n" //
				+ "process f2() { @init() { f1(2.1) } }", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {

					assertEquals("expected 1 error: " + attrib.errors, 1, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'Int' is not compatible with 'Double'", attrib.errors.get(0).message);
				});
	}

	public void testBindingInvocationResultType() {
		parseAndAttribCode("declare f1(Int)=>Int : [class=\"any\", method=\"any\"]\n" //
				+ "process f2() { @init() && f1(2)==1.2 {} }", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {

					assertEquals("expected 2 errors: " + attrib.errors, 2, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'Int' is not compatible with 'Double'", attrib.errors.get(0).message);
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'Int' is not compatible with 'Double'", attrib.errors.get(1).message);
				});
	}

	public void testWrongFacInvocation() {
		parseAndAttribCode("process main() {" //
				+ "	@init() {" //
				+ "		f=10.4\n" //
				+ "		fac(f)" //
				+ "	}" //
				+ "}\n" //
				+ "process fac(n) {" //
				+ "	@init() {" //
				+ "		f=1\n" //
				+ "		i=2\n" //
				+ "	}\n" //
				+ "	i <= n {" //
				+ "		f=f*i++" //
				+ "	}\n" //
				+ "	@end() {" //
				+ "		return f" //
				+ "	}" //
				+ "}", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 2 error: " + attrib.errors, 2, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'Int' is not compatible with 'Double'", attrib.errors.get(0).message);
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'Int' is not compatible with 'Double'", attrib.errors.get(1).message);
				});
	}

	public void testRightFacInvocation() {
		parseAndAttribCode("function main() {" //
				+ "	f=10\n" //
				+ "	fac(f)" //
				+ "}\n" //
				+ "process fac(n) {" //
				+ "	@init() {" + "		f=1\n" //
				+ "		i=2" //
				+ "	}\n" //
				+ "	i <= n {" //
				+ "		f=f*i++" //
				+ "	}\n" + "	@end() {" //
				+ "		return f" //
				+ "	}" //
				+ "}", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 0 error: " + attrib.errors, 0, attrib.errors.size());
				});
	}

	public void testFunctionVariableClash() {
		parseAndAttribCode("function main() {" //
				+ "	f=1\n" //
				+ "	f=f(2)\n" //
				+ "	println(f)" //
				+ "}\n" //
				+ "function f(n) {" //
				+ "	return n+1" //
				+ "}", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 0 error: " + attrib.errors, 0, attrib.errors.size());
				});
	}

	public void testVoidReturnType() {
		parseAndAttribCode("function nothing() {}\n" + "process main() { @init() { i = 1\n i = nothing() } }",
				parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 2 errors: " + attrib.errors, 2, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'Void' is not compatible with 'Int'", attrib.errors.get(0).message);
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'Void' is not compatible with 'Int'", attrib.errors.get(1).message);
				});
	}

	// TODO
	@Ignore
	@Test
	public void voidNotAllowedInStrings() {
		parseAndAttribCode("function nothing() {}" + "function main() { @init() { println(\"a\"+nothing()+\"b\") } }",
				parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {

					assertEquals("expected 1 errors: " + attrib.errors, 1, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'Void' is not compatible with 'String'", attrib.errors.get(0).message);
				});
	}

	@Ignore
	@Test
	public void evalParameterType() {
		parseAndAttribCode("function main() {" + "	@init() {" + "		f = function(int_to_char)"
				+ "		println(eval(f,0.0))" + "	}" + "}" + "function int_to_char(i) {" + "	i == 0 {"
				+ "		return \"test\"" + "	}" + "	@end() {" + "		return \"\"" + "	}" + "}", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {

					assertEquals("expected 1 error: " + attrib.errors, 1, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'Int' is not compatible with 'Float'", attrib.errors.get(0).message);
				});
	}

	// TODO: remove message "Couldn't repair and continue parse" on standard
	// error
	@Ignore
	@Test
	public void _testEvalReturnType() {
		parseAndAttribCode("function main() {" + "	@init() {" + "		f = function(f)" + "		i = 0 + eval(f,0)"
				+ "	}" + "}" + "function f(i) {" + "	i == 0 {" + "		return 0" + "	}" + "	@end() {"
				+ "		return 1" + "	}" + "}", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 1 error: " + attrib.errors, 1, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'Int' is not compatible with 'Float'", attrib.errors.get(0).message);
				});
	}

	public void testBindings() {
		testFile("ini/test/typing/functions/bindings.ini", (parser,out) -> {
			// TODO: should be 3 (Int) - to be fixed in bindings implementation
			assertEquals("3.0", out);
		});
	}

	public void testWrongBindings() {
		parseAndAttribFile("ini/test/typing/functions/wrong_bindings.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 4 errors: " + attrib.errors, 4, attrib.errors.size());
			assertEquals("wrong type of error: " + attrib.errors,
					"type mismatch: 'Double' is not compatible with 'Int'", attrib.errors.get(2).message);
			assertEquals("wrong type of error: " + attrib.errors,
					"type mismatch: 'Double' is not compatible with 'Int'", attrib.errors.get(3).message);
			assertEquals("wrong type of error: " + attrib.errors,
					"type mismatch: 'String' is not compatible with 'Double'", attrib.errors.get(0).message);
			assertEquals("wrong type of error: " + attrib.errors,
					"type mismatch: 'String' is not compatible with 'Double'", attrib.errors.get(1).message);
		});
	}
	
}
