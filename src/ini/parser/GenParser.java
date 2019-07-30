package ini.parser;

/**
 * This class is used to generate the INI lexer and parser out of the 
 * JFLex and CUP specifications. When run, it produces the files 
 * {@link grapple.parser.RScanner}&nbsp;and {@link grapple.parser.RParser}.
 * @author Renaud Pawlak
 */
public class GenParser {

  /**
   * Generate the INI lexer and parser.
   * @param args nothing
   * @throws Exception in case something real bad occurs
   */
  public static void main(String[] args) throws Exception {
    java_cup.Main.main(new String[] {"-expect","0","-package","ini.parser","-parser","IniParser","ini.cup"});
    JFlex.Main.main(new String[] {"ini.lex"});
  }
}
