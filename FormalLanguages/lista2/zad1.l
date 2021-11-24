%{
#include <stdio.h>
#include <string>

using namespace std;

int word_count = 0;

%}


%%

^[ \t\n]+   {;}
[^ \t\n]+   {++word_count; printf("%s", yytext);}
[\n]*     {printf("\n"); }
[\t]*     {printf("\t"); }
[ ]*     {printf(" "); }


%%

int main()
{
    yylex();
    printf("\n");
    printf("\nwords amount: %d\n", word_count);
    return 0;
}

int yywrap() {return -1;}
