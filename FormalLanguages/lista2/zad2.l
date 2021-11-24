%{
#include <stdio.h>
#include <string>

using namespace std;

int yyerror(const char* err);

%}

%x IN_COMMENT

%%
<INITIAL>{
"/*"              BEGIN(IN_COMMENT);
}
<IN_COMMENT>{
"*/"      BEGIN(INITIAL);
[^*\n]+   // eat comment in chunks
"*"       // eat the lone star
\n        yylineno++;
} { ; }

"//".*[\n]        { ; }


%%

int main()
{
    yylex();
    return 0;
}

int yywrap() {return -1;}
