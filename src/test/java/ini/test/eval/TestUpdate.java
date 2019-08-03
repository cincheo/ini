package ini.test.eval;

public class TestUpdate extends IniTestCase {

	public TestUpdate(String name) {
		super(name);
	}

	public void testSimpleUpdate1() {
		testFile("ini/test/update/var_update.ini", 50,
				(p, out) -> assertEquals(
						"expected output is wrong: ", "test: v is changed: 5,6",
						out));
	}

//	public void testListUpdate() {
//		assertEquals("expected output is wrong: ", "test: v is changed: false,6",
//				testFile("ini/test/update/list_update.ini"));
//	}

	/*
	 * public void testSimpleUpdate3() { try { IniParser parser =
	 * IniParser.parseCode("function main() {" + "	@init() {" +
	 * "		print(\"test: \")" + "		v = 5" + "	}" +
	 * "	@update[variable = v](a,b) {" + "		print(\"v is changed: \")" +
	 * "		print(a)" + "		print(\",\")" + "		print(b)" +
	 * "	}	" + "}"); assertEquals("expected 0 errors: " + parser.errors, 0,
	 * parser.errors.size()); parser.evalMainFunction(); Thread.sleep(50);
	 * assertEquals("expected output is wrong: ", "test: ",
	 * outputStream.toString()); } catch (Exception e) { fail(); } }
	 * 
	 * public void testSimpleUpdate2() { try { IniParser parser =
	 * IniParser.parseCode("function main() {" + "	@init() {" +
	 * "		print(\"test: \")" + "	}" + "	!x {" + "		v = 6" +
	 * "		x = true" + "	}" + "	@update[variable = v](a,b) {" +
	 * "		print(\"v is changed: \")" + "		print(a)" +
	 * "		print(\",\")" + "		print(b)" + "	}	" + "}");
	 * assertEquals("expected 0 errors: " + parser.errors, 0,
	 * parser.errors.size()); parser.evalMainFunction(); Thread.sleep(20);
	 * assertEquals("expected output is wrong: ", "test: v is changed: false,6",
	 * outputStream.toString()); } catch (Exception e) { fail(); } }
	 */

/*	public void testPingPongUpdate() {
		try {
			final IniParser parser = IniParser.parseFile("ini/test/update/ping_pong_update.ini");
			assertEquals("expected 0 errors: " + parser.errors, 0, parser.errors.size());
			parser.evalMainFunction();
			Thread.sleep(1000);
			String result = outputStream.toString();
			assertTrue("wrong result: " + result, result.startsWith("update2update1update2update1update2update1"));
		} catch (Exception e) {
			fail();
		}
	}*/
}
