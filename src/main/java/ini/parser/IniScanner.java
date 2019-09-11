/* The following code was generated by JFlex 1.3.5 on 9/9/19 11:47 AM */


/*
 * This source code file is the exclusive property of its author. No copy or 
 * usage of the source code is permitted unless the author contractually 
 * allows it under the terms of a well-defined agreement.
 */

package ini.parser;

import java_cup.runtime.*;
import ini.ast.Token;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.3.5
 * on 9/9/19 11:47 AM from the specification file
 * <tt>file:/Users/renaudpawlak/Documents/INI/ini/src/main/java/ini/parser/ini.lex</tt>
 */
class IniScanner implements java_cup.runtime.Scanner {

  /** This character denotes the end of file */
  final public static int YYEOF = -1;

  /** initial size of the lookahead buffer */
  final private static int YY_BUFFERSIZE = 16384;

  /** lexical states */
  final public static int LAMBDA = 4;
  final public static int STRING = 1;
  final public static int YYINITIAL = 0;
  final public static int CHAR = 2;
  final public static int USERTYPE = 3;

  /** 
   * Translates characters to character classes
   */
  final private static String yycmap_packed = 
    "\11\0\1\3\1\1\1\0\1\3\1\2\22\0\1\3\1\54\1\5"+
    "\1\10\1\60\1\0\1\61\1\64\1\20\1\17\1\7\1\62\1\21"+
    "\1\43\1\16\1\6\1\14\11\15\1\45\1\0\1\52\1\53\1\44"+
    "\1\57\1\63\32\13\1\50\1\4\1\51\1\0\1\12\1\0\1\36"+
    "\1\11\1\33\1\42\1\34\1\30\1\11\1\41\1\22\2\11\1\37"+
    "\1\23\1\32\1\25\1\24\1\11\1\26\1\35\1\27\1\31\3\11"+
    "\1\40\1\11\1\46\1\56\1\47\1\55\uff81\0";

  /** 
   * Translates characters to character classes
   */
  final private static char [] yycmap = yy_unpack_cmap(yycmap_packed);

  /** 
   * Translates a state to a row index in the transition table
   */
  final private static int yy_rowMap [] = { 
        0,    53,   106,   159,   212,   265,   265,   318,   265,   371, 
      424,   265,   477,   530,   583,   636,   689,   742,   265,   795, 
      265,   848,   901,   954,  1007,  1060,  1113,  1166,  1219,  1272, 
     1325,  1378,   265,   265,   265,   265,   265,  1431,  1484,  1537, 
      265,  1590,   265,   265,  1643,  1696,   265,   265,  1749,   265, 
     1802,   265,  1855,  1908,  1961,   424,  2014,   265,   265,   265, 
     2067,   265,   477,  2120,  2173,  2226,  2279,  2332,  2385,  2438, 
      265,   795,  2491,  2544,  2597,  2650,   530,  2703,  2756,  2809, 
     2862,  2915,  2968,  3021,  3074,  3127,   265,   265,   265,   265, 
      265,   265,   265,   265,   265,   265,   265,  3180,  3233,  3286, 
     3339,   265,  3392,  3445,   265,   265,  3498,   265,  3551,  3604, 
     2226,  3657,  3710,  3763,  3816,  3869,  3922,  3975,  4028,  4081, 
     4134,  4187,  4240,  4293,  4346,  4399,  4452,  4505,  4558,  4611, 
     4664,   530,   530,   530,  4717,  4770,   530,   530,  4823,  4876, 
     4929,  4982,  5035,  5088,  5141,  5194,  5247,  5300,   530,  5353, 
     5406,  5459,   265,  5512,   530,  5565,   530,  5618,  5671,  5724, 
     5777,  5830,   530,  5883,   530,   530,  5936,  5989,   530,  6042
  };

