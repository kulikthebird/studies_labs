#include <GriddlersProblem.hpp>
#include <Matrix.hpp>
#include <vector>
#include <math.h>
#include <ilconcert/iloalg.h>
#include <ilcp/cp.h>
#include <numeric>


GriddlersProblem::GriddlersProblem(unsigned int p_x, unsigned int p_y, std::vector<std::vector<unsigned int>> p_rows, std::vector<std::vector<unsigned int>> p_cols) :
    m_x(p_x), m_y(p_y), m_rows(p_rows), m_cols(p_cols)
{}

void GriddlersProblem::solve()
{
    IloEnv env;
    try
    {
        IloModel model(env);
        Matrix<IloBoolVar> l_image(m_x, m_y, env);
        for(int x=0; x<m_x; x++)
            model.add(IloSum(l_image.getColumn<IloIntVarArray>(x, env, m_y)) == std::accumulate(m_cols[x].begin(), m_cols[x].end(), 0));
        for(int y=0; y<m_y; y++)
            model.add(IloSum(l_image.getRow<IloIntVarArray>(y, env, m_x)) == std::accumulate(m_rows[y].begin(), m_rows[y].end(), 0));
        for(int y=0; y<m_y; y++)
        {
            IloIntExpr l_gapNumber(env);
            for(int x=0; x<m_x; x++)
            {
                if(x < m_x-1)
                    l_gapNumber +=  l_image(x, y) * (1-l_image(x+1, y));
                else
                    l_gapNumber +=  l_image(x, y);
            }
            model.add(l_gapNumber == m_rows[y].size());
        }
        for(int x=0; x<m_x; x++)
        {
            IloIntExpr l_gapNumber(env);
            for(int y=0; y<m_y; y++)
            {
                if(y < m_y-1)
                    l_gapNumber +=  l_image(x, y) * (1-l_image(x, y+1));
                else
                    l_gapNumber +=  l_image(x, y);
            }
            model.add(l_gapNumber == m_cols[x].size());
        }
        IloCP solver(model);
        solver.startNewSearch();
        solver.next();
        for(int y=0; y<m_y; y++)
        {
            for(int x=0; x<m_x; x++)
                std::cout << ((solver.getValue(l_image(x, y)) == 1)?"#":" ");
            std::cout << std::endl;
        }
    }
    catch (IloException& ex)
    {
        std::cerr << "Error:" << ex << std::endl;
    }
    env.end();
}
