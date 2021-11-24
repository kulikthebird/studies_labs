#ifndef INTEROPTIMIZER_H
#define INTEROPTIMIZER_H

#include <Utils.hpp>

#include <map>
#include <list>

struct VariableRange
{
    u32 start;
    u32 end;
};

class InterCodeOptimizer
{
public:
    InterCodeVector optimizeInterCode(InterCodeVector &instructions);

private:
    void assignToVariable(u32 variableId, u32 currentPosition);
    void useVariable(u32 variableId, u32 currentPosition);

    std::map<u32, std::list<VariableRange>> variables;
    void checkVariablesRange(InterCodeVector &instructions);
};

#endif // INTEROPTIMIZER_H
