#ifndef MEMORYMANAGEMENT_H
#define MEMORYMANAGEMENT_H

#include <Utils.hpp>
#include <string>
#include <map>
#include <functional>


struct Variable
{
    u64 memPosition;
    u32 variableId;
    std::string name;
    bool isInitialized;
    bool isTemporary;
    bool isControlVariable;
};

struct Table
{
    u64 memPosition;
    u32 tableId;
    u32 size;
};

class VariableManager
{
public:
    void createLabels(std::initializer_list<std::reference_wrapper<u32>> labelsList);
    Variable* createVariable(std::string name = "");
    void addVariableName(std::string name, u32 line);
    u32 addTemporaryVariable();
    void addControlVariableName(std::string name, u32 line);
    void addTableName(std::string name, u64 size, u32 line);
    void deleteVariableName(std::string name);
    u32 getTableId(std::string tableName, u32 line);
    u32 getVarId(std::string varName, u32 line);
    std::string getVarName(u32 variableId);
    void initializeVariable(std::string varName, u32 line);
    void assertThatInitialized(std::string varName, u32 line);
    void assertThatIsNotControlVariable(std::string name, u32 line);
    bool isVariableTemporary(u32 variableId);

private:
    u32 labelsNumber = 0;
    u32 freeMemoryPointer = 0;
    u32 numberOfTemporaryVariables = 0;
    u32 numberOfTables = 0;
    std::map<std::string, Variable*> variablesByName;
    std::map<u32, Variable*> variablesById;
    std::map<std::string, Table*>    tables;
};


#endif // MEMORYMANAGEMENT_H
