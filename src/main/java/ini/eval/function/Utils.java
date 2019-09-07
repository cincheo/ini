package ini.eval.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {

	public static long mod(long x, long y) {
		return x % y;
	}
	
	public static String read_keyboard() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			return in.readLine();
		} catch (IOException e) {
		} 
		return null;
		
	}
	
}
