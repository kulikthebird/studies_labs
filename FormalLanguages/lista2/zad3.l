%{
#include <stdio.h>
#include <string>

using namespace std;

int yyerror(const char* err);

%}


%x IN_COMMENT_JAVADOC
%x IN_COMMENT_DEFAULT
%x IN_COMMENT

%%
<INITIAL>{
"/*"              { BEGIN(IN_COMMENT);}
} { ; }

<IN_COMMENT>{
"*"               { printf("/**"); BEGIN(INITIAL); }
[^*]              BEGIN(IN_COMMENT_DEFAULT);
}  { ;  }

<IN_COMMENT_DEFAULT>{
"*/"      BEGIN(INITIAL);
[^*\n]+   // eat comment in chunks
"*"       // eat the lone star
\n        yylineno++;
} { ; }

<IN_COMMENT_JAVADOC>{
"*/"      BEGIN(INITIAL);
[^*\n]+   // eat comment in chunks
"*"       // eat the lone star
\n        yylineno++;
} { ;}

"//".*[\n]        { ; }


%%

int main()
{
    yylex();
    return 0;
}

int yywrap() {return -1;}
