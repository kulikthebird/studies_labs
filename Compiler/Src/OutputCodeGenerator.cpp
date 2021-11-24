#include <string>
#include <Utils.hpp>
#include <OutputCodeGenerator.hpp>



OutputCodeGenerator::OutputCodeGenerator(VariableManager *variableManager) : variableManager(variableManager)
{
    this->buildMnemonicMapOutputLanguage();
}

OutputCodeVector OutputCodeGenerator::resolveLabelsAddresses(OutputCodeVector &instructions)
{
    OutputCodeVector resultInstr;
    std::map<u32, u32> labelAddresses;
    int currentAddress = 0;
    for(OutputCode &cmd : instructions)
        if(cmd.id != OutCode::LABEL)
            currentAddress++;
        else
            labelAddresses[cmd.a1] = currentAddress;
    for(OutputCode &cmd : instructions)
        switch(cmd.id)
        {
        case OutCode::JUMP:
            resultInstr.push_back({OutCode::JUMP, labelAddresses[cmd.a1]});
            break;
        case OutCode::JZERO:
            resultInstr.push_back({OutCode::JZERO, cmd.a1, labelAddresses[cmd.a2]});
            break;
        case OutCode::LABEL:
            break;
        default:
            resultInstr.push_back(cmd);
            break;
        }
    return resultInstr;
}

void OutputCodeGenerator::buildMnemonicMapOutputLanguage()
{
    mnemonicMapOutputLanguage[OutCode::READ] = {"READ", 1};
    mnemonicMapOutputLanguage[OutCode::WRITE] = {"WRITE", 1};
    mnemonicMapOutputLanguage[OutCode::LOAD] = {"LOAD", 2};
    mnemonicMapOutputLanguage[OutCode::STORE] = {"STORE", 2};
    mnemonicMapOutputLanguage[OutCode::COPY] = {"COPY", 2};
    mnemonicMapOutputLanguage[OutCode::ADD] = {"ADD", 2};
    mnemonicMapOutputLanguage[OutCode::SUB] = {"SUB", 2};
    mnemonicMapOutputLanguage[OutCode::SHR] = {"SHR", 1};
    mnemonicMapOutputLanguage[OutCode::SHL] = {"SHL", 1};
    mnemonicMapOutputLanguage[OutCode::INC] = {"INC", 1};
    mnemonicMapOutputLanguage[OutCode::DEC] = {"DEC", 1};
    mnemonicMapOutputLanguage[OutCode::RESET] = {"RESET", 1};
    mnemonicMapOutputLanguage[OutCode::JUMP] = {"JUMP", 1};
    mnemonicMapOutputLanguage[OutCode::JZERO] = {"JZERO", 2};
    mnemonicMapOutputLanguage[OutCode::JODD] = {"JODD", 2};
    mnemonicMapOutputLanguage[OutCode::HALT] = {"HALT", 0};
    operators[ExpressionType::ADD] = " + ";
    operators[ExpressionType::SUB] = " - ";
    operators[ExpressionType::MUL] = " * ";
    operators[ExpressionType::DIV] = " / ";
    operators[ExpressionType::MOD] = " mod ";
    operators[ExpressionType::EQ] = " == ";
    operators[ExpressionType::GT] = " > ";
    operators[ExpressionType::LT] = " < ";
    operators[ExpressionType::GE] = " >= ";
    operators[ExpressionType::LE] = " <= ";
    operators[ExpressionType::NE] = " != ";
}

std::string OutputCodeGenerator::translateOutputInstrToString(OutputCodeVector &instructions)
{
    std::string outputCode;
    for(OutputCode &instr : instructions)
    {
        Mnemonic mnm = mnemonicMapOutputLanguage[instr.id];
        outputCode += mnm.name +
                (mnm.argsNumber > 0 ? " " + std::to_string(instr.a1) : "") +
                (mnm.argsNumber > 1 ? " " + std::to_string(instr.a2) : "") + "\n";
    }
    return outputCode;
}

std::string OutputCodeGenerator::translateInterCodeToString(InterCodeVector &instructions)
{
    std::string outputCode;
    for(InterCode &instr : instructions)
    {
        switch(instr.cmd)
        {
        case InterCodeList::MOV:
            outputCode += "   " + putOperand(instr.a1) + " = " + putOperand(instr.a2) + ";\n";
            break;
        case InterCodeList::READ:
            outputCode += "   " + putOperand(instr.a1) + " = call Read();\n";
            break;
        case InterCodeList::WRITE:
            outputCode += "   call Write(" + putOperand(instr.a1) + ");\n";
            break;
        case InterCodeList::STORE:
            outputCode += "   " + putOperand(instr.a1) + "[" + putOperand(instr.a2) + "] = " + putOperand(instr.a3) + ";\n";
            break;
        case InterCodeList::LOAD:
            outputCode += "   " + putOperand(instr.a3) + " = " + putOperand(instr.a1) + "[" + putOperand(instr.a2) + "];\n";
            break;
        case InterCodeList::EXPR:
            outputCode += "   " + putOperand(instr.a3) + " = " + putOperand(instr.a1) + operators[instr.expressionType] +
                    putOperand(instr.a2) + ";\n";
            break;
        case InterCodeList::IF:
            outputCode += "   if not " + putOperand(instr.a1) + operators[instr.expressionType] + putOperand(instr.a2) +
                    " then goto #" + putOperand(instr.a3) + "\n";
            break;
        case InterCodeList::JUMP:
            outputCode += "   jump #" + putOperand(instr.a1) + ";\n";
            break;
        case InterCodeList::HALT:
            outputCode += "   halt;\n";
            break;
        case InterCodeList::LABEL:
            outputCode += putOperand(instr.a1) + ":\n";
            break;
        }
    }
    return outputCode;
}

std::string OutputCodeGenerator::putOperand(Operand id)
{
    switch(id.operandType)
    {
    case OperandType::VAR:
        if(variableManager->isVariableTemporary(id.variable))
            return "%tmp" + std::to_string(id.variable);
        else
            return "$" + variableManager->getVarName(id.variable);
    case OperandType::CONST:
        return "{" + std::to_string(id.constant) + "}";
    case OperandType::LABELID:
        return "label" + std::to_string(id.label);
    case OperandType::TABLEID:
        return "@id" + std::to_string(id.tableId);
    default:
        throw std::exception();
    }
}

