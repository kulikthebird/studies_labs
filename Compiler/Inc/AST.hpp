#ifndef AST_H
#define AST_H

#include <Utils.hpp>
#include <string>



class Node
{
public:
    Node(u32 currentLine, NodeType nodeType)
    : currentLine(currentLine), nodeType(nodeType)
    {}
    u32 currentLine;
    const NodeType nodeType;
};

class Identifier : public Node
{
public:
    Identifier(std::string variableName, u32 currentLine)
    : variableName(variableName), Node(currentLine, NodeType::IDENTIFIER)
    {}
    Identifier(std::string variableName, u32 num, u32 currentLine)
    : variableName(variableName), tableIndexId(new Identifier(num, currentLine)), Node(currentLine, NodeType::IDENTIFIER)
    {}
    Identifier(std::string variableName, std::string index, u32 currentLine)
    : variableName(variableName), tableIndexId(new Identifier(index, currentLine)), Node(currentLine, NodeType::IDENTIFIER)
    {}
    Identifier(u32 constant, u32 currentLine)
    : constant(constant), Node(currentLine, NodeType::IDENTIFIER)
    {}

    Identifier *tableIndexId = nullptr;
    std::string variableName;
    u32 constant;
};

class Expression : public Node
{
public:
    Expression(Identifier *id, u32 currentLine = 0)
        : id(id), Node(currentLine, NodeType::EXPRESSION)
    {}
    Expression(Expression *expr1, Expression *expr2, ExpressionType op, u32 currentLine)
        : expr1(expr1), expr2(expr2), operatorType(op), Node(currentLine, NodeType::EXPRESSION)
    {}

    Expression *expr1 = nullptr;
    Expression *expr2 = nullptr;
    Identifier *id = nullptr;
    ExpressionType operatorType = ExpressionType::NONE;
};


class Condition : public Node
{
public:
    Condition(Expression *value1, Expression *value2, ExpressionType conditionType, u32 currentLine)
    : value1(value1), value2(value2), conditionType(conditionType), Node(currentLine, NodeType::CONDITION)
    {}

    Expression *value1;
    Expression *value2;
    ExpressionType conditionType;
};


class Declaration : public Node
{
public:
    Declaration(std::string variableName, u32 currentLine)
    : variableName(variableName), Node(currentLine, NodeType::Declaration), isTable(false)
    {}

    Declaration(std::string tableName, u32 size, u32 currentLine)
    : variableName(tableName), size(size), Node(currentLine, NodeType::Declaration), isTable(true)
    {}

    std::string variableName;
    u32 size = 0;
    bool isTable;
};

class Command : public Node
{
public:
    Command(Node *cmd, u32 currentLine)
    : cmd(cmd), Node(currentLine, NodeType::COMMANDS)
    {}

    Node *cmd;
};

class IfStatement : public Node
{
public:
    IfStatement(Condition *condition, std::vector<Command*> ifCmds, u32 currentLine)
    : condition(condition), ifCmds(ifCmds),Node(currentLine, NodeType::IF)
    {}
    IfStatement(Condition *condition, std::vector<Command*> ifCmds, std::vector<Command*> elseCmds, u32 currentLine)
    : condition(condition), ifCmds(ifCmds), elseCmds(elseCmds), Node(currentLine, NodeType::IF)
    {}

    std::vector<Command*> ifCmds;
    std::vector<Command*> elseCmds;
    Condition *condition;
};

class WhileStatement : public Node
{
public:
    WhileStatement(Condition *condition, std::vector<Command*> whileCmds, u32 currentLine)
    : condition(condition), whileCmds(whileCmds), Node(currentLine, NodeType::WHILE)
    {}
    Condition *condition;
    std::vector<Command*> whileCmds;
};

class ForStatement : public Node
{
public:
    ForStatement(std::string index, Expression *fromValue, Expression *toValue, std::vector<Command*> forCmds, bool isDown, u32 currentLine)
    : index(new Expression(new Identifier(index, currentLine))), fromValue(fromValue), toValue(toValue), forCmds(forCmds), isDown(isDown), Node(currentLine, NodeType::FOR)
    {}

    Expression *index;
    Expression *fromValue;
    Expression *toValue;
    std::vector<Command*> forCmds;
    bool isDown;
};

class GetStatement : public Node
{
public:
    GetStatement(Identifier *id, u32 currentLine)
    : id(id), Node(currentLine, NodeType::GET)
    {}

    Identifier *id;
};

class PutStatement : public Node
{
public:
    PutStatement(Expression *expr, u32 currentLine)
    : expr(expr), Node(currentLine, NodeType::PUT)
    {}

    Expression *expr;
};

class AssignStatement : public Node
{
public:
    AssignStatement(Identifier *id, Expression *expression, u32 currentLine)
    : id(id), expression(expression), Node(currentLine, NodeType::ASSIGN)
    {}

    Identifier *id;
    Expression *expression;
};

#endif
