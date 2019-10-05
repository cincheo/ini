package ini.eval.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;

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
		CleanerProperties props = new CleanerProperties();
		 
		// set some properties to non-default values
		props.setTranslateSpecialEntities(true);
		props.setTransResCharsToNCR(true);
		props.setOmitComments(true);
		 
		// do parsing
		TagNode tagNode = new HtmlCleaner(props).clean(
			new URL("https://en.wikipedia.org/wiki/Jacques_Chirac")
		);

		//System.out.println(tagNode.toString());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// serialize to xml file
		new PrettyXmlSerializer(props).writeToStream(
		    tagNode, out
		);
		System.out.println(out.toString("UTF8"));
		
		/*new PrettyXmlSerializer(props).writeToFile(
			    tagNode, "chinadaily.xml", "utf-8"
			);*/		
		 
		/*try {
			System.out.println(IOUtils.toString(new URL("https://en.wikipedia.org/wiki/Jacques_Chirac"), "UTF8"));
		} catch(Exception e) {
			e.printStackTrace();
		}*/
 	}
		
	
}
