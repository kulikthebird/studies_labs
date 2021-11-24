%{
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <cassert>
#include <map>
extern int yylex();
void yyerror(const char *s) { printf("ERROR: %s, %s\n", s); }

using namespace std;
map<string, float> variables;

float pow(float x, float n)
{
    if(n == 1)
        return x;
    return pow(x, n-1) * x;
}

%}


%union {
    float f;
    char* s;
}


%token <f> NUM
%token POWER PLUS MINUS STAR DIVIDE BITOPERATOR
%type <f> ADDSUB MULDIV POW PARENTHESIS EXPRESSION BITOPERATION


%token EQU
%token <s> VARIABLE
%type  <f> ASSIGN

%token PRINT

%token IF THEN ENDIF ELSEIF ELSE
%token NEGATIVE IFOPERATOR LOGICALOPERATOR
%type <f> CONDITION SETOFSUBCONDITIONS SUBCONDITION IFPARENTHESIS


%%

S : SETOFINSTRUCTIONS        { printf("End of compiling\n");}
  ;


SETOFINSTRUCTIONS : SETOFINSTRUCTIONS INSTRUCTION
  |                 INSTRUCTION
  ;


IFSTATEMENT: IF CONDITION THEN SETOFINSTRUCTIONS ENDIF
  ;


INSTRUCTION : ASSIGN ';'
  |           PRINT VARIABLE ';'                 { printf("variable %s: %f\n", $2, variables[$2]); }
  |                 IFSTATEMENT
  ;


ASSIGN : VARIABLE EQU EXPRESSION        { variables[$1] = $3; printf("assign %f to %s\n", $3, $1); }
  |      VARIABLE EQU VARIABLE          { variables[$1] = variables[$3]; }
  ;





EXPRESSION :      ADDSUB                {$$ = $1;}
  ;

ADDSUB : ADDSUB PLUS   MULDIV           {$$ = $1 + $3; }
  |      ADDSUB MINUS  MULDIV           {$$ = $1 - $3; }
  |      MULDIV                         {$$ = $1; }
  ;

MULDIV : MULDIV STAR POW                {$$ = $1 * $3; }
  |      MULDIV DIVIDE POW              {$$ = $1 / $3; }
  |      POW                            {$$ = $1; }
  ;

POW :    POW POWER BITOPERATION             {$$ = (float)pow((double)$1, (double)$3); }
  |      BITOPERATION                       {$$ = $1; }
  ;

BITOPERATION : PARENTHESIS BITOPERATOR PARENTHESIS      {$$ = $1 * $3; }
  |      PARENTHESIS
  ;

PARENTHESIS : '(' ADDSUB ')'                 {$$ = $2;  }
  |      MINUS PARENTHESIS                   {$$ = -$2; }
  |      NUM                                 {$$ = $1;  }
  |      VARIABLE                            {$$ = variables[$1]; }
  ;


CONDITION :      SETOFSUBCONDITIONS                                {$$ = $1;}
  ;

SETOFSUBCONDITIONS : SUBCONDITION LOGICALOPERATOR SUBCONDITION     {$$ = $1 + $3; }
  |      SUBCONDITION                                              {$$ = $1; }
  |      NEGATIVE SUBCONDITION                                     {$$ = (-1)*$2; }
  ;

SUBCONDITION : PARENTHESIS IFOPERATOR PARENTHESIS      {$$ = $1 * $3; }
  |      IFPARENTHESIS
  ;

IFPARENTHESIS : '(' SUBCONDITION ')'            {$$ = $2; }
  | EXPRESSION                                  {$$ = $1; }
  ;



%%

int main()
{
    yyparse();
    return 0;
}
