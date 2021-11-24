#include <StoneProblem.hpp>
#include <vector>
#include <ilconcert/iloalg.h>
#include <ilcp/cp.h>


StoneProblem::StoneProblem(unsigned int p_stoneWeight, unsigned int p_stoneParts)
    : m_stonePartsWeights(std::vector<unsigned int>(p_stoneParts)), m_stoneParts(p_stoneParts), m_stoneWeight(p_stoneWeight)
{}

void StoneProblem::solve()
{
    IloEnv env;
    try
    {
        IloModel model(env);
        IloIntVarArray l_stonePartsWeightsVars(env, m_stoneParts, 1, m_stoneWeight);
        std::vector<IloIntVarArray> l_tempVars(m_stoneWeight);
        for(auto& tempVar : l_tempVars)
            tempVar = IloIntVarArray(env, m_stoneParts, -1, 1);
        model.add(IloSum(l_stonePartsWeightsVars) == m_stoneWeight);
        for(int i=1 ; i<=m_stoneWeight; i++)
            model.add(IloScalProd(l_stonePartsWeightsVars, l_tempVars[i-1]) == i);
        IloCP solver(model);
        solver.startNewSearch();
        solver.next();
        for(int i=0; i<m_stonePartsWeights.size(); i++)
            m_stonePartsWeights[i] = solver.getValue(l_stonePartsWeightsVars[i]);
    }
    catch (IloException& ex)
    {
        std::cerr << "Error:" << ex << std::endl;
    }
    env.end();
}

unsigned int StoneProblem::getPartOfTheStoneWeight(unsigned int p_index)
{
    return m_stonePartsWeights[p_index];
}
