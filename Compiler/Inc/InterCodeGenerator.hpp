#ifndef INTERINSTRUCTIONSGENERATOR_H
#define INTERINSTRUCTIONSGENERATOR_H

#include <vector>
#include <Utils.hpp>
#include <AST.hpp>
#include <VariableManager.hpp>

class InterCodeGenerator
{
public:
    InterCodeGenerator(VariableManager *variableManager) : variableManager(variableManager)
    {}
    InterCodeVector buildCommand(std::vector<Command*> commands);

private:
    Reference getValueReference(Identifier *value);
    Operand getValue(InterCodeVector &resultInstr, Identifier *id);
    InterCodeVector buildIf(Operand oper1, Operand oper2, ExpressionType expressionType, InterCodeVector ifBody, InterCodeVector elseBody);
    InterCodeVector handleIf(Condition *condition, InterCodeVector ifBody, InterCodeVector elseBody);
    InterCodeVector handleWhile(Condition *condition, InterCodeVector cmds);
    InterCodeVector handleFor(ForStatement *loop);
    InterCodeVector handleExpression(Expression *assign, Operand &resultVariable);

    VariableManager *variableManager;
    InterCodeVector assignToId(Identifier *id, Operand variableToAssign);
};


#endif // INTERINSTRUCTIONSGENERATOR_H
