package ini.test.eval;

import ini.test.IniTestCase;

public class TestSync extends IniTestCase {

	public TestSync(String name) {
		super(name);
	}

	public void testSync() {
		for (int i = 0; i < 10; i++) {
			int it = i;
			testFile("ini/test/sync/sync.ini",
					(p, out) -> assertEquals("failed in iteration " + it, "e: 1\nu: 1\ne: 2\nu: 2\n", out));
		}
	}

	public void testNoSync() {
		testFile("ini/test/sync/nosync.ini", (p, out) -> assertEquals("u: 1\ne: 1\nu: 2\ne: 2\n", out));
	}

}
