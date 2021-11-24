#ifndef BACKENDTRANSLATOR_H
#define BACKENDTRANSLATOR_H

#include <Utils.hpp>
#include <VariableManager.hpp>
#include <string>
#include <map>


struct Mnemonic
{
    std::string name;
    u32 argsNumber;
};

class OutputCodeGenerator
{
public:
    OutputCodeGenerator(VariableManager* variableManager);
    OutputCodeVector resolveLabelsAddresses(OutputCodeVector &instructions);
    std::string translateOutputInstrToString(OutputCodeVector &instructions);
    std::string translateInterCodeToString(InterCodeVector &instructions);

private:
    void buildMnemonicMapOutputLanguage();
    std::string putOperand(Operand id);

    std::map<OutCode, Mnemonic> mnemonicMapOutputLanguage;
    std::map<ExpressionType, std::string> operators;
    VariableManager *variableManager;
};

#endif // BACKENDTRANSLATOR_H
