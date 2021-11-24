#include <iostream>
#include <vector>
#include <CompilerCore.hpp>
#include <VariableManager.hpp>
#include <InterCodeGenerator.hpp>
#include <InterCodeOptimizer.hpp>
#include <OutputCodeGenerator.hpp>
#include <InterToOutputTranslator.hpp>


void CompilerCore::compileProgram(std::vector<Declaration*> Declaration, std::vector<Command*> commands)
{
    VariableManager variableManager;
    InterCodeGenerator interCodeGenerator(&variableManager);
    InterCodeOptimizer interCodeOptimizer;
    InterCodeVector resultInterCode;
    std::string outputCode;
    try
    {
        for(auto declaration : Declaration)
        {
            if(declaration->isTable)
                variableManager.addTableName(declaration->variableName, declaration->size, declaration->currentLine);
            else
                variableManager.addVariableName(declaration->variableName, declaration->currentLine);
        }
        resultInterCode = interCodeGenerator.buildCommand(commands);
    }
    catch(RedeclarationException e)
    {
        std::cout << "Blad w linii " << e.line << ": Redeklaracja zmiennej '" << e.what() << "'" << std::endl;
        return;
    }
    catch(VariableNotExistException e)
    {
        std::cout << "Blad w linii " << e.line << ": Zmienna '" << e.what() << "' nie istnieje." << std::endl;
        return;
    }
    catch(WrongSizeException e)
    {
        std::cout << "Blad w linii " << e.line << ": Tablica zerowej wielkosci '" << e.what() << "'" << std::endl;
        return;
    }
    catch(TableNotExistException e)
    {
        std::cout << "Blad w linii " << e.line << ": Tablica '" << e.what() << "' nie istnieje." << std::endl;
        return;
    }
    catch(VariableNotInitialized e)
    {
        std::cout << "Blad w linii " << e.line << ": Uzycie niezainicjalizowanej zmiennej '" << e.what() << "'." << std::endl;
        return;
    }
    catch(UsedControlVariableException e)
    {
        std::cout << "Blad w linii " << e.line << ": Uzycie zmiennej sterujacej petla '" << e.what() << "'." << std::endl;
        return;
    }
    catch(...)
    {
        std::cout << "Nieznany blad." << std::endl;
        return;
    }

//    auto optimizedCode = interCodeOptimizer.optimizeInterCode(resultInterCode);
    OutputCodeVector outputInstr;
    OutputCodeGenerator backendTranslator(&variableManager);
    InterToOutputTranslator interToOutputTranslator;
    std::cout << backendTranslator.translateInterCodeToString(resultInterCode) << std::endl;
//    outputInstr = interToOutputTranslator.translateInterCodeToOutput(optimizedCode);
//    backendTranslator.resolveLabelsAddresses(outputInstr);
//    outputCode = backendTranslator.translateOutputInstrToString(outputInstr);
//    std::cout << outputCode << std::endl;
}
