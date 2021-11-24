#ifndef BACKENDGENERATOR_H
#define BACKENDGENERATOR_H

#include <AST.hpp>
#include <Utils.hpp>
#include <VariableManager.hpp>


class OutputCodeTemplates
{
public:
    OutputCodeTemplates(VariableManager *variableManager) : variableManager(variableManager)
    {}

    OutputCodeVector createConstant(u64 val, u32 result);
    OutputCodeVector buildIfStmtOutputCode(ExpressionType conditionType, u32 ELSE, u32 temporary, u32 in1, u32 in2);
    OutputCodeVector addOutputCode(u32 result, u32 in1);
    OutputCodeVector subOutputCode(u32 result, u32 in1);
    OutputCodeVector mulOutputCode(u32 result, u32 in1, u32 in2);
    OutputCodeVector divOutputCode(u32 result, u32 in1, u32 in2, u32 multiple, u32 next_multiple, u32 temporary);
    OutputCodeVector modOutputCode(u32 in1_result, u32 in2, u32 multiple, u32 next_multiple, u32 temporary);

private:
    VariableManager *variableManager;
};


#endif
