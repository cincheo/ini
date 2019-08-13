package ini.test.eval;

public class TestSync extends IniTestCase {

	public TestSync(String name) {
		super(name);
	}

	public void testSync() {
		testFile("ini/test/sync/sync.ini",
				(p, out) -> assertEquals(
						"e: 1\nu: 1\ne: 2\nu: 2\n",
						out));
	}

	public void testNoSync() {
		testFile("ini/test/sync/nosync.ini",
				(p, out) -> assertEquals(
						"u: 1\ne: 1\nu: 2\ne: 2\n",
						out));
	}
	
}
