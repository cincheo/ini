package ini.test.typing;

import org.junit.Ignore;

import ini.parser.IniParser;
import ini.type.AstAttrib;
import junit.framework.TestCase;

public class TestTypeDeclarations extends TestCase {

	public TestTypeDeclarations(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testRightTypeDeclaration() {
		try {
			IniParser parser = IniParser.parseCode(
					"type Tree = Leaf[value:Int] | Node[value:Int,left:Tree,right:Tree]");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 0 errors: "+attrib.errors, 0, attrib.errors.size());
		} catch (Exception e) {
			fail();
		}
	}

	// TODO: remove message "Couldn't repair and continue parse" on standard error
	@Ignore
	public void _testUnallowedEmptyConstructorDeclaration() {
		try {
			IniParser parser = IniParser.parseCode(
					"type Tree = Leaf | Node[value:Int,left:Tree,right:Tree]");
			assertEquals("expected 1 errors: "+parser.errors, 1, parser.errors.size());
			assertEquals("wrong type of error: "+parser.errors, "'|' is not expected", parser.errors.get(0).message);
		} catch (Exception e) {
			fail();
		}
	}
	
	public void testUnallowedConstructorReference() {
		try {
			IniParser parser = IniParser.parseCode(
					"type Tree = Leaf[value:Int] | Node[value:Int,left:Node,right:Tree]");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 1 error: "+attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "illegal type reference", attrib.errors.get(0).message);
		} catch (Exception e) {
			fail();
		}
	}

	public void testUndeclaredTypeReference() {
		try {
			IniParser parser = IniParser.parseCode(
					"type Tree = Leaf[value:Int] | Node[value:Int,left:Tree2,right:Tree]");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 2 error: "+attrib.errors, 2, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "undeclared type", attrib.errors.get(0).message);
			assertEquals("wrong type of error: "+attrib.errors, "illegal type reference", attrib.errors.get(1).message);
		} catch (Exception e) {
			fail();
		}
	}
	
	public void testIllegalDuplicateTypeDeclaration() {
		try {
			IniParser parser = IniParser.parseCode(
					"type Tree = Leaf[value:Int] | Node[value:Int,left:Tree,right:Tree]"+
					"type Tree = Text[value:Int]");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 1 error: "+attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "duplicate type name 'Tree'", attrib.errors.get(0).message);
		} catch (Exception e) {
			fail();
		}
	}

	public void testIllegalSameNameForTypeAndExternalConstructor() {
		try {
			IniParser parser = IniParser.parseCode(
					"type Tree1 = Leaf[value:Int] | Node[value:Int,left:Tree1,right:Tree1]"+
					"type Tree2 = Tree1[value:Int]");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 1 error: "+attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "duplicate type name 'Tree1'", attrib.errors.get(0).message);
		} catch (Exception e) {
			fail();
		}
	}
	
	public void testIllegalSameNameForTypeAndInternalConstructor() {
		try {
			IniParser parser = IniParser.parseCode(
					"type Tree = Leaf[value:Int] | Tree[value:Int,left:Tree,right:Tree]");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 1 error: "+attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "illegal constructor 'Tree'", attrib.errors.get(0).message);
		} catch (Exception e) {
			fail();
		}
	}

	public void testUseOfAnonymousConstructor1() {
		try {
			IniParser parser = IniParser.parseCode(
					"type Tree = [value:Int,left:Tree,right:Tree]" +
					"function main() {" +
					"  @init() { t = Tree[value = 1] }" +
					"}");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 0 errors: "+attrib.errors, 0, attrib.errors.size());
		} catch (Exception e) {
			fail();
		}
	}
	
	public void testUseOfAnonymousConstructor2() {
		try {
			IniParser parser = IniParser.parseCode(
					"type Point = [x:Int,y:Int]" +
					"function main() {" +
					"  @init() { p = Point[x = 1, y = 1] }" +
					"}");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 0 errors: "+attrib.errors, 0, attrib.errors.size());
		} catch (Exception e) {
			fail();
		}
	}
	
	public void testUseUndeclaredConstructorInMatchExpression() {
		try {
			IniParser parser = IniParser.parseCode(
					"function f() {" +
					"  n ~ C[value==2] {" +
					"    println(n)" +
					"  }"+
					"}");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 1 error: "+attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "undeclared constructor 'C'", attrib.errors.get(0).message);
		} catch (Exception e) {
			fail();
		}
	}

	public void testUseUndeclaredConstructorInConstructor() {
		try {
			IniParser parser = IniParser.parseCode(
					"function f() {\n" +
					"  @init() {\n" +
					"    c = C[value=2]\n" +
					"    println(c)\n" +
					"  }\n"+
					"}");
			assertEquals("expected 0 errors: "+parser.errors, 0, parser.errors.size());
			AstAttrib attrib = parser.attrib();
			assertEquals("expected 1 error: "+attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: "+attrib.errors, "undeclared constructor 'C'", attrib.errors.get(0).message);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
}
