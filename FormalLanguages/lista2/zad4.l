%{
#include <stdio.h>
#include <string>
#include <exception>

using namespace std;

int word_count = 0;
int stack[128];
int ptr = 0;

int pow(int a, int n)
{
    int result = 1;
    for(int i=0; i<n; i++)
        result *=a;
    return result;
}

void push(int number)
{
    stack[ptr] = number;
    ptr++;
}

int pop()
{
    if(ptr == 0)
    {
        printf("\nZa malo argumentow!\n");
        throw exception();
    }
    ptr--;
    return stack[ptr];
}

%}

%x COMPUTING

%%


[ \t]*      { ; }
[0-9]+      {push(atoi(yytext)); printf("%s ", yytext);}
\-[0-9]+    {push(atoi(yytext)); printf("%s ", yytext);}
\+          {int a=pop(); int b=pop(); push(b+a); printf("%s ", yytext);}
\-          {int a=pop(); int b=pop(); push(b-a); printf("%s ", yytext);}
\*          {int a=pop(); int b=pop(); push(b*a); printf("%s ", yytext);}
\/          {int a=pop(); int b=pop(); if(a == 0) {printf("\nDzielenie przez 0!\n"); throw exception();} push(b/a); printf("%s ", yytext);}
%           {int a=pop(); int b=pop(); if(a == 0) {printf("\nDzielenie modulo przez 0!\n"); throw exception();} push(b%a); printf("%s ", yytext);}
\^          {int a=pop(); int b=pop(); push(pow(b, a)); printf("%s ", yytext);}
[\n]        { if(ptr>1) {printf("\nZa malo operatorow!\n"); throw exception();} printf("\n = %d\n", pop());}

%%

int main()
{
    try {
    yylex();
    }
    catch(...)
    {}
    return 0;
}

int yywrap() {return -1;}
