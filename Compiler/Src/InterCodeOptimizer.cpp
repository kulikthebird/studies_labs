#include <InterCodeOptimizer.hpp>
#include <map>
#include <list>


void InterCodeOptimizer::assignToVariable(u32 variableId, u32 currentPosition)
{
    if(variables.find(variableId) == variables.end())
    {
        if(variables[variableId].back().end != currentPosition)
            variables[variableId].push_back({currentPosition, currentPosition});
    }
    else
        variables[variableId].back().end = currentPosition;
}

void InterCodeOptimizer::useVariable(u32 variableId, u32 currentPosition)
{
    variables[variableId].back().end = currentPosition;
}

void InterCodeOptimizer::checkVariablesRange(InterCodeVector &instructions)
{
    u32 currentPosition = 0;
    for(auto &instr : instructions)
    {
//        switch(instr.cmd)
//        {
//        case InterCodeList::MOV:
//            useVariable(instr.a1, currentPosition);
//            assignToVariable(instr.a2, currentPosition);
//            break;
//        case InterCodeList::READ:
//            assignToVariable(instr.a1, currentPosition);
//            break;
//        case InterCodeList::WRITE:
//            useVariable(instr.a1, currentPosition);
//            break;
//        case InterCodeList::STORE:  // TODO: czy const czy variable index jest to samo w LOAD
//            useVariable(instr.a2, currentPosition);
//            useVariable(instr.a3, currentPosition);
//            break;
//        case InterCodeList::LOAD:
//            useVariable(instr.a1, currentPosition);
//            assignToVariable(instr.a2, currentPosition);
//            break;
//        case InterCodeList::EXPR:
//            useVariable(instr.a1, currentPosition);
//            useVariable(instr.a2, currentPosition);
//            assignToVariable(instr.a3, currentPosition);
//            break;
//        case InterCodeList::IF:
//            useVariable(instr.a1, currentPosition);
//            useVariable(instr.a2, currentPosition);
//            break;
//        }
        currentPosition++;
    }
}

InterCodeVector InterCodeOptimizer::optimizeInterCode(InterCodeVector &instructions)
{
    return {};
}
