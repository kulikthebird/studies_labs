#include <InterCodeGenerator.hpp>
#include <AST.hpp>
#include <Utils.hpp>


Reference InterCodeGenerator::getValueReference(Identifier *value)
{
    Operand operand1;
    Operand operand2;
    u32 currentLine = value->currentLine;
    if(value->tableIndexId != nullptr)
    {
        if(value->tableIndexId->variableName.empty())
        {
            operand1 = operId(variableManager->getTableId(value->variableName, currentLine));
            operand2 = operConst(value->tableIndexId->constant);
        }
        else
        {
            variableManager->assertThatInitialized(value->tableIndexId->variableName, currentLine);
            operand1 = operId(variableManager->getTableId(value->variableName, currentLine));
            operand2 = operVar(variableManager->getVarId(value->tableIndexId->variableName, currentLine));
        }
    }
    else if(value->variableName.empty())
        operand1 = operConst(value->constant);
    else
        operand1 = operVar(variableManager->getVarId(value->variableName, currentLine));
    return {operand1, operand2};
}

Operand InterCodeGenerator::getValue(InterCodeVector &resultInstr, Identifier *id)
{
    Reference ref = getValueReference(id);
    if(ref.refOperand1.operandType == OperandType::VAR)
        variableManager->assertThatInitialized(id->variableName, id->currentLine);
    if(ref.refOperand1.operandType == OperandType::TABLEID)
    {
        Operand resultVariable = operVar(variableManager->addTemporaryVariable());
        insertToVec(resultInstr, {{InterCodeList::LOAD, ref.refOperand1, ref.refOperand2, resultVariable}});
        return resultVariable;
    }
    return ref.refOperand1;
}

InterCodeVector InterCodeGenerator::buildIf(
        Operand oper1, Operand oper2, ExpressionType expressionType, InterCodeVector ifBody, InterCodeVector elseBody)
{
    InterCodeVector resultInstr;
    u32 ELSE, ENDIF;
    variableManager->createLabels({ELSE, ENDIF});
    insertToVec(resultInstr, {{InterCodeList::IF, oper1, oper2, operLabel(ELSE), expressionType}});
    insertToVec(resultInstr, ifBody);
    if(elseBody.size() > 0)
    {
        insertToVec(resultInstr, {{InterCodeList::JUMP, operLabel(ENDIF)}});
        insertToVec(resultInstr, {{InterCodeList::LABEL, operLabel(ELSE)}});
        insertToVec(resultInstr, elseBody);
        insertToVec(resultInstr, {{InterCodeList::LABEL, operLabel(ENDIF)}});
    }
    else
    {
        insertToVec(resultInstr, {{InterCodeList::LABEL, operLabel(ELSE)}});
    }
    return resultInstr;
}

InterCodeVector InterCodeGenerator::handleIf(Condition *condition, InterCodeVector ifBody, InterCodeVector elseBody)
{
    InterCodeVector resultInstr;
    Operand val1;
    Operand val2;
    insertToVec(resultInstr, handleExpression(condition->value1, val1));
    insertToVec(resultInstr, handleExpression(condition->value2, val2));
    insertToVec(resultInstr, buildIf(val1, val2, condition->conditionType, ifBody, elseBody));
    return resultInstr;
}

InterCodeVector InterCodeGenerator::handleWhile(Condition *condition, InterCodeVector cmds)
{
    InterCodeVector resultInstr;
    InterCodeVector whileBody;
    u32 WHILE;
    variableManager->createLabels({WHILE});
    insertToVec(resultInstr, {{InterCodeList::LABEL, operLabel(WHILE)}});
    insertToVec(whileBody, cmds);
    insertToVec(whileBody, {{InterCodeList::JUMP, operLabel(WHILE)}});
    insertToVec(resultInstr, handleIf(condition, whileBody, InterCodeVector()));
    return resultInstr;
}

InterCodeVector InterCodeGenerator::handleFor(ForStatement *loop)
{
    InterCodeVector resultInstr;
    InterCodeVector forBody;
    variableManager->addControlVariableName(loop->index->id->variableName, loop->currentLine);
    variableManager->initializeVariable(loop->index->id->variableName, loop->currentLine);
    Operand indexVariable = getValue(resultInstr, loop->index->id);
    Operand fromValue;
    insertToVec(resultInstr, handleExpression(loop->fromValue, fromValue));
    insertToVec(resultInstr, {{InterCodeList::MOV, indexVariable, fromValue}});
    insertToVec(forBody, buildCommand(loop->forCmds));
    insertToVec(forBody, {{InterCodeList::EXPR,
                           indexVariable, operConst(1), indexVariable,
                           loop->isDown ? ExpressionType::SUB : ExpressionType::ADD}});
    Condition condition = Condition(loop->index, loop->toValue, loop->isDown ? ExpressionType::GE : ExpressionType::LE,
                             loop->currentLine);
    insertToVec(resultInstr, handleWhile(&condition, forBody));
    variableManager->deleteVariableName(loop->index->id->variableName);
    return resultInstr;
}

