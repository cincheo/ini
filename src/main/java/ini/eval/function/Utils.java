package ini.eval.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {

	public static Number mod(Number x, Number y) {
		if (x instanceof Integer) {
			return (int) x % (int) y;
		} else {
			return (long) x % (long) y;
		}
	}

	public static String read_keyboard() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			return in.readLine();
		} catch (IOException e) {
		}
		return null;

	}

}