  /** 
   * The packed transition table of the DFA (part 0)
   */
  final private static String yy_packed0 = 
    "\1\6\1\7\1\10\1\11\1\6\1\12\1\13\1\14"+
    "\1\15\1\16\1\6\1\17\1\20\1\21\1\22\1\23"+
    "\1\24\1\25\1\26\1\16\1\27\1\30\1\31\1\32"+
    "\1\33\2\16\1\34\1\35\5\16\1\36\1\37\1\40"+
    "\1\41\1\42\1\43\1\44\1\45\1\46\1\47\1\50"+
    "\1\51\1\52\1\53\1\54\1\55\1\56\1\57\1\60"+
    "\1\61\1\62\2\61\1\63\1\62\57\61\1\64\1\62"+
    "\2\64\1\65\57\64\1\62\1\6\1\66\1\67\1\11"+
    "\2\6\1\70\1\14\1\15\1\71\1\6\1\17\5\6"+
    "\1\25\21\71\2\6\1\41\2\6\1\44\1\45\1\6"+
    "\1\72\2\6\1\73\7\6\1\7\1\6\1\11\2\6"+
    "\1\70\1\6\1\15\1\71\5\6\1\23\1\74\1\25"+
    "\21\71\10\6\1\75\11\6\66\0\1\7\70\0\1\76"+
    "\65\0\1\77\1\100\55\0\1\77\1\11\1\101\62\77"+
    "\1\0\1\102\1\103\1\102\5\0\5\16\4\0\21\16"+
    "\10\0\1\104\22\0\5\17\4\0\21\17\40\0\1\105"+
    "\62\0\2\21\1\105\57\0\1\106\4\0\1\107\3\0"+
    "\21\106\23\0\1\110\1\111\1\110\5\0\1\112\10\0"+
    "\21\112\23\0\1\102\1\103\1\102\5\0\5\16\4\0"+
    "\1\16\1\113\17\16\10\0\1\104\12\0\1\102\1\103"+
    "\1\102\5\0\5\16\4\0\4\16\1\114\14\16\10\0"+
    "\1\104\12\0\1\102\1\103\1\102\5\0\5\16\4\0"+
    "\6\16\1\115\12\16\10\0\1\104\12\0\1\102\1\103"+
    "\1\102\5\0\5\16\4\0\12\16\1\116\6\16\10\0"+
    "\1\104\12\0\1\102\1\103\1\102\5\0\5\16\4\0"+
    "\4\16\1\117\11\16\1\120\1\121\1\16\10\0\1\104"+
    "\12\0\1\102\1\103\1\102\5\0\5\16\4\0\7\16"+
    "\1\122\4\16\1\123\4\16\10\0\1\104\12\0\1\102"+
    "\1\103\1\102\5\0\5\16\4\0\14\16\1\124\4\16"+
    "\10\0\1\104\12\0\1\102\1\103\1\102\5\0\5\16"+
    "\4\0\15\16\1\125\3\16\10\0\1\104\12\0\1\102"+
    "\1\103\1\102\5\0\5\16\4\0\12\16\1\126\6\16"+
    "\10\0\1\104\54\0\1\127\1\130\73\0\1\131\64\0"+
    "\1\132\55\0\1\133\6\0\1\134\64\0\1\135\67\0"+
    "\1\136\67\0\1\137\65\0\1\140\2\0\1\61\1\0"+
    "\2\61\1\63\1\0\60\61\1\0\2\61\1\63\60\61"+
    "\64\0\1\64\1\0\1\141\1\142\21\0\1\143\2\0"+
    "\1\144\1\145\35\0\1\66\74\0\5\71\4\0\21\71"+
    "\66\0\1\146\20\0\7\147\1\150\55\147\1\0\1\11"+
    "\64\0\1\102\1\103\1\102\47\0\1\104\12\0\1\102"+
    "\127\0\1\151\34\0\1\152\1\153\60\0\5\106\2\0"+
    "\1\154\1\0\21\106\23\0\1\110\64\0\1\155\1\156"+
    "\1\155\5\0\5\112\1\0\1\157\1\0\1\110\21\112"+
    "\23\0\1\102\1\103\1\102\5\0\5\16\4\0\2\16"+
    "\1\160\16\16\10\0\1\104\12\0\1\102\1\103\1\102"+
    "\5\0\5\16\4\0\3\16\1\161\15\16\10\0\1\104"+
    "\12\0\1\102\1\103\1\102\5\0\5\16\4\0\5\16"+
    "\1\162\13\16\10\0\1\104\12\0\1\102\1\103\1\102"+
    "\5\0\5\16\4\0\7\16\1\163\11\16\10\0\1\104"+
    "\12\0\1\102\1\103\1\102\5\0\5\16\4\0\2\16"+
    "\1\164\16\16\10\0\1\104\12\0\1\102\1\103\1\102"+
    "\5\0\5\16\4\0\1\165\20\16\10\0\1\104\12\0"+
    "\1\102\1\103\1\102\5\0\5\16\4\0\10\16\1\166"+
    "\10\16\10\0\1\104\12\0\1\102\1\103\1\102\5\0"+
    "\5\16\4\0\15\16\1\167\3\16\10\0\1\104\12\0"+
    "\1\102\1\103\1\102\5\0\5\16\4\0\13\16\1\170"+
    "\5\16\10\0\1\104\12\0\1\102\1\103\1\102\5\0"+
    "\5\16\4\0\13\16\1\171\5\16\10\0\1\104\12\0"+
    "\1\102\1\103\1\102\5\0\5\16\4\0\6\16\1\172"+
    "\2\16\1\173\7\16\10\0\1\104\12\0\1\141\111\0"+
    "\1\174\76\0\1\175\55\0\1\176\33\0\7\147\1\177"+
    "\55\147\7\150\1\200\55\150\14\0\2\153\50\0\1\155"+
    "\1\156\1\155\13\0\1\157\1\0\1\110\44\0\1\155"+
    "\64\0\1\102\1\103\1\102\5\0\5\16\4\0\3\16"+
    "\1\201\15\16\10\0\1\104\12\0\1\102\1\103\1\102"+
    "\5\0\5\16\4\0\11\16\1\202\7\16\10\0\1\104"+
    "\12\0\1\102\1\103\1\102\5\0\5\16\4\0\7\16"+
    "\1\203\11\16\10\0\1\104\12\0\1\102\1\103\1\102"+
    "\5\0\5\16\4\0\12\16\1\204\6\16\10\0\1\104"+
    "\12\0\1\102\1\103\1\102\5\0\5\16\4\0\12\16"+
    "\1\205\6\16\10\0\1\104\12\0\1\102\1\103\1\102"+
    "\5\0\5\16\4\0\13\16\1\206\5\16\10\0\1\104"+
    "\12\0\1\102\1\103\1\102\5\0\5\16\4\0\11\16"+
    "\1\207\7\16\10\0\1\104\12\0\1\102\1\103\1\102"+
    "\5\0\5\16\4\0\13\16\1\210\5\16\10\0\1\104"+
    "\12\0\1\102\1\103\1\102\5\0\5\16\4\0\12\16"+
    "\1\211\6\16\10\0\1\104\12\0\1\102\1\103\1\102"+
    "\5\0\5\16\4\0\12\16\1\212\6\16\10\0\1\104"+
    "\12\0\1\102\1\103\1\102\5\0\5\16\4\0\14\16"+
    "\1\213\4\16\10\0\1\104\12\0\1\102\1\103\1\102"+
    "\5\0\5\16\4\0\15\16\1\214\3\16\10\0\1\104"+
    "\36\0\1\215\63\0\1\216\72\0\1\217\32\0\6\147"+
    "\1\11\1\177\55\147\6\150\1\11\1\220\55\150\1\0"+
    "\1\102\1\103\1\102\5\0\5\16\4\0\4\16\1\221"+
    "\14\16\10\0\1\104\12\0\1\102\1\103\1\102\5\0"+
    "\5\16\4\0\12\16\1\222\6\16\10\0\1\104\12\0"+
    "\1\102\1\103\1\102\5\0\5\16\4\0\4\16\1\223"+
    "\14\16\10\0\1\104\12\0\1\102\1\103\1\102\5\0"+
    "\5\16\4\0\5\16\1\224\13\16\10\0\1\104\12\0"+
    "\1\102\1\103\1\102\5\0\5\16\4\0\12\16\1\225"+
    "\6\16\10\0\1\104\12\0\1\102\1\103\1\102\5\0"+
    "\5\16\4\0\7\16\1\226\11\16\10\0\1\104\12\0"+
    "\1\102\1\103\1\102\5\0\5\16\4\0\14\16\1\227"+
    "\4\16\10\0\1\104\44\0\1\230\65\0\1\231\63\0"+
    "\1\232\31\0\6\150\1\0\1\220\55\150\1\0\1\102"+
    "\1\103\1\102\5\0\5\16\4\0\5\16\1\233\13\16"+
    "\10\0\1\104\12\0\1\102\1\103\1\102\5\0\5\16"+
    "\4\0\13\16\1\234\5\16\10\0\1\104\12\0\1\102"+
    "\1\103\1\102\5\0\5\16\4\0\10\16\1\235\10\16"+
    "\10\0\1\104\12\0\1\102\1\103\1\102\5\0\5\16"+
    "\4\0\1\236\20\16\10\0\1\104\12\0\1\102\1\103"+
    "\1\102\5\0\5\16\4\0\15\16\1\237\3\16\10\0"+
    "\1\104\12\0\1\102\1\103\1\102\5\0\5\16\4\0"+
    "\4\16\1\240\14\16\10\0\1\104\45\0\1\241\57\0"+
    "\1\242\36\0\1\102\1\103\1\102\5\0\5\16\4\0"+
    "\13\16\1\243\5\16\10\0\1\104\12\0\1\102\1\103"+
    "\1\102\5\0\5\16\4\0\3\16\1\244\15\16\10\0"+
    "\1\104\12\0\1\102\1\103\1\102\5\0\5\16\4\0"+
    "\5\16\1\245\13\16\10\0\1\104\12\0\1\102\1\103"+
    "\1\102\5\0\5\16\4\0\12\16\1\246\6\16\10\0"+
    "\1\104\46\0\1\247\51\0\1\250\43\0\1\102\1\103"+
    "\1\102\5\0\5\16\4\0\10\16\1\251\10\16\10\0"+
    "\1\104\46\0\1\231\54\0\1\252\71\0\1\231\32\0";

