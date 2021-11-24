%{
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <cassert>

using namespace std;

extern int yylex();
void yyerror(const char *s) { printf("\nBlad.\n"); }

unsigned int pow(unsigned int a, unsigned int t)
{
    unsigned int result = 1;
    unsigned int n = 1234577;
    while(t > 0)
    {
        if(t & 1 == 1)
            result *= a;
        a *= a;
        t >>= 1;
        a %= n;
        result %= n;
    }
    return result;
}

unsigned int changetoZ(long long int m)
{
    unsigned int n = 1234577;
    if(m >= 0)
        return m % n;
    return ((-m/n) + 1)*n + m;
}

unsigned int inverseZ(unsigned int a)
{
    unsigned int b = 1234577;
    unsigned int a0 = a;
    unsigned int b0 = b;

    unsigned int p = 1, q = 0;
    unsigned int r = 0, s = 1;

    while (b != 0)
    {
        unsigned int c = a % b;
        unsigned int quot = a/b;
        a = b;
        b = c;
        unsigned int r_tmp = r;
        unsigned int s_tmp = s;
        r = p - quot * r;
        s = q - quot * s;
        p = r_tmp; q = s_tmp;
    }
    return changetoZ(p);
}

%}

%union {
    unsigned long long f;
}

%token <f> NUM
%token PLUS MINUS STAR DIVIDE POWER ENDLINE SOFTBREAKLN
%type <f> EXPRESSION SETOFEXPRESSIONS

%left MINUS PLUS
%left STAR DIVIDE
%precedence NEG
%right POWER

%%

S   : SETOFEXPRESSIONS
    ;

SETOFEXPRESSIONS : SETOFEXPRESSIONS EXPRESSION ENDLINE          { printf("\nWynik: %d\n", $2); }
    | EXPRESSION ENDLINE                                        { printf("\nWynik: %d\n", $1); }
    | SETOFEXPRESSIONS ENDLINE
    | ENDLINE
    ;

EXPRESSION  :
     NUM                                        { $$ = $1;  printf("%d ", $1);                                                  }
    | MINUS NUM %prec NEG                       { $$ = changetoZ(-$2); printf("%d ", changetoZ(-$2));                           }
    | MINUS EXPRESSION %prec NEG                { $$ = changetoZ(-$2); printf("%d ", changetoZ(-$2));                           }
    | EXPRESSION PLUS EXPRESSION                { $$ = ($1 + $3) % 1234577; printf("+ ");                                       }
    | EXPRESSION MINUS EXPRESSION               { $$ = changetoZ($1 - $3);     printf("- ");                                    }
    | EXPRESSION STAR EXPRESSION                { $$ = (($1 % 1234577) * ($3 % 1234577)) % 1234577; printf("* ");               }
    | EXPRESSION DIVIDE EXPRESSION              { $$ = (($1 % 1234577) * inverseZ($3)) % 1234577; printf("/ ");                 }
    | EXPRESSION POWER EXPRESSION               { $$ = pow(changetoZ($1), changetoZ($3)); printf("^ ");                         }
    | '(' EXPRESSION ')'                        { $$ = changetoZ($2);                                                           }
    ;

%%

int main()
{
    yyparse();
    return 0;
}
