#pragma once

#include <vector>


class StoneProblem
{
public:
    StoneProblem(unsigned int p_stoneWeight, unsigned int p_stoneParts);
    void solve();
    unsigned int getPartOfTheStoneWeight(unsigned int p_stonePartId);

private:
    unsigned int m_stoneWeight;
    unsigned int m_stoneParts;
    std::vector<unsigned int> m_stonePartsWeights;
};