InterCodeVector InterCodeGenerator::handleExpression(Expression *expression, Operand &resultVariable)
{
    InterCodeVector resultInstr;
    Operand e1, e2;
    if(expression->id != nullptr)
    {
        variableManager->assertThatInitialized(expression->id->variableName, expression->currentLine);
        resultVariable = getValue(resultInstr, expression->id);
        return resultInstr;
    }
    resultVariable = operVar(variableManager->addTemporaryVariable());
    if(expression->expr1->id != nullptr)
    {
        variableManager->assertThatInitialized(expression->expr1->id->variableName, expression->expr1->currentLine);
        e1 = getValue(resultInstr, expression->expr1->id);
    }
    else
    {
        e1 = operVar(variableManager->addTemporaryVariable());
        insertToVec(resultInstr, handleExpression(expression->expr1, e1));
    }
    if(expression->expr2->id != nullptr)
    {
        variableManager->assertThatInitialized(expression->expr2->id->variableName, expression->expr2->currentLine);
        e2 = getValue(resultInstr, expression->expr2->id);
    }
    else
    {
        e2 = operVar(variableManager->addTemporaryVariable());
        insertToVec(resultInstr, handleExpression(expression->expr2, e2));
    }
    insertToVec(resultInstr, {{InterCodeList::EXPR, e1, e2, resultVariable, expression->operatorType}});
    return resultInstr;
}

InterCodeVector InterCodeGenerator::buildCommand(std::vector<Command*> commands)
{
    InterCodeVector resultInstr;
    for(auto current : commands)
    {
        switch(current->cmd->nodeType)
        {
        case NodeType::IF:
        {
            auto ifStmt = (IfStatement*)(current->cmd);
            insertToVec(resultInstr, handleIf(ifStmt->condition, buildCommand(ifStmt->ifCmds), buildCommand(ifStmt->elseCmds)));
        }
        break;
        case NodeType::WHILE:
        {
            auto whileStmt = (WhileStatement*)(current->cmd);
            insertToVec(resultInstr, handleWhile(whileStmt->condition, buildCommand(whileStmt->whileCmds)));
        }
        break;
        case NodeType::FOR:
            insertToVec(resultInstr, handleFor((ForStatement*)(current->cmd)));
        break;
        case NodeType::GET:
        {
            auto getStmt = (GetStatement*)current->cmd;
            Operand tempVar = operVar(variableManager->addTemporaryVariable());
            insertToVec(resultInstr, {{InterCodeList::READ, tempVar}});
            insertToVec(resultInstr, assignToId(getStmt->id, tempVar));
        }
        break;
        case NodeType::PUT:
        {
            auto putStmt = (PutStatement*)current->cmd;
            Operand resultVariable;
            insertToVec(resultInstr, handleExpression(putStmt->expr, resultVariable));
            insertToVec(resultInstr, {{InterCodeList::WRITE, resultVariable}});
        }
        break;
        case NodeType::ASSIGN:
        {
            Operand resultVariable;
            auto assign = (AssignStatement*) current->cmd;
            insertToVec(resultInstr, handleExpression(assign->expression, resultVariable));
            insertToVec(resultInstr, assignToId(assign->id, resultVariable));
        }
        break;
        }
    }
    return resultInstr;
}

InterCodeVector InterCodeGenerator::assignToId(Identifier *id, Operand variableToAssign)
{
    InterCodeVector resultInstr;
    Reference targetOperand = getValueReference(id);
    if(targetOperand.refOperand1.operandType == OperandType::TABLEID)
        insertToVec(resultInstr, {{InterCodeList::STORE, targetOperand.refOperand1, targetOperand.refOperand2, variableToAssign}});
    else
        insertToVec(resultInstr, {{InterCodeList::MOV, targetOperand.refOperand1, variableToAssign}});
    variableManager->initializeVariable(id->variableName, id->currentLine);
    return resultInstr;
}
