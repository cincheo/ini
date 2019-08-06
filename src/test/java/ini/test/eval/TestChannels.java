package ini.test.eval;

public class TestChannels extends IniTestCase {

	public TestChannels(String name) {
		super(name);
	}

	public void testChannel1() {
		testFile("ini/test/channels/channel1.ini", (p, out) -> assertEquals("string value = coucou 1\n", out));
	}

	public void testChannel2() {
		testFile("ini/test/channels/channel2.ini", (p, out) -> assertEquals("int value = 1\n", out));
	}

	public void testChannel3() {
		testFile("ini/test/channels/channel3.ini",
				(p, out) -> assertEquals(
						"{lastName:\"Pawlak\",firstNames:[\"Renaud\",\"Bruno\",\"Pierre\"],height:184}\nperson value = Data({lastName=Data(Pawlak),firstNames=Data({0=Data(Renaud),1=Data(Bruno),2=Data(Pierre)}),height=Data(184)})\n{lastName:\"Pawlak\",firstNames:[\"Renaud\",\"Bruno\",\"Pierre\"],height:184}\n",
						out));
	}

	public void testChannel4() {
		testFile("ini/test/channels/channel4.ini",
				(p, out) -> assertEquals("list value = [\"Renaud\",\"Bruno\",\"Pierre\"]\nBruno\n", out));
	}

	public void testChannel5() {
		testFile("ini/test/channels/channel5.ini", (p, out) -> assertEquals("dict value = {key:\"abc\"}\nabc\n", out));
	}

	public void testChannel6() {
		testFile("ini/test/channels/channel6.ini", (p, out) -> assertEquals("double value = 1.0\n", out));
	}

}