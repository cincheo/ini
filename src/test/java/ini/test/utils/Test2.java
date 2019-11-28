package ini.test.utils;

import ini.test.IniTestCase;

public class Test2 {

	public void m() {
		IniTestCase.currentParser.out.println("method of test2");
	}

	public void m(String s) {
		IniTestCase.currentParser.out.println("method of test2 - " + s);
	}

}
