#ifndef UTILS_H
#define UTILS_H

#include <string>
#include <vector>
#include <exception>
#include <stdexcept>

typedef unsigned long long u64;
typedef unsigned int       u32;
enum class ExpressionType {NONE, ADD, SUB, MUL, DIV, MOD, EQ, LT, GT, LE, GE, NE};
enum class OperandType {VAR, CONST, TABLEID, LABELID};

enum class OutCode
{
    READ,
    WRITE,
    LOAD,
    STORE,
    COPY,
    ADD,
    SUB,
    SHR,
    SHL,
    INC,
    DEC,
    RESET,
    JUMP,
    JZERO,
    JODD,
    HALT,
    LABEL
};

enum class InterCodeList
{
    MOV,
    STORE,
    LOAD,
    EXPR,
    IF,
    JUMP,
    HALT,
    READ,
    WRITE,
    LABEL
};

enum class NodeType
{
    IDENTIFIER,
    EXPRESSION,
    CONDITION,
    Declaration,
    COMMANDS,
    IF,
    WHILE,
    FOR,
    GET,
    PUT,
    ASSIGN
};

struct OutputCode
{
    OutCode id;
    u32 a1;
    u32 a2;
};

struct Operand
{
    union
    {
        u32 variable;
        u32 constant;
        u32 label;
        u32 tableId;
    };
    OperandType operandType;
};

struct InterCode
{
    InterCodeList cmd;
    Operand a1;
    Operand a2;
    Operand a3;
    ExpressionType expressionType;
};

struct Reference
{
    Operand refOperand1;
    Operand refOperand2;
};

typedef std::vector<OutputCode> OutputCodeVector;
typedef std::vector<InterCode> InterCodeVector;

template <typename T>
inline void insertToVec(std::vector<T> &mainVector, std::vector<T> vectorToInsert)
{
    mainVector.insert(mainVector.end(), vectorToInsert.begin(), vectorToInsert.end());
}

inline Operand operConst(u32 value)
{
    return {value, OperandType::CONST};
}

inline Operand operVar(u32 value)
{
    return {value, OperandType::VAR};
}

inline Operand operId(u32 value)
{
    return {value, OperandType::TABLEID};
}

inline Operand operLabel(u32 value)
{
    return {value, OperandType::LABELID};
}

#define CreateException(NAME) class NAME : public std::logic_error \
{ \
public: \
    NAME(std::string const& msg, u32 line) : std::logic_error(msg), line(line) {} \
    u32 line; \
};
CreateException(RedeclarationException)
CreateException(VariableNotExistException)
CreateException(TableNotExistException)
CreateException(WrongSizeException)
CreateException(VariableNotInitialized)
CreateException(UsedControlVariableException)

#endif // UTILS_H
