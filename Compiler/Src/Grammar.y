%{
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <vector>
#include <CompilerCore.hpp>
#include <AST.hpp>


unsigned int currentLine = 1;
extern int yylex();
void yyerror(const char *s) { printf("\nBlad skladni: %s w linii %d.\n", s, currentLine); }
CompilerCore core;
%}

%union
{
    unsigned long long n;
    std::string *name;

    std::vector<Command*>* cmds;
    std::vector<Declaration*>* declar;

    Declaration*     vdecl;
    Node*            node_id;
    Identifier*      idnf;
    Expression*      expres;
    Condition*       condit;
    ExpressionType   oper;
    ExpressionType   compar;
    IfStatement*     ifstat;
    WhileStatement*  whilestat;
    ForStatement*    forstat;
    GetStatement*    getstat;
    PutStatement*    putstat;
    AssignStatement* assignstat;
}

%token <n> num
%token <name> pidentifier

%type <idnf> identifier
%type <expres> expression
%type <condit> condition
%type <declar> vdeclarations
%type <vdecl> vdeclaration
%type <cmds> commands
%type <idnf> value
%type <node_id> command
%type <compar> comparators

%token SYNTAX_ERROR
%token T_IF T_THEN T_ENDIF T_ELSE T_WHILE T_DO
%token T_ENDWHILE T_FOR T_FROM T_TO T_ENDFOR T_DOWN
%token T_GET T_PUT T_DECLARE T_IN T_END
%token<oper> T_PLUS T_MINUS T_STAR T_DIVIDE T_MODULO T_INSTREND T_ASSIGN
%token<compar> T_EQ T_NE T_LT T_GT T_LE T_GE
%token T_OPENPARENT T_CLOSEPARENT

%left  T_MINUS T_PLUS
%left  T_STAR T_DIVIDE
%right T_MODULO

%start program

%%

program : T_DECLARE vdeclarations T_IN commands T_END { core.compileProgram(*$2, *$4); }
    ;

vdeclarations : vdeclaration { $$ = new std::vector<Declaration*>(); $$->push_back($1); }
    | vdeclarations vdeclaration { $1->push_back($2); }
    | {}
    ;

vdeclaration : pidentifier { $$ = new Declaration(*$1, currentLine); }
    | pidentifier T_OPENPARENT num T_CLOSEPARENT { $$ = new Declaration(*$1, $3, currentLine); }
    ;

commands : command { $$ = new std::vector<Command*>(); $$->push_back(new Command($1, currentLine)); }
    | commands command { $1->push_back(new Command($2, currentLine)); }
    | {}
    ;

command : identifier T_ASSIGN expression T_INSTREND { $$ = new AssignStatement($1, $3, currentLine); }
    | T_IF condition T_THEN commands T_ENDIF { $$ = new IfStatement($2, *$4, currentLine); }
    | T_IF condition T_THEN commands T_ELSE commands T_ENDIF { $$ = new IfStatement($2, *$4, *$6, currentLine); }
    | T_WHILE condition T_DO commands T_ENDWHILE { $$ = new WhileStatement($2, *$4, currentLine); }
    | T_GET identifier T_INSTREND { $$ = new GetStatement($2, currentLine); }
    | T_PUT expression T_INSTREND { $$ = new PutStatement($2, currentLine); }
    | T_FOR pidentifier T_FROM expression T_TO expression T_DO commands T_ENDFOR
        { $$ = new ForStatement(*$2, $4, $6, *$8, false, currentLine); }
    | T_FOR pidentifier T_DOWN T_FROM expression T_TO expression T_DO commands T_ENDFOR
        { $$ = new ForStatement(*$2, $5, $7, *$9, true, currentLine); }
    ;

expression : value { $$ = new Expression($1, currentLine); }
    | expression T_PLUS expression { $$ = new Expression($1, $3, $2, currentLine); }
    | expression T_MINUS expression { $$ = new Expression($1, $3, $2, currentLine); }
    | expression T_STAR expression { $$ = new Expression($1, $3, $2, currentLine); }
    | expression T_DIVIDE expression { $$ = new Expression($1, $3, $2, currentLine); }
    | expression T_MODULO expression { $$ = new Expression($1, $3, $2, currentLine); }
    | T_OPENPARENT expression T_CLOSEPARENT { $$ = $2; }
    ;

condition : expression comparators expression { $$ = new Condition($1, $3, $2, currentLine); }
    ;

value : num      { $$ = new Identifier($1, currentLine); }
    | identifier { $$ = $1; }
    ;

identifier : pidentifier { $$ = new Identifier(*$1, currentLine); }
    | pidentifier T_OPENPARENT pidentifier T_CLOSEPARENT { $$ = new Identifier(*$1, *$3, currentLine); }
    | pidentifier T_OPENPARENT num T_CLOSEPARENT { $$ = new Identifier(*$1, $3, currentLine); }
    ;

comparators : T_EQ | T_NE | T_LT | T_GT | T_LE | T_GE ;

%%
