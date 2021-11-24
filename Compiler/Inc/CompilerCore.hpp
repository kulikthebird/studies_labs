#ifndef COMPILER_CORE
#define COMPILER_CORE


#include <AST.hpp>
#include <Utils.hpp>
#include <string>
#include <map>


class CompilerCore
{
public:
    void compileProgram(std::vector<Declaration*> Declaration, std::vector<Command*> commands);
};


#endif      // COMPILER_CORE
