package ini.test.typing;

import org.junit.Ignore;

import ini.test.IniTestCase;

public class TestTypeDeclarations extends IniTestCase {

	public TestTypeDeclarations(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testRightTypeDeclaration() {
		parseAndAttribCode("type Tree = Leaf[value:Int] | Node[value:Int,left:Tree,right:Tree]\n\n", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 0 errors: " + attrib.errors, 0, attrib.errors.size());
		});
	}

	// TODO: remove message "Couldn't repair and continue parse" on standard
	// error
	@Ignore
	public void _testUnallowedEmptyConstructorDeclaration() {
		parseAndAttribCode("type Tree = Leaf | Node[value:Int,left:Tree,right:Tree]\n", parser -> {
			assertEquals("expected 1 errors: " + parser.errors, 1, parser.errors.size());
			assertEquals("wrong type of error: " + parser.errors, "'|' is not expected", parser.errors.get(0).message);
		}, attrib -> {
		});
	}

	public void testUnallowedConstructorReference() {
		parseAndAttribCode("type Tree = Leaf[value:Int] | Node[value:Int,left:Node,right:Tree]\n", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 1 error: " + attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: " + attrib.errors, "illegal type reference",
					attrib.errors.get(0).message);
		});
	}

	public void testUndeclaredTypeReference() {
		parseAndAttribFile("ini/test/typing/type_declaration/undeclared_type_reference.ini", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 2 error: " + attrib.errors, 2, attrib.errors.size());
			assertEquals("wrong type of error: " + attrib.errors, "undeclared type", attrib.errors.get(0).message);
			assertEquals("wrong type of error: " + attrib.errors, "illegal type reference",
					attrib.errors.get(1).message);
		});
	}

	public void testIllegalDuplicateTypeDeclaration() {
		parseAndAttribCode("type Tree = Leaf[value:Int] | Node[value:Int,left:Tree,right:Tree]\n"
				+ "type Tree = Text[value:Int]\n", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 1 error: " + attrib.errors, 1, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors, "duplicate type name 'Tree'",
							attrib.errors.get(0).message);
				});
	}

	public void testIllegalSameNameForTypeAndExternalConstructor() {
		parseAndAttribCode("type Tree1 = Leaf[value:Int] | Node[value:Int,left:Tree1,right:Tree1]\n"
				+ "type Tree2 = Tree1[value:Int]\n", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 1 error: " + attrib.errors, 1, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors, "duplicate type name 'Tree1'",
							attrib.errors.get(0).message);
				});
	}

	public void testIllegalSameNameForTypeAndInternalConstructor() {
		parseAndAttribCode("type Tree = Leaf[value:Int] | Tree[value:Int,left:Tree,right:Tree]", parser -> {
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 1 error: " + attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: " + attrib.errors, "illegal constructor 'Tree'",
					attrib.errors.get(0).message);
		});
	}

	public void testUseOfAnonymousConstructor1() {
		parseAndAttribCode("type Tree = [value:Int,left:Tree,right:Tree]\n" + "process main() {"
				+ "  @init() { t = Tree[value = 1] }" + "}", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 0 errors: " + attrib.errors, 0, attrib.errors.size());
				});
	}

	public void testUseOfAnonymousConstructor2() {
		parseAndAttribCode(
				"type Point = [x:Int,y:Int]\n" + "process main() {" + "  @init() { p = Point[x = 1, y = 1] }" + "}",
				parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 0 errors: " + attrib.errors, 0, attrib.errors.size());
				});
	}

	public void testUseUndeclaredConstructorInMatchExpression() {
		parseAndAttribCode("function f() {" + "  case n ~ C[value==2] {" + "    println(n)" + "  }" + "}", parser -> {

			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
		}, attrib -> {
			assertEquals("expected 1 error: " + attrib.errors, 1, attrib.errors.size());
			assertEquals("wrong type of error: " + attrib.errors, "undeclared constructor 'C'",
					attrib.errors.get(0).message);
		});
	}

	public void testUseUndeclaredConstructorInConstructor() {
		parseAndAttribCode(
				"process f() {\n" + "  @init() {\n" + "    c = C[value=2]\n" + "    println(c)\n" + "  }\n" + "}",
				parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 1 error: " + attrib.errors, 1, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors, "undeclared constructor 'C'",
							attrib.errors.get(0).message);
				});
	}

}
