package ini.test.typing;

import org.junit.Ignore;
import org.junit.Test;

import ini.parser.IniParser;
import ini.type.AstAttrib;
import junit.framework.TestCase;

public class TestFunctions extends TestCase {

	public TestFunctions(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testWrongReturnTypeInRecursiveFunction() {
		try {
			IniParser parser = IniParser.parseCode(
					"type Tree = Leaf[value:Int]"+
					"| Node[value:Int,left:Tree,right:Tree]\n"+
					"function fibtree3(n) {"+
					"	n ~	Node[value>2,!left,!right] {"+
					"		n.left = fibtree3(Node[value=n.value - 1])"+
					"	}\n"+
					"	@end() {"+
					"		return 2"+
					"	}"+
					"}");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 2 error: "+attrib.errors, 2, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'Int' is not compatible with 'Tree[value:Int]'", attrib.errors.get(0).message);
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'Int' is not compatible with 'Tree[value:Int]'", attrib.errors.get(1).message);
		} catch (Exception e) {
			fail();
		}
	}

	// TODO
	@Ignore
	public void parametersForRecursiveFunction() {
		try {
			IniParser parser = IniParser.parseCode(
					"function f() { @init() { f(a,b) } }");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 1 error: "+attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "wrong number of parameters", attrib.errors.get(0).message);
		} catch (Exception e) {
			fail();
		}
	}
	
	public void testWrongParameterType() {
		try {
			IniParser parser = IniParser.parseCode(
					"function main() { @init() { f(1.2) } }"+
					"function f(n) { @end() { return n+1 } }");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 2 error: "+attrib.errors, 2, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'Float' is not compatible with 'Int'", attrib.errors.get(0).message);
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'Float' is not compatible with 'Int'", attrib.errors.get(1).message);
		} catch (Exception e) {
			fail();
		}
	}
	
	public void testPolymorphicFunction() {
		try {
			IniParser parser = IniParser.parseCode(
					"function main() { @init() { println(f([1,3]))\n println(f(\"13\")) } }"+
					"function f(l) { @end() { swap(l[0],l[1])\n return l } }");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 0 error: "+attrib.errors, 0, attrib.errors.size());
		} catch (Exception e) {
			fail();
		}
	}

	public void testWrongPolymorphicFunctionInvocation() {
		try {
			IniParser parser = IniParser.parseCode(
					"function main() { @init() { println(f([1,3]))\n println(f(\"13\"))\n println(f(1.2))\n } }"+
					"function f(l) { @end() { swap(l[0],l[1])\n return l } }");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 3 error: "+attrib.errors, 3, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch", attrib.errors.get(0).message.substring(0,13));
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch", attrib.errors.get(1).message.substring(0,13));
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch", attrib.errors.get(2).message.substring(0,13));
		} catch (Exception e) {
			fail();
		}
	}

	public void testBindingInvocationParameterType() {
		try {
			IniParser parser = IniParser.parseCode(
					"f1(Int)=>Void [class=\"any\", member=\"any\"]\n"+
					"function f2() { @init() { f1(2.1) } }");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 1 error: "+attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'Float' is not compatible with 'Int'", attrib.errors.get(0).message);
		} catch (Exception e) {
			fail();
		}
	}
	
	public void testBindingInvocationResultType() {
		try {
			IniParser parser = IniParser.parseCode(
					"f1(Int)=>Int [class=\"any\", method=\"any\"]\n"+
					"function f2() { @init() && f1(2)==1.2 {} }");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 2 errors: "+attrib.errors, 2, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'Int' is not compatible with 'Float'", attrib.errors.get(0).message);
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'Int' is not compatible with 'Float'", attrib.errors.get(1).message);
		} catch (Exception e) {
			fail();
		}
	}
	
	public void testWrongFacInvocation() {
		try {
			IniParser parser = IniParser.parseCode("function main() {"+
					"	@init() {"+
					"		f=10.4\n"+
					"		fac(f)"+
					"	}"+
					"}"+
					"function fac(n) {"+
					"	@init() {"+
					"		f=1\n"+
					"		i=2\n"+
					"	}\n"+
					"	i <= n {"+
					"		f=f*i++"+
					"	}\n"+
					"	@end() {"+
					"		return f"+
					"	}"+
					"}");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 2 error: "+attrib.errors, 2, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'Int' is not compatible with 'Float'", attrib.errors.get(0).message);
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'Int' is not compatible with 'Float'", attrib.errors.get(1).message);
		} catch (Exception e) {
			fail();
		}
	}

	public void testRightFacInvocation() {
		try {
			IniParser parser = IniParser.parseCode("function main() {"+
					"	@init() {"+
					"		f=10\n"+
					"		fac(f)"+
					"	}"+
					"}"+
					"function fac(n) {"+
					"	@init() {"+
					"		f=1\n"+
					"		i=2"+
					"	}\n"+
					"	i <= n {"+
					"		f=f*i++"+
					"	}\n"+
					"	@end() {"+
					"		return f"+
					"	}"+
					"}");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 0 error: "+attrib.errors, 0, attrib.errors.size());
		} catch (Exception e) {
			fail();
		}
	}
	
	public void testVoidReturnType() {
		try {
			IniParser parser = IniParser.parseCode(
					"function nothing() {}"+
					"function main() { @init() { i = 1\n i = nothing() } }");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 2 errors: "+attrib.errors, 2, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'Void' is not compatible with 'Int'", attrib.errors.get(0).message);
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'Void' is not compatible with 'Int'", attrib.errors.get(1).message);
		} catch (Exception e) {
			fail();
		}
	}

	// TODO
	@Ignore
	@Test
	public void voidNotAllowedInStrings() {
		try {
			IniParser parser = IniParser.parseCode(
					"function nothing() {}"+
					"function main() { @init() { println(\"a\"+nothing()+\"b\") } }");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 1 errors: "+attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'Void' is not compatible with 'String'", attrib.errors.get(0).message);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Ignore
	@Test
	public void evalParameterType() {
		try {
			IniParser parser = IniParser.parseCode(
					"function main() {"+
					"	@init() {"+
					"		f = function(int_to_char)"+
					"		println(eval(f,0.0))"+
					"	}"+
					"}"+
					"function int_to_char(i) {"+
					"	i == 0 {"+
					"		return \"test\""+
					"	}"+
					"	@end() {"+
					"		return \"\""+
					"	}"+
					"}");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 1 error: "+attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'Int' is not compatible with 'Float'", attrib.errors.get(0).message);
		} catch (Exception e) {
			fail();
		}
	}
	
	// TODO: remove message "Couldn't repair and continue parse" on standard error
	@Ignore
	@Test
	public void _testEvalReturnType() {
		try {
			IniParser parser = IniParser.parseCode(
					"function main() {"+
					"	@init() {"+
					"		f = function(f)"+
					"		i = 0 + eval(f,0)"+
					"	}"+
					"}"+
					"function f(i) {"+
					"	i == 0 {"+
					"		return 0"+
					"	}"+
					"	@end() {"+
					"		return 1"+
					"	}"+
					"}");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 1 error: "+attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'Int' is not compatible with 'Float'", attrib.errors.get(0).message);
		} catch (Exception e) {
			fail();
		}
	}
	
}
