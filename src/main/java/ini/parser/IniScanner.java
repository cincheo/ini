/* The following code was generated by JFlex 1.3.5 on 10/8/19 8:01 AM */


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
 * on 10/8/19 8:01 AM from the specification file
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
  final public static int EMBEDED_EXPRESSION2 = 6;
  final public static int EMBEDED_EXPRESSION1 = 5;
  final public static int YYINITIAL = 0;
  final public static int CHAR = 2;
  final public static int USERTYPE = 3;

  /** 
   * Translates characters to character classes
   */
  final private static String yycmap_packed = 
    "\11\0\1\3\1\1\1\0\1\3\1\2\22\0\1\3\1\53\1\5"+
    "\1\10\1\57\1\0\1\60\1\63\1\17\1\16\1\7\1\61\1\20"+
    "\1\42\1\15\1\6\1\12\11\14\1\44\1\0\1\51\1\52\1\43"+
    "\1\56\1\62\32\13\1\47\1\4\1\50\1\0\1\11\1\0\1\35"+
    "\1\11\1\32\1\40\1\33\1\27\1\11\1\37\1\21\2\11\1\36"+
    "\1\22\1\31\1\24\1\23\1\11\1\25\1\34\1\26\1\30\3\11"+
    "\1\41\1\11\1\45\1\55\1\46\1\54\uff81\0";

  /** 
   * Translates characters to character classes
   */
  final private static char [] yycmap = yy_unpack_cmap(yycmap_packed);

  /** 
   * Translates a state to a row index in the transition table
   */
  final private static int yy_rowMap [] = { 
        0,    52,   104,   156,   208,   260,   312,   364,   364,   416, 
      364,   468,   520,   364,   572,   624,   676,   728,   780,   832, 
      364,   884,   364,   936,   988,  1040,  1092,  1144,  1196,  1248, 
     1300,  1352,  1404,  1456,   364,   364,   364,   364,   364,  1508, 
     1560,  1612,   364,  1664,   364,   364,  1716,  1768,   364,   364, 
      364,   364,  1820,  1872,   364,   364,  1924,  1976,  2028,  2080, 
     2132,  2184,   364,   364,   364,   364,  2236,   364,   364,   364, 
     2288,  2340,   364,  1664,   364,   572,  2392,   416,  2444,  2496, 
     2548,  2600,  2652,   364,   884,  2704,  2756,  2808,  2860,   624, 
     2912,  2964,  3016,  3068,  3120,  3172,  3224,  3276,   364,   364, 
      364,   364,   364,   364,   364,   364,   364,   364,   364,  1872, 
      364,   364,  3328,  3380,  3432,  3484,  3536,  2132,  3588,   364, 
     3640,  3692,   364,   364,  3744,   364,  3796,  3848,  2444,  3900, 
     3952,  4004,  4056,  4108,  4160,  4212,  4264,  4316,  4368,  4420, 
     4472,  4524,  4576,  4628,  4680,  4732,  4784,  4836,  4888,   624, 
      624,  4940,  4992,   624,   624,  5044,  5096,  5148,  5200,  5252, 
     5304,  5356,  5408,  5460,  5512,  5564,   624,  5616,  5668,  5720, 
      364,  5772,  5824,   624,  5876,   624,  5928,  5980,  6032,  6084, 
     6136,  6188,   624,  6240,   624,  6292,  6344,  6396,   624,  6448, 
     6500,  6552,  6604,  6656,  6708,  6760,  6812,  6864,  6916,  6968, 
     7020,  7072,  7124,  7176,  7228,  7280,  7332,  7384,  7436,  7488, 
      624,  7540,  7592,   364,  7644,  7696,  7748,  7800,  7852,  7904, 
     7956,  8008,  8060,  8112,   624,  8164,   364,  8216,  8268,   624, 
      364
  };

  /** 
   * The packed transition table of the DFA (part 0)
   */
  final private static String yy_packed0 = 
    "\1\10\1\11\1\12\1\13\1\10\1\14\1\15\1\16"+
    "\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26"+
    "\1\27\1\30\1\20\1\31\1\32\1\33\1\34\1\35"+
    "\2\20\1\36\1\37\4\20\1\40\1\20\1\41\1\42"+
    "\1\43\1\44\1\45\1\46\1\47\1\50\1\51\1\52"+
    "\1\53\1\54\1\55\1\56\1\57\1\60\1\61\1\62"+
    "\1\63\1\64\2\63\1\65\1\64\37\63\1\66\16\63"+
    "\1\67\1\70\2\67\1\71\56\67\1\70\1\10\1\72"+
    "\1\73\1\13\2\10\1\74\1\16\1\75\1\76\1\10"+
    "\1\22\4\10\1\27\21\76\2\10\1\43\2\10\1\46"+
    "\1\47\1\77\1\100\2\10\1\101\7\10\1\11\1\10"+
    "\1\13\2\10\1\74\1\10\1\75\1\76\4\10\1\25"+
    "\1\102\1\27\21\76\10\10\1\103\56\10\1\104\1\105"+
    "\23\10\1\106\1\16\1\10\1\76\1\21\1\10\1\23"+
    "\1\107\1\25\1\102\1\27\21\76\1\110\2\10\1\102"+
    "\1\111\1\46\1\47\1\10\1\51\1\52\1\53\1\112"+
    "\1\55\1\56\1\57\1\60\2\10\65\0\1\11\67\0"+
    "\1\113\64\0\1\114\1\115\54\0\1\114\1\11\1\116"+
    "\61\114\1\0\1\117\1\120\1\117\5\0\4\20\4\0"+
    "\21\20\10\0\1\121\26\0\1\122\57\0\4\22\4\0"+
    "\21\22\34\0\1\23\1\0\1\23\1\122\57\0\1\123"+
    "\3\0\1\124\3\0\21\123\23\0\1\125\1\126\1\125"+
    "\5\0\1\127\7\0\21\127\23\0\1\117\1\120\1\117"+
    "\5\0\4\20\4\0\1\20\1\130\17\20\10\0\1\121"+
    "\12\0\1\117\1\120\1\117\5\0\4\20\4\0\4\20"+
    "\1\131\14\20\10\0\1\121\12\0\1\117\1\120\1\117"+
    "\5\0\4\20\4\0\6\20\1\132\12\20\10\0\1\121"+
    "\12\0\1\117\1\120\1\117\5\0\4\20\4\0\12\20"+
    "\1\133\6\20\10\0\1\121\12\0\1\117\1\120\1\117"+
    "\5\0\4\20\4\0\4\20\1\134\11\20\1\135\2\20"+
    "\10\0\1\121\12\0\1\117\1\120\1\117\5\0\4\20"+
    "\4\0\7\20\1\136\4\20\1\137\4\20\10\0\1\121"+
    "\12\0\1\117\1\120\1\117\5\0\4\20\4\0\14\20"+
    "\1\140\4\20\10\0\1\121\12\0\1\117\1\120\1\117"+
    "\5\0\4\20\4\0\15\20\1\141\3\20\10\0\1\121"+
    "\12\0\1\117\1\120\1\117\5\0\4\20\4\0\12\20"+
    "\1\142\6\20\10\0\1\121\53\0\1\143\1\144\72\0"+
    "\1\145\63\0\1\146\54\0\1\147\6\0\1\150\63\0"+
    "\1\151\66\0\1\152\66\0\1\153\64\0\1\154\7\0"+
    "\1\63\37\0\1\155\16\0\1\156\1\0\44\156\1\157"+
    "\15\156\63\0\1\67\1\0\1\160\1\161\20\0\1\162"+
    "\2\0\1\163\1\164\10\0\1\165\24\0\1\72\70\0"+
    "\1\166\1\115\54\0\1\166\1\13\1\167\61\166\11\0"+
    "\4\76\4\0\21\76\65\0\1\170\31\0\1\123\7\0"+
    "\21\123\64\0\1\143\21\0\7\171\1\172\54\171\1\0"+
    "\1\117\1\120\1\117\46\0\1\121\12\0\1\117\125\0"+
    "\1\173\32\0\1\174\1\0\1\175\60\0\4\123\2\0"+
    "\1\176\1\0\21\123\23\0\1\125\63\0\1\177\1\200"+
    "\1\177\5\0\4\127\1\0\1\201\1\0\1\125\21\127"+
    "\23\0\1\117\1\120\1\117\5\0\4\20\4\0\2\20"+
    "\1\202\16\20\10\0\1\121\12\0\1\117\1\120\1\117"+
    "\5\0\4\20\4\0\3\20\1\203\15\20\10\0\1\121"+
    "\12\0\1\117\1\120\1\117\5\0\4\20\4\0\5\20"+
    "\1\204\13\20\10\0\1\121\12\0\1\117\1\120\1\117"+
    "\5\0\4\20\4\0\7\20\1\205\11\20\10\0\1\121"+
    "\12\0\1\117\1\120\1\117\5\0\4\20\4\0\1\206"+
    "\20\20\10\0\1\121\12\0\1\117\1\120\1\117\5\0"+
    "\4\20\4\0\10\20\1\207\10\20\10\0\1\121\12\0"+
    "\1\117\1\120\1\117\5\0\4\20\4\0\15\20\1\210"+
    "\3\20\10\0\1\121\12\0\1\117\1\120\1\117\5\0"+
    "\4\20\4\0\13\20\1\211\5\20\10\0\1\121\12\0"+
    "\1\117\1\120\1\117\5\0\4\20\4\0\13\20\1\212"+
    "\5\20\10\0\1\121\12\0\1\117\1\120\1\117\5\0"+
    "\4\20\4\0\6\20\1\213\2\20\1\214\7\20\10\0"+
    "\1\121\12\0\1\160\107\0\1\215\77\0\1\216\52\0"+
    "\1\217\66\0\1\220\31\0\1\13\62\0\7\171\1\221"+
    "\54\171\7\172\1\222\54\172\12\0\1\175\1\0\1\175"+
    "\50\0\1\177\1\200\1\177\12\0\1\201\1\0\1\125"+
    "\44\0\1\177\63\0\1\117\1\120\1\117\5\0\4\20"+
    "\4\0\3\20\1\223\15\20\10\0\1\121\12\0\1\117"+
    "\1\120\1\117\5\0\4\20\4\0\11\20\1\224\7\20"+
    "\10\0\1\121\12\0\1\117\1\120\1\117\5\0\4\20"+
    "\4\0\7\20\1\225\11\20\10\0\1\121\12\0\1\117"+
    "\1\120\1\117\5\0\4\20\4\0\12\20\1\226\6\20"+
    "\10\0\1\121\12\0\1\117\1\120\1\117\5\0\4\20"+
    "\4\0\13\20\1\227\5\20\10\0\1\121\12\0\1\117"+
    "\1\120\1\117\5\0\4\20\4\0\11\20\1\230\7\20"+
    "\10\0\1\121\12\0\1\117\1\120\1\117\5\0\4\20"+
    "\4\0\13\20\1\231\5\20\10\0\1\121\12\0\1\117"+
    "\1\120\1\117\5\0\4\20\4\0\12\20\1\232\6\20"+
    "\10\0\1\121\12\0\1\117\1\120\1\117\5\0\4\20"+
    "\4\0\12\20\1\233\6\20\10\0\1\121\12\0\1\117"+
    "\1\120\1\117\5\0\4\20\4\0\14\20\1\234\4\20"+
    "\10\0\1\121\12\0\1\117\1\120\1\117\5\0\4\20"+
    "\4\0\15\20\1\235\3\20\10\0\1\121\35\0\1\236"+
    "\62\0\1\237\71\0\1\240\64\0\1\241\31\0\6\171"+
    "\1\13\1\221\54\171\6\172\1\13\1\242\54\172\1\0"+
    "\1\117\1\120\1\117\5\0\4\20\4\0\4\20\1\243"+
    "\14\20\10\0\1\121\12\0\1\117\1\120\1\117\5\0"+
    "\4\20\4\0\12\20\1\244\6\20\10\0\1\121\12\0"+
    "\1\117\1\120\1\117\5\0\4\20\4\0\4\20\1\245"+
    "\14\20\10\0\1\121\12\0\1\117\1\120\1\117\5\0"+
    "\4\20\4\0\5\20\1\246\13\20\10\0\1\121\12\0"+
    "\1\117\1\120\1\117\5\0\4\20\4\0\12\20\1\247"+
    "\6\20\10\0\1\121\12\0\1\117\1\120\1\117\5\0"+
    "\4\20\4\0\7\20\1\250\11\20\10\0\1\121\12\0"+
    "\1\117\1\120\1\117\5\0\4\20\4\0\14\20\1\251"+
    "\4\20\10\0\1\121\43\0\1\252\64\0\1\253\62\0"+
    "\1\254\67\0\1\255\25\0\6\172\1\0\1\242\54\172"+
    "\1\0\1\117\1\120\1\117\5\0\4\20\4\0\5\20"+
    "\1\256\13\20\10\0\1\121\12\0\1\117\1\120\1\117"+
    "\5\0\4\20\4\0\13\20\1\257\5\20\10\0\1\121"+
    "\12\0\1\117\1\120\1\117\5\0\4\20\4\0\10\20"+
    "\1\260\10\20\10\0\1\121\12\0\1\117\1\120\1\117"+
    "\5\0\4\20\4\0\1\261\20\20\10\0\1\121\12\0"+
    "\1\117\1\120\1\117\5\0\4\20\4\0\15\20\1\262"+
    "\3\20\10\0\1\121\12\0\1\117\1\120\1\117\5\0"+
    "\4\20\4\0\4\20\1\263\14\20\10\0\1\121\44\0"+
    "\1\264\56\0\1\265\72\0\1\266\27\0\1\117\1\120"+
    "\1\117\5\0\4\20\4\0\13\20\1\267\5\20\10\0"+
    "\1\121\12\0\1\117\1\120\1\117\5\0\4\20\4\0"+
    "\3\20\1\270\15\20\10\0\1\121\12\0\1\117\1\120"+
    "\1\117\5\0\4\20\4\0\5\20\1\271\13\20\10\0"+
    "\1\121\12\0\1\117\1\120\1\117\5\0\4\20\4\0"+
    "\12\20\1\272\6\20\10\0\1\121\45\0\1\273\50\0"+
    "\1\274\67\0\1\237\37\0\1\117\1\120\1\117\5\0"+
    "\4\20\4\0\10\20\1\275\10\20\10\0\1\121\12\0"+
    "\1\117\1\120\1\276\5\0\4\20\4\0\2\20\1\277"+
    "\2\20\1\300\3\20\1\301\7\20\10\0\1\121\45\0"+
    "\1\253\53\0\1\302\40\0\1\117\1\120\1\276\17\0"+
    "\1\303\2\0\1\304\3\0\1\305\17\0\1\121\12\0"+
    "\1\117\1\120\1\117\5\0\4\20\4\0\4\20\1\306"+
    "\14\20\10\0\1\121\12\0\1\117\1\120\1\117\5\0"+
    "\4\20\4\0\20\20\1\307\10\0\1\121\12\0\1\117"+
    "\1\120\1\117\5\0\4\20\4\0\16\20\1\310\2\20"+
    "\10\0\1\121\42\0\1\253\57\0\1\311\77\0\1\312"+
    "\61\0\1\313\25\0\1\117\1\120\1\117\5\0\4\20"+
    "\4\0\12\20\1\314\6\20\10\0\1\121\12\0\1\117"+
    "\1\120\1\117\5\0\4\20\4\0\2\20\1\315\16\20"+
    "\10\0\1\121\12\0\1\117\1\120\1\117\5\0\4\20"+
    "\4\0\14\20\1\316\4\20\10\0\1\121\44\0\1\317"+
    "\53\0\1\320\75\0\1\321\27\0\1\117\1\120\1\117"+
    "\5\0\4\20\4\0\17\20\1\322\1\20\10\0\1\121"+
    "\12\0\1\117\1\120\1\117\5\0\4\20\4\0\12\20"+
    "\1\323\6\20\10\0\1\121\12\0\1\117\1\120\1\117"+
    "\5\0\4\20\4\0\10\20\1\324\10\20\10\0\1\121"+
    "\51\0\1\325\56\0\1\326\61\0\1\327\33\0\1\117"+
    "\1\120\1\117\5\0\4\20\4\0\1\330\20\20\10\0"+
    "\1\121\12\0\1\117\1\120\1\117\5\0\4\20\4\0"+
    "\10\20\1\331\10\20\10\0\1\121\32\0\1\332\73\0"+
    "\1\333\33\0\1\117\1\120\1\117\5\0\4\20\4\0"+
    "\11\20\1\334\7\20\10\0\1\121\12\0\1\117\1\120"+
    "\1\117\5\0\4\20\4\0\12\20\1\335\6\20\10\0"+
    "\1\121\43\0\1\336\64\0\1\337\31\0\1\117\1\120"+
    "\1\117\5\0\4\20\4\0\14\20\1\340\4\20\10\0"+
    "\1\121\12\0\1\117\1\120\1\117\5\0\4\20\4\0"+
    "\15\20\1\341\3\20\10\0\1\121\46\0\1\342\64\0"+
    "\1\343\26\0\1\117\1\120\1\117\5\0\4\20\4\0"+
    "\5\20\1\344\13\20\10\0\1\121\37\0\1\345\36\0"+
    "\1\117\1\120\1\117\5\0\4\20\4\0\12\20\1\346"+
    "\6\20\10\0\1\121\44\0\1\347\30\0";

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
     0,  0,  0,  0,  0,  0,  0,  9,  9,  1,  9,  1,  1,  9,  1,  3, 
     1,  1,  1,  3,  9,  1,  9,  3,  3,  3,  3,  3,  3,  3,  3,  3, 
     1,  1,  9,  9,  9,  9,  9,  1,  1,  1,  9,  1,  9,  9,  1,  1, 
     9,  9,  9,  9,  1,  3,  9,  9,  1,  3,  1,  1,  1,  1,  9,  9, 
     9,  9,  1,  9,  9,  9,  3,  1,  9,  1,  9,  0,  0,  0,  0,  0, 
     0,  0,  0,  9,  0,  0,  0,  3,  3,  3,  3,  3,  3,  3,  3,  3, 
     3,  3,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  0, 13,  9, 
     0,  0,  0,  0,  0,  0,  0,  9,  0,  0, 13,  9,  1, 13,  0,  0, 
     2,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  0,  0,  0,  0, 
     0,  0,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  0,  0,  0, 
     0,  0,  3,  3,  3,  3,  3,  3,  3,  0, 13,  0,  0,  3,  3,  3, 
     3,  3,  3,  0,  0,  0,  3,  3,  3,  3,  0,  0,  3,  0,  3,  3, 
     3,  0,  0,  0,  0,  3,  3,  3,  0,  0,  0,  3,  3,  3,  0,  0, 
     0,  3,  3,  3,  0,  9,  0,  3,  3,  0,  0,  3,  3,  0,  0,  3, 
     3,  0,  9,  3,  0,  3,  9
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
	StringBuffer string=new StringBuffer();
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
	private Symbol symbol(int type, String text) {
		return new Symbol(sym.STRING,yyline,yycolumn,
		    new Token(sym.STRING,fileName,text,
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
    int [] trans = new int[8320];
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

        case 15: 
        case 23: 
        case 24: 
        case 25: 
        case 26: 
        case 27: 
        case 28: 
        case 29: 
        case 30: 
        case 31: 
        case 87: 
        case 88: 
        case 90: 
        case 91: 
        case 92: 
        case 93: 
        case 94: 
        case 95: 
        case 96: 
        case 97: 
        case 129: 
        case 130: 
        case 131: 
        case 132: 
        case 133: 
        case 134: 
        case 135: 
        case 136: 
        case 137: 
        case 138: 
        case 139: 
        case 146: 
        case 147: 
        case 148: 
        case 151: 
        case 152: 
        case 155: 
        case 156: 
        case 162: 
        case 163: 
        case 164: 
        case 165: 
        case 167: 
        case 168: 
        case 174: 
        case 176: 
        case 177: 
        case 178: 
        case 183: 
        case 190: 
        case 191: 
        case 192: 
        case 197: 
        case 198: 
        case 199: 
        case 203: 
        case 204: 
        case 205: 
        case 209: 
        case 211: 
        case 215: 
        case 216: 
        case 219: 
        case 220: 
        case 223: 
        case 227: 
          {  return symbol(sym.IDENTIFIER);  }
        case 232: break;
        case 17: 
          {  return symbol(sym.TIDENTIFIER);  }
        case 233: break;
        case 61: 
          {  return symbol(sym.IDENTIFIER);  }
        case 234: break;
        case 98: 
          {  return symbol(sym.MINUSMINUS);  }
        case 235: break;
        case 99: 
          {  return symbol(sym.ARROW_RIGHT);  }
        case 236: break;
        case 68: 
          {  yybegin(STRING); return symbol(sym.PLUS);  }
        case 237: break;
        case 119: 
          {  yybegin(YYINITIAL); return symbol(sym.IMPLIES);  }
        case 238: break;
        case 210: 
        case 213: 
          {  yybegin(USERTYPE); return symbol(sym.TYPE);  }
        case 239: break;
        case 7: 
        case 9: 
        case 14: 
        case 58: 
        case 59: 
        case 60: 
        case 66: 
        case 73: 
          {  System.out.println("unmatched: '"+yytext()+"'");  }
        case 240: break;
        case 74: 
          {  return emptyString();  }
        case 241: break;
        case 229: 
        case 230: 
          {  return symbol(sym.PREDICATE);  }
        case 242: break;
        case 188: 
          {  return symbol(sym.FUNCTION);  }
        case 243: break;
        case 51: 
          {  yybegin(YYINITIAL); Symbol s = symbol(sym.STRING, string.toString()); string.setLength(0); return s;  }
        case 244: break;
        case 44: 
          {  return symbol(sym.QUESTION);  }
        case 245: break;
        case 104: 
          {  return symbol(sym.NOTEQUALS);  }
        case 246: break;
        case 107: 
          {  return symbol(sym.PLUSPLUS);  }
        case 247: break;
        case 108: 
          {  string.append("{");  }
        case 248: break;
        case 48: 
          {  return symbol(sym.AT);  }
        case 249: break;
        case 46: 
          {  return symbol(sym.AND);  }
        case 250: break;
        case 41: 
          {  return symbol(sym.NOT);  }
        case 251: break;
        case 39: 
          {  return symbol(sym.LT);  }
        case 252: break;
        case 16: 
        case 18: 
          {  return symbol(sym.INT);  }
        case 253: break;
        case 12: 
          {  return symbol(sym.DIV);  }
        case 254: break;
        case 10: 
          {  /* ignore */  }
        case 255: break;
        case 8: 
          {  return symbol(sym.LF);  }
        case 256: break;
        case 19: 
          {  return symbol(sym.DOT);  }
        case 257: break;
        case 33: 
          {  return symbol(sym.GT);  }
        case 258: break;
        case 34: 
          {  return symbol(sym.COL);  }
        case 259: break;
        case 57: 
          {  return symbol(sym.LF);  }
        case 260: break;
        case 62: 
          {  return symbol(sym.LT);  }
        case 261: break;
        case 69: 
          {  return symbol(sym.DIV);  }
        case 262: break;
        case 70: 
          {  return symbol(sym.DOT);  }
        case 263: break;
        case 89: 
          {  return symbol(sym.OF);  }
        case 264: break;
        case 100: 
          {  return symbol(sym.GTE);  }
        case 265: break;
        case 101: 
          {  return symbol(sym.LTE);  }
        case 266: break;
        case 123: 
        case 124: 
          {  return symbol(sym.NUM);  }
        case 267: break;
        case 166: 
          {  return symbol(sym.FALSE);  }
        case 268: break;
        case 54: 
        case 56: 
          {  return symbol(sym.CHAR);  }
        case 269: break;
        case 47: 
          {  return symbol(sym.PLUS);  }
        case 270: break;
        case 43: 
          {  return symbol(sym.TUBE);  }
        case 271: break;
        case 13: 
          {  return symbol(sym.MULT);  }
        case 272: break;
        case 22: 
          {  return symbol(sym.COMMA);  }
        case 273: break;
        case 32: 
          {  return symbol(sym.MINUS);  }
        case 274: break;
        case 64: 
          {  return symbol(sym.TUBE);  }
        case 275: break;
        case 71: 
          {  return symbol(sym.MINUS);  }
        case 276: break;
        case 105: 
          {  return symbol(sym.OROR);  }
        case 277: break;
        case 149: 
          {  return symbol(sym.TRUE);  }
        case 278: break;
        case 150: 
          {  return symbol(sym.THIS);  }
        case 279: break;
        case 153: 
          {  return symbol(sym.CASE);  }
        case 280: break;
        case 154: 
          {  return symbol(sym.ELSE);  }
        case 281: break;
        case 122: 
          {  Symbol s = symbol(sym.LAMBDA); yypushback(yylength()); yybegin(LAMBDA); return s;  }
        case 282: break;
        case 50: 
        case 52: 
        case 53: 
          {  string.append(yytext());  }
        case 283: break;
        case 55: 
          {  yybegin(YYINITIAL);  }
        case 284: break;
        case 67: 
          {  Symbol s = symbol(sym.PLUS); yybegin(EMBEDED_EXPRESSION2); yypushback(yylength());  return s;  }
        case 285: break;
        case 11: 
          {  yybegin(STRING);  }
        case 286: break;
        case 170: 
          {  Symbol s = symbol(sym.END); yybegin(YYINITIAL); yypushback(yylength()); return s;  }
        case 287: break;
        case 111: 
          {  Symbol s = symbol(sym.END); yybegin(YYINITIAL); yypushback(yylength()); return s;  }
        case 288: break;
        case 72: 
          {  Symbol s = symbol(sym.RPAREN); yybegin(EMBEDED_EXPRESSION1); yypushback(yylength());  return s;  }
        case 289: break;
        case 110: 
          {  yybegin(EMBEDED_EXPRESSION1); yypushback(yytext().length()); Symbol s = symbol(sym.STRING, string.toString()); string.setLength(0); return s;  }
        case 290: break;
        case 224: 
        case 226: 
          {  return symbol(sym.CHANNEL);  }
        case 291: break;
        case 185: 
          {  return symbol(sym.DECLARE);  }
        case 292: break;
        case 184: 
          {  return symbol(sym.DEFAULT);  }
        case 293: break;
        case 182: 
          {  return symbol(sym.PROCESS);  }
        case 294: break;
        case 175: 
          {  return symbol(sym.RETURN);  }
        case 295: break;
        case 173: 
          {  return symbol(sym.IMPORT);  }
        case 296: break;
        case 49: 
          {  yybegin(CHAR);  }
        case 297: break;
        case 45: 
          {  return symbol(sym.DOLLAR);  }
        case 298: break;
        case 42: 
          {  return symbol(sym.MATCHES);  }
        case 299: break;
        case 40: 
          {  return symbol(sym.ASSIGN);  }
        case 300: break;
        case 38: 
          {  return symbol(sym.RSPAREN);  }
        case 301: break;
        case 37: 
          {  return symbol(sym.LSPAREN);  }
        case 302: break;
        case 36: 
          {  return symbol(sym.RCPAREN);  }
        case 303: break;
        case 20: 
          {  return symbol(sym.RPAREN);  }
        case 304: break;
        case 21: 
          {  return symbol(sym.LPAREN);  }
        case 305: break;
        case 35: 
          {  return symbol(sym.LCPAREN);  }
        case 306: break;
        case 63: 
          {  return symbol(sym.ASSIGN);  }
        case 307: break;
        case 65: 
          {  return symbol(sym.LPAREN);  }
        case 308: break;
        case 83: 
          {  return symbol(sym.DOTDOT);  }
        case 309: break;
        case 102: 
          {  return symbol(sym.IMPLIES);  }
        case 310: break;
        case 103: 
          {  return symbol(sym.EQUALS);  }
        case 311: break;
        case 106: 
          {  return symbol(sym.ANDAND);  }
        case 312: break;
        case 125: 
          {  return symbol(sym.INVDOT);  }
        case 313: break;
        default: 
          if (yy_input == YYEOF && yy_startRead == yy_currentPos) {
            yy_atEOF = true;
            yy_do_eof();
            switch (yy_lexical_state) {
            case USERTYPE:
              {  yybegin(YYINITIAL); return symbol(sym.END);  }
            case 232: break;
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
