package testl.parser;

import java.io.FileNotFoundException;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
			String fileName = "src/testl/parser/test.testl";
	      TestlScanner scanner= new TestlScanner(new java.io.FileReader(fileName));
	      scanner.setFileName(fileName);
	      TestlParser parser= new TestlParser(scanner);
	      try {
	    	parser.parse();
	    	System.out.println(parser.expression);
	    	System.out.println("result = "+new Eval().eval(parser.expression));
	    	
		  } catch(Exception e) {
		  	e.printStackTrace();
		  }
	}

}
