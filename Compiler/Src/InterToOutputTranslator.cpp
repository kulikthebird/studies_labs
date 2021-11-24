#include <InterToOutputTranslator.hpp>
#include <Utils.hpp>


struct Register
{
    u32 variableId = 0;
    bool isInUse = false;
};

struct InterNode
{
    InterNode* parent1 = nullptr;
    InterNode* parent2 = nullptr;

    InterCodeList action;
    std::vector<InterNode*> children;
};


OutputCodeVector InterToOutputTranslator::translateInterCodeToOutput(InterCodeVector &instructions)
{

    std::vector< // TODO

    u32 registersInUse;
    Register regs[10];
    std::map<u32, >
    for(InterCode &instruction : instructions)
    {
        switch(instruction.cmd)
        {
        case InterCodeList::MOV:
            break;
        case InterCodeList::READ:
            break;
        case InterCodeList::WRITE:
            break;
        case InterCodeList::STORE:
            break;
        case InterCodeList::LOAD:
            break;
        case InterCodeList::EXPR:
            break;
        case InterCodeList::IF:
            break;
        case InterCodeList::JUMP:
            break;
        case InterCodeList::HALT:
            break;
        case InterCodeList::LABEL:
            break;
        }
    }
    return {};
}

//void InterToOutputTranslator::codeAnalyzer(InterCodeVector &instructions)
//{

//}
