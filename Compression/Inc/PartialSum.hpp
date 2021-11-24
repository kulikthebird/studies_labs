#pragma once

#include <vector>


class PartialSum
{
public:
    PartialSum(unsigned int p_size);
    void updatePartialSum(unsigned char l_character);
    unsigned char getCharacter(unsigned int p_projectedValue);
    unsigned int getSumOfAllElements();

    unsigned int operator[](unsigned int p_element)
    {
        return p_element == 0 ? 0 : m_partialSum[p_element-1];
    }

private:
    std::vector<unsigned int> m_partialSum;
};
