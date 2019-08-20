package ini.test.typing;

import ini.parser.IniParser;
import ini.type.AstAttrib;
import junit.framework.TestCase;

public class TestFields extends TestCase {

	public TestFields(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testWrongFieldAccessInMatchRule() {
		try {
			IniParser parser = IniParser.parseCode(
					"type Tree = Leaf[value:Int] | Node[value:Int,left:Tree,right:Tree]\n"+
					"function f() {" +
					"  n ~ Node[value==2,!left,!right] {" +
					"    n.let = Leaf[value=1]\n" +
					"    n.right = Leaf[value=1]\n" +
					"  }"+
					"}");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 1 error: "+attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "undeclared field 'let'", attrib.errors.get(0).message);
		} catch (Exception e) {
			fail();
		}
	}

	public void testWrongFieldAccessInMatchExpression() {
		try {
			IniParser parser = IniParser.parseCode(
					"type Tree = Leaf[value:Int] | Node[value:Int,left:Tree,right:Tree]\n"+
					"function f() {" +
					"  n ~ Node[value==2,!let,!right] {\n" +
					"    n.left = Leaf[value=1]\n" +
					"    n.right = Leaf[value=1]\n" +
					"  }"+
					"}");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 1 error: "+attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "undeclared field or variable", attrib.errors.get(0).message);
		} catch (Exception e) {
			fail();
		}
	}
	
	public void testWrongFieldAccessTypeInMatchExpression() {
		try {
			IniParser parser = IniParser.parseCode(
					"type Tree = Leaf[value:Int] | Node[value:Int,left:Tree,right:Tree]\n"+
					"function f() {" +
					"  n ~ Node[value==2.0,!left,!right] {" +
					"    n.left = Leaf[value=1]\n" +
					"    n.right = Leaf[value=1]\n" +
					"  }"+
					"}");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 2 errors: "+attrib.errors, 2, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'Int' is not compatible with 'Float'", attrib.errors.get(0).message);
			assertEquals("wrong type of error: "+attrib.errors, "type mismatch: 'Int' is not compatible with 'Float'", attrib.errors.get(1).message);
		} catch (Exception e) {
			fail();
		}
	}

	public void testFieldAccessAfterAssignment() {
		try {
			IniParser parser = IniParser.parseCode(
					"type Point = [x:Int,y:Int]\n"+
					"function f() {" +
					"  @init() {" +
					"    p = Point[x=0,y=0]\n" +
					"    x = p.z\n" +
					"  }"+
					"}");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 1 error: "+attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "undeclared field 'z' in type 'Point'", attrib.errors.get(0).message);
		} catch (Exception e) {
			fail();
		}
	}

	public void testFieldAccessAfterAssignmentAndOp() {
		try {
			IniParser parser = IniParser.parseCode(
					"type Point = [x:Int,y:Int]\n"+
					"function f() {" +
					"  @init() {" +
					"    p = Point[x=0,y=0]\n" +
					"    x = p.x - p.z\n" +
					"  }"+
					"}");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 1 error: "+attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "undeclared field 'z' in type 'Point'", attrib.errors.get(0).message);
		} catch (Exception e) {
			fail();
		}
	}
	
	
}
