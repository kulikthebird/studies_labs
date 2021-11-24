#include <DominoProblem.hpp>
#include <Matrix.hpp>
#include <vector>
#include <math.h>
#include <ilconcert/iloalg.h>
#include <ilcp/cp.h>


DominoProblem::DominoProblem()
{
    m_numbers =
    {
        {3, 1, 2, 6, 6, 1, 2, 2},
        {3, 4, 1, 5, 3, 0, 3, 6},
        {5, 6, 6, 1, 2, 4, 5, 0},
        {5, 6, 4, 1, 3, 3, 0, 0},
        {6, 1, 0, 6, 3, 2, 4, 0},
        {4, 1, 5, 2, 4, 3, 5, 5},
        {4, 1, 0, 2, 4, 5, 2, 0}
    };
}

int DominoProblem::countPair(int p_a, int p_b)
{
    int l_result = 7*std::max(p_a, p_b) + std::min(p_a, p_b);
    return l_result;
}

void DominoProblem::solve()
{
    IloEnv env;
    try
    {
        IloModel model(env);
        unsigned int l_x = m_numbers.getXsize();
        unsigned int l_y = m_numbers.getYsize();
        Matrix<IloIntVar> l_links(l_x, l_y, env, 0, 7*6+6);
        Matrix<IloIntExpr> l_sumOfGivenBlock(7, 7, env);
        for(int x=0; x<l_x; x++)
            for(int y=0; y<l_y; y++)
            {
                IloOr l_linksConstraints(env);
                int l_firstValue = m_numbers(x, y);
                if(x < l_x-1)
                {
                    int l_secondValue = m_numbers(x+1, y);
                    int l_uniqueValue = countPair(l_firstValue, l_secondValue);
                    IloConstraint l_condition = l_links(x, y) == l_uniqueValue && l_links(x+1, y) == l_uniqueValue;
                    l_linksConstraints.add(l_condition);
                    l_sumOfGivenBlock(l_firstValue, l_secondValue) += l_condition;
                }
                if(x > 0)
                {
                    int l_secondValue = m_numbers(x-1, y);
                    int l_uniqueValue = countPair(l_firstValue, l_secondValue);
                    IloConstraint l_condition = l_links(x, y) == l_uniqueValue && l_links(x-1, y) == l_uniqueValue;
                    l_linksConstraints.add(l_condition);
                    l_sumOfGivenBlock(l_firstValue, l_secondValue) += l_condition;
                }
                if(y < l_y-1)
                {
                    int l_secondValue = m_numbers(x, y+1);
                    int l_uniqueValue = countPair(l_firstValue, l_secondValue);
                    IloConstraint l_condition = l_links(x, y) == l_uniqueValue && l_links(x, y+1) == l_uniqueValue;
                    l_linksConstraints.add(l_condition);
                    l_sumOfGivenBlock(l_firstValue, l_secondValue) += l_condition;
                }
                if(y > 0)
                {
                    int l_secondValue = m_numbers(x, y-1);
                    int l_uniqueValue = countPair(l_firstValue, l_secondValue);
                    IloConstraint l_condition = l_links(x, y) == l_uniqueValue && l_links(x, y-1) == l_uniqueValue;
                    l_linksConstraints.add(l_condition);
                    l_sumOfGivenBlock(l_firstValue, l_secondValue) += l_condition;
                }
                model.add(l_linksConstraints);
            }
        for(int y=0; y<7; y++)
            for(int x=0; x<7; x++)
                if(x == y)
                    model.add(l_sumOfGivenBlock(x, y) == 2);
                else
                    model.add(l_sumOfGivenBlock(x, y) == 1);
        IloCP solver(model);
        solver.startNewSearch();
        solver.next();
        for(int y=0; y<l_y; y++)
        {
            for(int x=0; x<l_x; x++)
                std::cout << solver.getValue(l_links(x, y)) << " ";
            std::cout << std::endl;
        }
    }
    catch (IloException& ex)
    {
        std::cerr << "Error:" << ex << std::endl;
    }
    env.end();
}

