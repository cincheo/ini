package ini.test.typing;

import ini.test.IniTestCase;

public class TestFields extends IniTestCase {

	public TestFields(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testWrongFieldAccessInMatchRule() {
		parseAndAttribCode("declare type Tree = Leaf[value:Int] | Node[value:Int,left:Tree,right:Tree]\n" //
				+ "function f(n) {"
				+ "  case n ~ Node[value==2,!left,!right] {" //
				+ "    n.let = Leaf[value=1]\n" //
				+ "    n.right = Leaf[value=1]\n" //
				+ "  }" //
				+ "}", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 1 error: " + attrib.errors, 1, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors, "undeclared field 'let'",
							attrib.errors.get(0).message);
				});
	}

	public void testWrongFieldAccessInMatchExpression() {
		parseAndAttribCode("declare type Tree = Leaf[value:Int] | Node[value:Int,left:Tree,right:Tree]\n" //
				+ "function f(n) {"
				+ "  case n ~ Node[value==2,!let,!right] {\n" //
				+ "    n.left = Leaf[value=1]\n" //
				+ "    n.right = Leaf[value=1]\n" //
				+ "  }" //
				+ "}", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 1 error: " + attrib.errors, 1, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors, "undeclared field or variable",
							attrib.errors.get(0).message);
				});
	}

	public void testWrongFieldAccessTypeInMatchExpression() {
		parseAndAttribCode("declare type Tree = Leaf[value:Int] | Node[value:Int,left:Tree,right:Tree]\n" //
				+ "process f(n) {" //
				+ "  n ~ Node[value==2.0,!left,!right] {" //
				+ "    n.left = Leaf[value=1]\n" //
				+ "    n.right = Leaf[value=1]\n" //
				+ "  }" //
				+ "}", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 2 errors: " + attrib.errors, 2, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'Int' is not compatible with 'Double'", attrib.errors.get(0).message);
					assertEquals("wrong type of error: " + attrib.errors,
							"type mismatch: 'Int' is not compatible with 'Double'", attrib.errors.get(1).message);
				});
	}

	public void testFieldAccessAfterAssignment() {
		parseAndAttribCode("declare type Point = [x:Int,y:Int]\n" //
				+ "process f() {" //
				+ "  @init() {" //
				+ "    p = Point[x=0,y=0]\n" //
				+ "    x = p.z\n" //
				+ "  }" //
				+ "}", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 1 error: " + attrib.errors, 1, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors, "undeclared field 'z' in type 'Point'",
							attrib.errors.get(0).message);
				});
	}

	public void testFieldAccessAfterAssignmentAndOp() {
		parseAndAttribCode("declare type Point = [x:Int,y:Int]\n" + "process f() {" + "  @init() {" + "    p = Point[x=0,y=0]\n"
				+ "    x = p.x - p.z\n" + "  }" + "}", parser -> {
					assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
				}, attrib -> {
					assertEquals("expected 1 error: " + attrib.errors, 1, attrib.errors.size());
					assertEquals("wrong type of error: " + attrib.errors, "undeclared field 'z' in type 'Point'",
							attrib.errors.get(0).message);
				});
	}

}
