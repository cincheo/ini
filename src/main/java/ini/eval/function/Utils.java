package ini.eval.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.List;

import com.google.common.collect.Lists;

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
	
	public static <T> List<T> toList(Iterable<T> iterable) {
		return Lists.newArrayList(iterable);
	}
	
	
	

	public static void main(String[] args) throws MalformedURLException, IOException {
        /*SparkSession spark = SparkSession
                .builder()
                .appName("Read JSON File to DataSet")
                .master("local[2]")
                .getOrCreate();

        Dataset<String> ds = spark.read().textFile("README.md");
        System.out.println(ds.count());
        ds.show();*/
        
 	}
		
	
}