  /** 
   * The transition table of the DFA
   */
  final private static int yytrans [] = yy_unpack();


  /* error codes */
  final private static int YY_UNKNOWN_ERROR = 0;
  final private static int YY_ILLEGAL_STATE = 1;
  final private static int YY_NO_MATCH = 2;
  final private static int YY_PUSHBACK_2BIG = 3;

  /* error messages for the codes above */
  final private static String YY_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Internal error: unknown state",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * YY_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private final static byte YY_ATTRIBUTE[] = {
     0,  1,  0,  0,  0,  9,  9,  1,  9,  1,  1,  9,  1,  3,  1,  1, 
     1,  3,  9,  1,  9,  3,  3,  3,  3,  3,  3,  3,  3,  3,  1,  1, 
     9,  9,  9,  9,  9,  1,  1,  1,  9,  1,  9,  9,  1,  1,  9,  9, 
     1,  9,  1,  9,  1,  3,  1,  1,  1,  9,  9,  9,  1,  9,  0,  0, 
     0,  0,  0,  0,  0,  0,  9,  0,  0,  0,  3,  3,  3,  3,  3,  3, 
     3,  3,  3,  3,  3,  3,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9, 
     9,  0,  0,  0,  0,  9,  0,  0, 13,  9,  1, 13,  0,  0,  2,  3, 
     3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  0,  0,  0,  0,  0, 
     3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  0,  0,  0,  0, 
     3,  3,  3,  3,  3,  3,  3,  0, 13,  0,  3,  3,  3,  3,  3,  3, 
     0,  0,  3,  3,  3,  3,  0,  0,  3,  0
  };

  /** the input device */
  private java.io.Reader yy_reader;

  /** the current state of the DFA */
  private int yy_state;

  /** the current lexical state */
  private int yy_lexical_state = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char yy_buffer[] = new char[YY_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int yy_markedPos;

  /** the textposition at the last state to be included in yytext */
  private int yy_pushbackPos;

  /** the current text position in the buffer */
  private int yy_currentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int yy_startRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int yy_endRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn; 

  /** 
   * yy_atBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean yy_atBOL = true;

  /** yy_atEOF == true <=> the scanner is at the EOF */
  private boolean yy_atEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean yy_eof_done;

  /* user code: */
	//StringBuffer string=new StringBuffer();
	String fileName;
	public void setFileName(String name) {
		fileName=name;
	}
	public String getFileName() {
		return fileName;
	}
	private Symbol symbol(int type) {
		return new Symbol(type,yyline,yycolumn,
		    new Token(type,fileName,yytext(),
		                    yyline+1,yycolumn+1,
		                    yycolumn+1+yytext().length()));
	}
	private Symbol emptyString() {
		return new Symbol(sym.STRING,yyline,yycolumn,
		    new Token(sym.STRING,fileName,"",
		                    yyline+1,yycolumn+1,
		                    yycolumn+1+0));
	}
	//private Symbol symbol(int type,Object value) {
	//	return new Symbol(type,yyline,yycolumn,value);
	//}


  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  IniScanner(java.io.Reader in) {
    this.yy_reader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  IniScanner(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the split, compressed DFA transition table.
   *
   * @return the unpacked transition table
   */
  private static int [] yy_unpack() {
    int [] trans = new int[6095];
    int offset = 0;
    offset = yy_unpack(yy_packed0, offset, trans);
    return trans;
  }

  /** 
   * Unpacks the compressed DFA transition table.
   *
   * @param packed   the packed transition table
   * @return         the index of the last entry
   */
  private static int yy_unpack(String packed, int offset, int [] trans) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do trans[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] yy_unpack_cmap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 134) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   IOException  if any I/O-Error occurs
   */
  private boolean yy_refill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (yy_startRead > 0) {
      System.arraycopy(yy_buffer, yy_startRead, 
                       yy_buffer, 0, 
                       yy_endRead-yy_startRead);

      /* translate stored positions */
      yy_endRead-= yy_startRead;
      yy_currentPos-= yy_startRead;
      yy_markedPos-= yy_startRead;
      yy_pushbackPos-= yy_startRead;
      yy_startRead = 0;
    }

    /* is the buffer big enough? */
    if (yy_currentPos >= yy_buffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[yy_currentPos*2];
      System.arraycopy(yy_buffer, 0, newBuffer, 0, yy_buffer.length);
      yy_buffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = yy_reader.read(yy_buffer, yy_endRead, 
                                            yy_buffer.length-yy_endRead);

    if (numRead < 0) {
      return true;
    }
    else {
      yy_endRead+= numRead;  
      return false;
    }
  }


  /**
   * Closes the input stream.
   */
  final public void yyclose() throws java.io.IOException {
    yy_atEOF = true;            /* indicate end of file */
    yy_endRead = yy_startRead;  /* invalidate buffer    */

    if (yy_reader != null)
      yy_reader.close();
  }


  /**
   * Closes the current stream, and resets the
   * scanner to read from a new input stream.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>YY_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  final public void yyreset(java.io.Reader reader) throws java.io.IOException {
    yyclose();
    yy_reader = reader;
    yy_atBOL  = true;
    yy_atEOF  = false;
    yy_endRead = yy_startRead = 0;
    yy_currentPos = yy_markedPos = yy_pushbackPos = 0;
    yyline = yychar = yycolumn = 0;
    yy_lexical_state = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  final public int yystate() {
    return yy_lexical_state;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  final public void yybegin(int newState) {
    yy_lexical_state = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  final public String yytext() {
    return new String( yy_buffer, yy_startRead, yy_markedPos-yy_startRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  final public char yycharat(int pos) {
    return yy_buffer[yy_startRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  final public int yylength() {
    return yy_markedPos-yy_startRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void yy_ScanError(int errorCode) {
    String message;
    try {
      message = YY_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = YY_ERROR_MSG[YY_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  private void yypushback(int number)  {
    if ( number > yylength() )
      yy_ScanError(YY_PUSHBACK_2BIG);

    yy_markedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void yy_do_eof() throws java.io.IOException {
    if (!yy_eof_done) {
      yy_eof_done = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   IOException  if any I/O-Error occurs
   */
  public java_cup.runtime.Symbol next_token() throws java.io.IOException {
    int yy_input;
    int yy_action;

    // cached fields:
    int yy_currentPos_l;
    int yy_startRead_l;
    int yy_markedPos_l;
    int yy_endRead_l = yy_endRead;
    char [] yy_buffer_l = yy_buffer;
    char [] yycmap_l = yycmap;

    int [] yytrans_l = yytrans;
    int [] yy_rowMap_l = yy_rowMap;
    byte [] yy_attr_l = YY_ATTRIBUTE;
    int yy_pushbackPos_l = yy_pushbackPos = -1;
    boolean yy_was_pushback;

    while (true) {
      yy_markedPos_l = yy_markedPos;

      boolean yy_r = false;
      for (yy_currentPos_l = yy_startRead; yy_currentPos_l < yy_markedPos_l;
                                                             yy_currentPos_l++) {
        switch (yy_buffer_l[yy_currentPos_l]) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          yy_r = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          yy_r = true;
          break;
        case '\n':
          if (yy_r)
            yy_r = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          yy_r = false;
          yycolumn++;
        }
      }

      if (yy_r) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean yy_peek;
        if (yy_markedPos_l < yy_endRead_l)
          yy_peek = yy_buffer_l[yy_markedPos_l] == '\n';
        else if (yy_atEOF)
          yy_peek = false;
        else {
          boolean eof = yy_refill();
          yy_markedPos_l = yy_markedPos;
          yy_buffer_l = yy_buffer;
          if (eof) 
            yy_peek = false;
          else 
            yy_peek = yy_buffer_l[yy_markedPos_l] == '\n';
        }
        if (yy_peek) yyline--;
      }
      yy_action = -1;

      yy_startRead_l = yy_currentPos_l = yy_currentPos = 
                       yy_startRead = yy_markedPos_l;

      yy_state = yy_lexical_state;

      yy_was_pushback = false;

      yy_forAction: {
        while (true) {

          if (yy_currentPos_l < yy_endRead_l)
            yy_input = yy_buffer_l[yy_currentPos_l++];
          else if (yy_atEOF) {
            yy_input = YYEOF;
            break yy_forAction;
          }
          else {
            // store back cached positions
            yy_currentPos  = yy_currentPos_l;
            yy_markedPos   = yy_markedPos_l;
            yy_pushbackPos = yy_pushbackPos_l;
            boolean eof = yy_refill();
            // get translated positions and possibly new buffer
            yy_currentPos_l  = yy_currentPos;
            yy_markedPos_l   = yy_markedPos;
            yy_buffer_l      = yy_buffer;
            yy_endRead_l     = yy_endRead;
            yy_pushbackPos_l = yy_pushbackPos;
            if (eof) {
              yy_input = YYEOF;
              break yy_forAction;
            }
            else {
              yy_input = yy_buffer_l[yy_currentPos_l++];
            }
          }
          int yy_next = yytrans_l[ yy_rowMap_l[yy_state] + yycmap_l[yy_input] ];
          if (yy_next == -1) break yy_forAction;
          yy_state = yy_next;

          int yy_attributes = yy_attr_l[yy_state];
          if ( (yy_attributes & 2) == 2 )
            yy_pushbackPos_l = yy_currentPos_l;

          if ( (yy_attributes & 1) == 1 ) {
            yy_was_pushback = (yy_attributes & 4) == 4;
            yy_action = yy_state; 
            yy_markedPos_l = yy_currentPos_l; 
            if ( (yy_attributes & 8) == 8 ) break yy_forAction;
          }

        }
      }

      // store back cached position
      yy_markedPos = yy_markedPos_l;
      if (yy_was_pushback)
        yy_markedPos = yy_pushbackPos_l;

      switch (yy_action) {

        case 165: 
          {  return symbol(sym.DECLARE);  }
        case 171: break;
        case 164: 
          {  return symbol(sym.DEFAULT);  }
        case 172: break;
        case 162: 
          {  return symbol(sym.PROCESS);  }
        case 173: break;
        case 156: 
          {  return symbol(sym.RETURN);  }
        case 174: break;
        case 154: 
          {  return symbol(sym.IMPORT);  }
        case 175: break;
        case 107: 
          {  return symbol(sym.INVDOT);  }
        case 176: break;
        case 94: 
          {  return symbol(sym.ANDAND);  }
        case 177: break;
        case 91: 
          {  return symbol(sym.EQUALS);  }
        case 178: break;
        case 90: 
          {  return symbol(sym.IMPLIES);  }
        case 179: break;
        case 70: 
          {  return symbol(sym.DOTDOT);  }
        case 180: break;
        case 59: 
          {  return symbol(sym.LPAREN);  }
        case 181: break;
        case 57: 
          {  return symbol(sym.ASSIGN);  }
        case 182: break;
        case 19: 
          {  return symbol(sym.LPAREN);  }
        case 183: break;
        case 18: 
          {  return symbol(sym.RPAREN);  }
        case 184: break;
        case 1: 
        case 48: 
        case 50: 
          {  return symbol(sym.STRING);  }
        case 185: break;
        case 33: 
          {  return symbol(sym.LCPAREN);  }
        case 186: break;
        case 34: 
          {  return symbol(sym.RCPAREN);  }
        case 187: break;
        case 35: 
          {  return symbol(sym.LSPAREN);  }
        case 188: break;
        case 36: 
          {  return symbol(sym.RSPAREN);  }
        case 189: break;
        case 38: 
          {  return symbol(sym.ASSIGN);  }
        case 190: break;
        case 40: 
          {  return symbol(sym.MATCHES);  }
        case 191: break;
        case 43: 
          {  return symbol(sym.DOLLAR);  }
        case 192: break;
        case 47: 
          {  yybegin(CHAR);  }
        case 193: break;
        case 87: 
          {  return symbol(sym.ARROW_RIGHT);  }
        case 194: break;
        case 86: 
          {  return symbol(sym.MINUSMINUS);  }
        case 195: break;
        case 56: 
          {  return symbol(sym.IDENTIFIER);  }
        case 196: break;
        case 14: 
          {  return symbol(sym.TIDENTIFIER);  }
        case 197: break;
        case 13: 
        case 21: 
        case 22: 
        case 23: 
        case 24: 
        case 25: 
        case 26: 
        case 27: 
        case 28: 
        case 29: 
        case 74: 
        case 75: 
        case 77: 
        case 78: 
        case 79: 
        case 80: 
        case 81: 
        case 82: 
        case 83: 
        case 84: 
        case 85: 
        case 111: 
        case 112: 
        case 113: 
        case 114: 
        case 115: 
        case 116: 
        case 117: 
        case 118: 
        case 119: 
        case 120: 
        case 121: 
        case 122: 
        case 128: 
        case 129: 
        case 130: 
        case 134: 
        case 135: 
        case 138: 
        case 139: 
        case 144: 
        case 145: 
        case 146: 
        case 147: 
        case 149: 
        case 150: 
        case 155: 
        case 157: 
        case 158: 
        case 159: 
        case 163: 
          {  return symbol(sym.IDENTIFIER);  }
        case 198: break;
        case 101: 
          {  yybegin(YYINITIAL); return symbol(sym.IMPLIES);  }
        case 199: break;
        case 132: 
          {  yybegin(USERTYPE); return symbol(sym.TYPE);  }
        case 200: break;
        case 61: 
          {  return emptyString();  }
        case 201: break;
        case 5: 
        case 7: 
        case 12: 
        case 54: 
        case 55: 
        case 60: 
          {  System.out.println("unmatched: '"+yytext()+"'");  }
        case 202: break;
        case 168: 
          {  return symbol(sym.FUNCTION);  }
        case 203: break;
        case 95: 
          {  return symbol(sym.PLUSPLUS);  }
        case 204: break;
        case 92: 
          {  return symbol(sym.NOTEQUALS);  }
        case 205: break;
        case 42: 
          {  return symbol(sym.QUESTION);  }
        case 206: break;
        case 105: 
        case 106: 
          {  return symbol(sym.NUM);  }
        case 207: break;
        case 89: 
          {  return symbol(sym.LTE);  }
        case 208: break;
        case 88: 
          {  return symbol(sym.GTE);  }
        case 209: break;
        case 76: 
          {  return symbol(sym.OF);  }
        case 210: break;
        case 32: 
          {  return symbol(sym.COL);  }
        case 211: break;
        case 31: 
          {  return symbol(sym.GT);  }
        case 212: break;
        case 17: 
          {  return symbol(sym.DOT);  }
        case 213: break;
        case 15: 
        case 16: 
          {  return symbol(sym.INT);  }
        case 214: break;
        case 6: 
          {  return symbol(sym.LF);  }
        case 215: break;
        case 8: 
          {  /* ignore */  }
        case 216: break;
        case 10: 
          {  return symbol(sym.DIV);  }
        case 217: break;
        case 37: 
          {  return symbol(sym.LT);  }
        case 218: break;
        case 39: 
          {  return symbol(sym.NOT);  }
        case 219: break;
        case 44: 
          {  return symbol(sym.AND);  }
        case 220: break;
        case 46: 
          {  return symbol(sym.AT);  }
        case 221: break;
        case 53: 
          {  return symbol(sym.LF);  }
        case 222: break;
        case 148: 
          {  return symbol(sym.FALSE);  }
        case 223: break;
        case 137: 
          {  return symbol(sym.ELSE);  }
        case 224: break;
        case 136: 
          {  return symbol(sym.CASE);  }
        case 225: break;
        case 133: 
          {  return symbol(sym.THIS);  }
        case 226: break;
        case 131: 
          {  return symbol(sym.TRUE);  }
        case 227: break;
        case 93: 
          {  return symbol(sym.OROR);  }
        case 228: break;
        case 58: 
          {  return symbol(sym.TUBE);  }
        case 229: break;
        case 30: 
          {  return symbol(sym.MINUS);  }
        case 230: break;
        case 20: 
          {  return symbol(sym.COMMA);  }
        case 231: break;
        case 11: 
          {  return symbol(sym.MULT);  }
        case 232: break;
        case 41: 
          {  return symbol(sym.TUBE);  }
        case 233: break;
        case 45: 
          {  return symbol(sym.PLUS);  }
        case 234: break;
        case 51: 
        case 52: 
          {  return symbol(sym.CHAR);  }
        case 235: break;
        case 104: 
          {  Symbol s = symbol(sym.LAMBDA); yypushback(yylength()); yybegin(LAMBDA); return s;  }
        case 236: break;
        case 49: 
          {  yybegin(YYINITIAL);  }
        case 237: break;
        case 9: 
          {  yybegin(STRING);  }
        case 238: break;
        case 152: 
          {  Symbol s = symbol(sym.END); yybegin(YYINITIAL); yypushback(yylength()); return s;  }
        case 239: break;
        case 96: 
          {  Symbol s = symbol(sym.END); yybegin(YYINITIAL); yypushback(yylength()); return s;  }
        case 240: break;
        default: 
          if (yy_input == YYEOF && yy_startRead == yy_currentPos) {
            yy_atEOF = true;
            yy_do_eof();
            switch (yy_lexical_state) {
            case USERTYPE:
              {  yybegin(YYINITIAL); return symbol(sym.END);  }
            case 171: break;
            default:
              { return new java_cup.runtime.Symbol(sym.EOF); }
            }
          } 
          else {
            yy_ScanError(YY_NO_MATCH);
          }
      }
    }
  }


}
