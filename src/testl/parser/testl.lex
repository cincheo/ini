
/*
 * This source code file is the exclusive property of its author. No copy or 
 * usage of the source code is permitted unless the author contractually 
 * allows it under the terms of a well-defined agreement.
 */

package testl.parser;

import java_cup.runtime.*;
import ini.ast.Token;

%%

%class TestlScanner
%unicode
%cup
%line
%column
%state BBLOCK
%state BLOCK
%state STRING
%state CHAR

%{
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
	//private Symbol symbol(int type,Object value) {
	//	return new Symbol(type,yyline,yycolumn,value);
	//}
%}	

LineTerminator= \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace = {LineTerminator} | [ \t\f]
WhiteSpaceChar = [ \t\f]
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}

StringText=(\\\"|[^\n\"]|\\{WhiteSpaceChar}*\\)*

TraditionalComment = "/*" [^*] ~"*/"
EndOfLineComment = "//" {InputCharacter}* {LineTerminator}
DocumentationComment = "/**" {CommentContent} "*/"
CommentContent = ( [^*] | \*+ [^/*] )*

Identifier = [a-z][A-Za-z0-9_]*

TypeIdentifier = [A-Z][A-Za-z0-9_]*

DecIntegerLiteral = [0-9] | [1-9][0-9]*
DecFloatLiteral = {DecIntegerLiteral}\.{DecIntegerLiteral}

%% 

<YYINITIAL> {
  /* keywords */
  {DecFloatLiteral}		{ return symbol(sym.NUM); }
  {Identifier}          { return symbol(sym.IDENTIFIER); }
  "+"                   { return symbol(sym.PLUS); }
  "-"                   { return symbol(sym.MINUS); }
  "/"                   { return symbol(sym.DIV); }
  "*"                   { return symbol(sym.MULT); }
  "("                   { return symbol(sym.LPAREN); }
  ")"                   { return symbol(sym.RPAREN); }
  {Comment}             { /* ignore */ }
  {WhiteSpace}          { /* ignore */ }
}

.|\n { System.out.println("unmatched:"+yytext()); }
