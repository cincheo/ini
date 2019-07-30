package testl.parser;

public class GenParser {
  public static void main(String[] args) throws Exception {
    java_cup.Main.main(new String[] {"-expect","0","-package","testl.parser","-parser","TestlParser","testl.cup"});
    JFlex.Main.main(new String[] {"testl.lex"});
  }
}
