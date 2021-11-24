#include <PuzzleProblem.hpp>
#include <Matrix.hpp>
#include <vector>
#include <ilconcert/iloalg.h>
#include <ilcp/cp.h>


PuzzleProblem::PuzzleProblem()
{}

void PuzzleProblem::solve()
{
    IloEnv env;
    try
    {
        const unsigned int n = 5;
        IloModel model(env);
        Matrix<unsigned int> l_initialState(n, n, 0);
        Matrix<unsigned int> l_resultState(n, n, 0);
        Matrix<IloBoolVar> l_moves(n, n, env);
        Matrix<IloConstraint> l_result(n, n);

        l_initialState(2, 1) = 1;
        l_initialState(1, 2) = 1;
        l_initialState(2, 2) = 1;
        l_initialState(3, 2) = 1;
        l_initialState(2, 3) = 1;
        for(int x=0; x<n; x++)
            for(int y=0; y<n; y++)
                l_resultState(x, y) = 1;

        for(int x=0; x<n; x++)
            for(int y=0; y<n; y++)
                l_result(x, y) = (l_initialState(x, y) == 1) ? IloTrueConstraint(env) : IloFalseConstraint(env);
        for(int x=0; x<n; x++)
            for(int y=0; y<n; y++)
            {
                l_result(x, y) = l_result(x, y) != l_moves(x, y);
                if(x > 0)
                    l_result(x-1, y) = l_result(x-1, y) != l_moves(x, y);
                if(x < n-1)
                    l_result(x+1, y) = l_result(x+1, y) != l_moves(x, y);
                if(y > 0)
                    l_result(x, y-1) = l_result(x, y-1) != l_moves(x, y);
                if(y < n-1)
                    l_result(x, y+1) = l_result(x, y+1) != l_moves(x, y);
            }
        for(int x=0; x<n; x++)
            for(int y=0; y<n; y++)
                model.add(l_result(x, y) == l_resultState(x, y));

        IloCP solver(model);
        solver.startNewSearch();
        solver.next();
        for(int x=0; x<n; x++)
        {
            for(int y=0; y<n; y++)
                std::cout << solver.getValue(l_moves(x, y));
            std::cout << std::endl;
        }
    }
    catch (IloException& ex)
    {
        std::cerr << "Error:" << ex << std::endl;
    }
    env.end();
}
