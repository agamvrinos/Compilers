import java_cup.runtime.*;

%%

%public
%class Scanner

%unicode

%line
%column

%cup
%cupdebug

%{
  StringBuilder string = new StringBuilder();

  private Symbol symbol(int type) {
    return new Symbol(type, yyline+1, yycolumn+1);
  }

  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline+1, yycolumn+1, value);
  }

%}

/* main character classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

WhiteSpace = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} |
          {DocumentationComment}

TraditionalComment = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment = "//" {InputCharacter}* {LineTerminator}?
DocumentationComment = "/*" "*"+ [^/*] ~"*/"

/* identifiers */
Identifier = [:jletter:][:jletterdigit:]*

RPAREN_LBRACE = \){WhiteSpace}*\{

/* string literals */
StringCharacter = [^\r\n\"\\]

%state STRING

%%

<YYINITIAL> {

  /* keywords */
  "in"                           { return symbol(sym.IN); }
  "if"                           { return symbol(sym.IF); }
  "else"                         { return symbol(sym.ELSE); }

  /* separators */
  "("                            { return symbol(sym.LPAREN); }
  ")"                            { return symbol(sym.RPAREN); }
  "{"                            { return symbol(sym.LBRACE); }
  "}"                            { return symbol(sym.RBRACE); }
  ","                            { return symbol(sym.COMMA); }

  /* operators */
  "="                            { return symbol(sym.EQ); }
  "+"                            { return symbol(sym.PLUS); }

  /* string literal */
  \"                             { yybegin(STRING); string.setLength(0); }


  /* comments */
  {Comment}                      { /* ignore */ }

  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }

  /* identifiers */
  {Identifier}                   { return symbol(sym.IDENTIFIER, yytext()); }
}

<STRING> {
  \"                             { yybegin(YYINITIAL); return symbol(sym.STRING_LITERAL, string.toString()); }

  {StringCharacter}+             { string.append( yytext() ); }

  {LineTerminator}               { throw new RuntimeException("Unterminated string at end of line"); }
}

{RPAREN_LBRACE} 				 { return symbol(sym.RPAREN_LBRACE); }


/* error fallback */
[^]                              { throw new RuntimeException("Illegal character \""+yytext()+
                                                              "\" at line "+yyline+", column "+yycolumn); }
// <<EOF>>                          { return symbol(sym.EOF); }
