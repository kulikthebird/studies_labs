%{
#include <stdio.h>
#include <string>
#include "parser.h"

using namespace std;
%}


%%

[0-9]+(\.[0-9]+)?([eE][0-9]+)?      { yylval.f = atof(yytext); return NUM;  }
[();]                               { return yytext[0];                     }
\+                                  { return PLUS;                          }
\-                                  { return MINUS;                         }
\*                                  { return STAR;                          }
\\                                  { return DIVIDE;                        }
\*\*                                { return POWER;                         }
=                                   { return EQU;                           }
\^                                  { return BITOPERATOR;                   }
print                               { return PRINT;                         }

if                                  { return IF;     }
then                                { return THEN;   }
endif                               { return ENDIF;  }
else                                { return ELSE;   }
elseif                              { return ELSEIF; }

\!                                  { return NEGATIVE;        }
\<|\>|==                            { return IFOPERATOR;      }
&&|\|\|                             { return LOGICALOPERATOR; }

[a-zA-Z]+[a-zA-Z0-9]*               {   int len = (new string(yytext))->length();
                                        yylval.s = new char[len+1];
                                        memcpy(yylval.s, yytext, len);
                                        yylval.s[len] = 0;
                                        return VARIABLE; }

[ \t\f\v\n]                         { ; }

%%

int yywrap() {return -1;}
