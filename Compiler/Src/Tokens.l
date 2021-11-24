%{
#include <stdio.h>
#include <string>
#include <iostream>
#include <CompilerCore.hpp>
#include <Utils.hpp>
#include <Parser.hpp>

extern unsigned int currentLine;

%}
%x IN_COMMENT
%%


"["                 BEGIN(IN_COMMENT);
<IN_COMMENT>
{
    "]"     BEGIN(INITIAL);
    [^\]]+      // eat comment in chunks
}


[_a-z]+    { yylval.name = new std::string(yytext, yyleng); return pidentifier; }
[0-9]+     { yylval.n = atof(yytext); return num;      }

[(]        { return T_OPENPARENT; }
[)]        { return T_CLOSEPARENT;}
":="       { return T_ASSIGN; }
";"        { return T_INSTREND; }

\+         { yylval.oper = ExpressionType::ADD; return T_PLUS; }
\-         { yylval.oper = ExpressionType::SUB; return T_MINUS; }
\*         { yylval.oper = ExpressionType::MUL; return T_STAR; }
\/         { yylval.oper = ExpressionType::DIV; return T_DIVIDE; }
\%         { yylval.oper = ExpressionType::MOD; return T_MODULO; }
"="        { yylval.compar = ExpressionType::EQ; return T_EQ; }
"!="       { yylval.compar = ExpressionType::NE; return T_NE; }
"<"        { yylval.compar = ExpressionType::LT; return T_LT; }
">"        { yylval.compar = ExpressionType::GT; return T_GT; }
"<="       { yylval.compar = ExpressionType::LE; return T_LE; }
">="       { yylval.compar = ExpressionType::GE; return T_GE; }

"IF"       { return T_IF; }
"THEN"     { return T_THEN; }
"ENDIF"    { return T_ENDIF; }
"ELSE"     { return T_ELSE; }
"WHILE"    { return T_WHILE; }
"DO"       { return T_DO; }
"ENDWHILE" { return T_ENDWHILE; }
"ENDFOR"   { return T_ENDFOR; }
"FOR"      { return T_FOR; }
"FROM"     { return T_FROM; }
"TO"       { return T_TO; }
"END"      { return T_END; }
"DOWN"     { return T_DOWN; }
"GET"      { return T_GET; }
"PUT"      { return T_PUT; }
"DECLARE"  { return T_DECLARE; }
"IN"       { return T_IN; }

[ \t\f\v]  { ; }
[\n]       { currentLine++; }

.    { return SYNTAX_ERROR; }


%%

int yywrap() {return -1;}

