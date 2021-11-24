#include <PartialSum.hpp>
#include <iostream>


PartialSum::PartialSum(unsigned int p_size)
{
    m_partialSum = std::vector<unsigned int>(p_size);
    for(unsigned int i=0; i<p_size; i++)
        m_partialSum[i] = i+1;
}

void PartialSum::updatePartialSum(unsigned char l_character)
{
    for(unsigned int i=l_character; i<m_partialSum.size(); i++)
        m_partialSum[i]++;
    if (getSumOfAllElements() >= 0x3FFE)
    {
        m_partialSum[0] >>= 1;
        if (m_partialSum[0] == 0)
            m_partialSum[0] = 1;
        for (int i = 1; i<m_partialSum.size(); i++)
        {
            m_partialSum[i] >>= 1;
            if (m_partialSum[i] <= m_partialSum[i-1])
                m_partialSum[i] = m_partialSum[i-1] + 1;
        }
    }
}

unsigned char PartialSum::getCharacter(unsigned int p_projectedValue)
{
    for(int i=0; i<m_partialSum.size(); i++)
        if(m_partialSum[i] > p_projectedValue)
            return i;
    return 0;
}

unsigned int PartialSum::getSumOfAllElements()
{
    return m_partialSum[m_partialSum.size()-1];
}
