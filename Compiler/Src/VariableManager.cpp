#include <VariableManager.hpp>


void VariableManager::createLabels(std::initializer_list<std::reference_wrapper<u32>> labelsList)
{
    for(auto label : labelsList)
        label.get() = labelsNumber++;
}

Variable* VariableManager::createVariable(std::string name)
{
    Variable* var = new Variable;
    var->isInitialized = false;
    var->isControlVariable = false;
    var->isTemporary = false;
    var->memPosition = 0;
    var->name = name;
    var->variableId = variablesById.size();
    return var;
}

void VariableManager::addVariableName(std::string name, u32 line)
{
    if(variablesByName.find(name) != variablesByName.end())
        throw RedeclarationException(name, line);
    Variable *var = createVariable(name);
    variablesByName[name] = var;
    variablesById[var->variableId] = var;
}

u32 VariableManager::addTemporaryVariable()
{
    Variable *var = createVariable();
    var->isTemporary = true;
    variablesById[var->variableId] = var;
    return var->variableId;
}

void VariableManager::addControlVariableName(std::string name, u32 line)
{
    addVariableName(name, line);
    variablesByName[name]->isControlVariable = true;
}

void VariableManager::addTableName(std::string name, u64 size, u32 line)
{
    if(tables.find(name) != tables.end())
        throw RedeclarationException(name, line);
    if(size == 0)
        throw WrongSizeException(name, line);
    Table* tab = new Table;
    tables[name] = tab;
    tab->memPosition = freeMemoryPointer;
    tab->tableId = numberOfTables++;
    tab->size = size;
    freeMemoryPointer += size;
}

void VariableManager::deleteVariableName(std::string name)
{
    variablesByName.erase(name);
}

u32 VariableManager::getTableId(std::string tableName, u32 line)
{
    if(tables.find(tableName) != tables.end())
        return tables[tableName]->tableId;
    else
        throw TableNotExistException(tableName, line);
}

u32 VariableManager::getVarId(std::string varName, u32 line)
{
    if(variablesByName.find(varName) != variablesByName.end())
        return variablesByName[varName]->variableId;
    else
        throw VariableNotExistException(varName, line);
}

std::string VariableManager::getVarName(u32 variableId)
{
    return variablesById[variableId]->name;
}

bool VariableManager::isVariableTemporary(u32 variableId)
{
    return variablesById[variableId]->isTemporary;
}

void VariableManager::initializeVariable(std::string varName, u32 line)
{
    if(variablesByName.find(varName) != variablesByName.end())
        variablesByName[varName]->isInitialized = true;
    else
        throw VariableNotExistException(varName, line);
}

void VariableManager::assertThatInitialized(std::string varName, u32 line)
{
    if(variablesByName.find(varName) != variablesByName.end())
        if(!variablesByName[varName]->isInitialized)
            throw VariableNotInitialized(varName, line);
}

void VariableManager::assertThatIsNotControlVariable(std::string name, u32 line)
{
    if(variablesByName.find(name) != variablesByName.end())
        if(variablesByName[name]->isControlVariable)
            throw UsedControlVariableException(name, line);
}
