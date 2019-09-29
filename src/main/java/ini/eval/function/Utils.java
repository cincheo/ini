package ini.eval.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bson.Document;

import com.mongodb.MongoClient;

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

	public static void main(String[] args) {
		System.out.println("start test");
		MongoClient c = new MongoClient("localhost:27017");
		System.out.println("connected");
		c.getDatabase("test").getCollection("test").insertOne(Document.parse("{key1:\"value\"}"));
		System.out.println("wrote document");
		System.out.println("===>"+c.getDatabase("test").getCollection("test").find().first().toJson());
		c.close();
	}
	
}
