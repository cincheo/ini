package ini.test.typing;

import ini.parser.IniParser;
import ini.type.AstAttrib;
import junit.framework.TestCase;

public class TestTypingRules extends TestCase {

	public TestTypingRules(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testStringByStringDivisionNotAllowed() {
		try {
			IniParser parser = IniParser.parseCode(
					"process main() {"+ 
					"	@init() {"+ 
					"		println(f(\"a\",\"b\"))"+
					"	}"+
					"}\n"+
					"function f(i,j) {"+
					"	case 0.0 == i/j {"+
					"		return 0"+
					"	}"+
					"}");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 2 error: "+attrib.errors, 2, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'String' is not compatible with 'Double'", attrib.errors.get(0).message);
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'String' is not compatible with 'Double'", attrib.errors.get(1).message);
		} catch (Exception e) {
			fail();
		}
	}

	public void testStringByStringMultNotAllowed() {
		try {
			IniParser parser = IniParser.parseCode(
					"process main() {"+ 
					"	@init() {"+ 
					"		println(\"a\"*\"b\")"+
					"	}"+
					"}");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 1 error: "+attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'String' is not compatible with 'Double'", attrib.errors.get(0).message);
		} catch (Exception e) {
			fail();
		}
	}

	public void testUnaryOpOnlyWorksOnNumbers() {
		try {
			IniParser parser = IniParser.parseCode(
					"function main() {"+ 
					"	println(f(\"a\"))"+
					"}\n"+
					"function f(i) {"+
					"	return -i"+
					"}");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 2 error: "+attrib.errors, 2, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'String' is not compatible with 'Double'", attrib.errors.get(0).message);
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'String' is not compatible with 'Double'", attrib.errors.get(1).message);
		} catch (Exception e) {
			fail();
		}
	}
	
}
