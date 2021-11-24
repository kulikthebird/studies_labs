#pragma once


#include <iostream>
#include <list>
#include <vector>
#include <numeric>
#include <BitStream.hpp>
#include <ICompress.hpp>


struct TreeElement
{
    TreeElement(TreeElement* p_parent) : parentNode(p_parent) {}
    TreeElement* leftNode = nullptr;
    TreeElement* rightNode = nullptr;
    TreeElement* parentNode;
    std::list<TreeElement*>::iterator listIterator;
    unsigned int weight = 0;
    unsigned int value = 0;
};
typedef std::list<TreeElement*> TreeElementsList;

struct Symbol
{
    unsigned int code;
    unsigned int length;
};

class Huffman : public ICompress
{
public:
    Huffman(const char* p_inputFilePath, const char* p_outputFilePath);

    void codeStream();
    void decodeStream();

private:
    const unsigned int m_bitsPerSymbol = 8;  // can't touch this! :P
    BitStream bitstr;
    TreeElement* m_npNode;
    TreeElementsList m_nodeList;
    std::vector<TreeElement*> m_leafsArray = std::vector<TreeElement*>(1 << m_bitsPerSymbol, nullptr);

    void sendNpCode();
    TreeElement* addTreeElement(unsigned char character);
    void addNpNode();
    void addTreeElementToList(TreeElement *e);
    void updateElement(TreeElement *e);
    void swapParentsChildNodes(TreeElementsList::iterator p_first, TreeElementsList::iterator p_second);
    Symbol getCode(TreeElement* p_element);
    TreeElement* findElementByCharacter(unsigned char character);
    TreeElement* getNextElementFromStream();
};
