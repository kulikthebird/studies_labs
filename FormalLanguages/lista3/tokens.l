%{
#include <stdio.h>
#include <string>
#include "parser.h"

using namespace std;
%}


%%

[0-9]+(\.[0-9]+)?([eE][0-9]+)?      { yylval.f = atof(yytext); return NUM;  }
[()]                                { return yytext[0];                     }
\+                                  { return PLUS;                          }
\-                                  { return MINUS;                         }
\*                                  { return STAR;                          }
\/                                  { return DIVIDE;                        }
\^                                  { return POWER;                         }


(#.*)|(#(.*\\\n)+.*\n)              { ; }

\\\n                                { ; }

\n                                  { return ENDLINE; }

[ \t\f\v]                           { ; }

%%

int yywrap() {return -1;}
