#include <iostream>
#include <list>
#include <vector>
#include <BitStream.hpp>
#include <ICompress.hpp>
#include <Huffman.hpp>


Huffman::Huffman(const char* p_inputFilePath, const char* p_outputFilePath)
    :    ICompress(p_inputFilePath, p_outputFilePath)
{
    m_npNode = new TreeElement(nullptr);
    addNpNode();
}

void Huffman::codeStream()
{
    unsigned char character;
    bitstr.setFile(&m_outputFile);
    while(m_inputFile.read((char*)&character, sizeof(char)))
    {
        updateProgressBar();
        Symbol l_code;
        TreeElement* l_element = findElementByCharacter(character);
        if (l_element != nullptr)
        {
            l_code = getCode(l_element);
            updateElement(l_element);
        }
        else
        {
            sendNpCode();
            updateElement(addTreeElement(character));
            unsigned char temp = 0;
            for(int i=0; i<m_bitsPerSymbol; i++, character >>= 1)
                temp = (temp << 1) | (character & 1);
            l_code = {temp, m_bitsPerSymbol};
        }
        for (int i = 0; i<l_code.length; i++, l_code.code>>=1)
            bitstr.putBit(l_code.code & 1);
    }
    sendNpCode();
    bitstr.forceWrite();
}

void Huffman::decodeStream()
{
    bitstr.setFile(&m_inputFile);
    while (!bitstr.eof())
    {
        updateProgressBar();
        TreeElement* l_element = getNextElementFromStream();
        if(l_element != nullptr)
        {
            unsigned char character = 0;
            if(l_element == m_npNode)
            {
                for (int i = 0; i < m_bitsPerSymbol; i++)
                {
                    if(bitstr.eof())
                        return;
                    character = (character << 1) | bitstr.getBit();
                }
                updateElement(addTreeElement(character));
            }
            else
            {
                character = l_element->value;
                updateElement(l_element);
            }
            m_outputFile.write((const char*)&character, sizeof(char));
        }
        else
            if(!bitstr.eof())
                std::cout << "ERROR: There is something wrong with that code" << std::endl;
    }
}

void Huffman::addNpNode()
{
    m_nodeList.push_back(m_npNode);
    m_npNode->listIterator = std::prev(m_nodeList.end());
}

void Huffman::addTreeElementToList(TreeElement* p_element)
{
    m_nodeList.push_back(p_element);
    p_element->listIterator = std::prev(m_nodeList.end());
    addNpNode();
}

void Huffman::swapParentsChildNodes(TreeElementsList::iterator p_first, TreeElementsList::iterator p_second)
{
    TreeElement** l_firstChild;
    TreeElement** l_secondChild;
    if ((*p_first)->parentNode->rightNode == (*p_first))
        l_firstChild = &((*p_first)->parentNode->rightNode);
    else
        l_firstChild = &((*p_first)->parentNode->leftNode);
    if((*p_second)->parentNode->rightNode == *p_second)
        l_secondChild = &((*p_second)->parentNode->rightNode);
    else
        l_secondChild = &((*p_second)->parentNode->leftNode);
    std::iter_swap(l_firstChild, l_secondChild);
}

void Huffman::updateElement(TreeElement* p_element)
{
    TreeElementsList::iterator current;
    for(current = p_element->listIterator; current != m_nodeList.begin(); current = (*current)->parentNode->listIterator)
    {
        TreeElementsList::iterator change = current;
        while((std::prev(change)) != m_nodeList.begin() && (*std::prev(change))->weight == (*change)->weight)
            change--;
        (*current)->weight++;
        if(current != change)
        {
            swapParentsChildNodes(current, change);
            std::swap((*current)->parentNode, (*change)->parentNode);
            std::swap((*current)->listIterator, (*change)->listIterator);
            std::swap(*current, *change);
        }
    }
    (*m_nodeList.begin())->weight++;
}

Symbol Huffman::getCode(TreeElement* p_element)
{
    Symbol s = {0, 0};
    for(; p_element->parentNode != nullptr; p_element = p_element->parentNode, s.length++)
        s.code = (s.code << 1) | (p_element->parentNode->rightNode == p_element ? 1 : 0);
    return s;
}

TreeElement* Huffman::getNextElementFromStream()
{
    TreeElement* l_element;
    l_element = *(m_nodeList.begin());
    while(l_element->rightNode != nullptr && !bitstr.eof())
        l_element = bitstr.getBit()==1 ? l_element->rightNode : l_element->leftNode;
    if(l_element->rightNode != nullptr)
        return nullptr;
    return l_element;
}

TreeElement* Huffman::addTreeElement(unsigned char character)
{
    auto l_leftNode = new TreeElement(m_npNode);
    auto l_rightNode = new TreeElement(m_npNode);
    m_npNode->leftNode = l_leftNode;
    m_npNode->rightNode = l_rightNode;
    l_rightNode->value = character;
    m_leafsArray[character] = l_rightNode;
    m_npNode = l_leftNode;
    addTreeElementToList(l_rightNode);
    return l_rightNode;
}

void Huffman::sendNpCode()
{
    Symbol l_npSymbol = getCode(m_npNode);
    for (int i = 0; i<l_npSymbol.length; i++, l_npSymbol.code >>= 1)
        bitstr.putBit(l_npSymbol.code & 1);
}

TreeElement* Huffman::findElementByCharacter(unsigned char character)
{
    return m_leafsArray[character];
}
