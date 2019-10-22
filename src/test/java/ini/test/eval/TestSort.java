package ini.test.eval;

import ini.test.IniTestCase;

public class TestSort extends IniTestCase {

	public TestSort(String name) {
		super(name);
	}

	public void testSort1() {
		testFile("ini/test/sort/sort1.ini",
				(p, out) -> assertEquals(
						"[1,1,1,2,2,3,3,4,4,5,5,5,6,8,9,10]\n             ..ITaaabbddeeeeghiiiiilllnoorrssssssstttttuw\n",
						out));
	}

	public void testSort2() {
		testFile("ini/test/sort/sort2.ini",
				(p, out) -> assertEquals(
						"[1,1,1,2,2,3,3,4,4,5,5,5,6,8,9,10]\n             ..ITaaabbddeeeeghiiiiilllnoorrssssssstttttuw\n",
						out));
	}

	public void testSort3() {
		testFile("ini/test/sort/sort3.ini",
				(p, out) -> assertEquals(
						"[1,1,1,2,2,3,3,4,4,5,5,5,6,8,9,10]\n             ..ITaaabbddeeeeghiiiiilllnoorrssssssstttttuw\n",
						out));
	}

	public void testSort4() {
		testFile("ini/test/sort/sort4.ini",
				(p, out) -> assertEquals(
						"[1,1,1,2,2,3,3,4,4,5,5,5,6,8,9,10]\n             ..ITaaabbddeeeeghiiiiilllnoorrssssssstttttuw\n",
						out));
	}

	public void testSort5() {
		testFile("ini/test/sort/sort5.ini",
				(p, out) -> assertEquals(
						"[1,1,1,2,2,3,3,4,4,5,5,5,6,8,9,10]\n             ..ITaaabbddeeeeghiiiiilllnoorrssssssstttttuw\n",
						out));
	}

}
