#include <CelticProblem.hpp>
#include <Matrix.hpp>
#include <vector>
#include <math.h>
#include <ilconcert/iloalg.h>
#include <ilcp/cp.h>


CelticProblem::CelticProblem()
{}

struct Tile
{
    IloBoolVar* left = nullptr;
    IloBoolVar* right = nullptr;
    IloBoolVar* up = nullptr;
    IloBoolVar* down = nullptr;
};

void CelticProblem::solve(unsigned int p_x, unsigned int p_y)
{
    IloEnv env;
    try
    {
        IloModel model(env);
        Matrix<Tile> l_tiles(p_x, p_y);
        IloIntExpr sumA(env);
        IloIntExpr sumB(env);
        IloIntExpr sumC(env);
        IloIntExpr sumD(env);
        IloIntExpr sumE(env);
        for(int x=0; x<p_x; x++)
            for(int y=0; y<p_y; y++)
            {
                IloBoolVar*& right = l_tiles(x, y).right;
                IloBoolVar*& left = l_tiles(x, y).left;
                IloBoolVar*& down = l_tiles(x, y).down;
                IloBoolVar*& up = l_tiles(x, y).up;
                if(x < p_x-1)
                {
                    right = new IloBoolVar(env);
                }
                if(x > 0)
                {
                    left =  new IloBoolVar(env);
                    model.add(*l_tiles(x-1, y).right == *left);
                }
                if(y < p_y-1)
                {
                    down =  new IloBoolVar(env);
                }
                if(y > 0)
                {
                    up =  new IloBoolVar(env);
                    model.add(*l_tiles(x, y-1).down == *up);
                }
                // A
                if(right != nullptr) sumA += *right;
                if(left != nullptr) sumA += *left;
                if(down != nullptr) sumA += *down;
                if(up != nullptr) sumA += *up;
                // B
                if(right != nullptr && left != nullptr) sumB += *right * *left;
                if(up != nullptr && down != nullptr) sumB += *up * *down;
                // C
                if(right != nullptr && down != nullptr) sumC += *right * *down;
                if(down != nullptr && left != nullptr) sumC += *down * *left;
                if(left != nullptr && up != nullptr) sumC += *left * *up;
                if(up != nullptr && right != nullptr) sumC += *up * *right;
                // D
                if(right != nullptr && down != nullptr && left != nullptr) sumD += *right * *down * *left;
                if(down != nullptr && left != nullptr && up != nullptr) sumD += *down * *left * *up;
                if(left != nullptr && up != nullptr && right != nullptr) sumD += *left * *up * *right;
                if(up != nullptr && right != nullptr && down != nullptr) sumD += *up * *right * *down;
                // E
                if(up != nullptr && right != nullptr && down != nullptr && left != nullptr) sumE += *up * *right * *down * *left;

            }
        const int expectedA = 5;
        const int expectedB = 5;
        const int expectedC = 5;
        const int expectedD = 5;
        const int expectedE = 5;
        model.add(sumA == 2*expectedB + 2*expectedC + 3*expectedD + 4*expectedE + expectedA &&
                  sumB == 1*expectedD + 2*expectedE + expectedB &&
                  sumC == 2*expectedD + 4*expectedE + expectedC &&
                  sumD == 4*expectedE + expectedD &&
                  sumE == expectedE);
        model.add(*l_tiles(2, 2).right + *l_tiles(2, 2).left + *l_tiles(2, 2).up + *l_tiles(2, 2).down == 4);
        for(int y=0; y<p_y; y++)
            for(int x=0; x<p_x; x++)
            {
                IloIntExpr tile(env);
                if(l_tiles(x, y).right != nullptr) tile += *l_tiles(x, y).right;
                if(l_tiles(x, y).left != nullptr) tile += *l_tiles(x, y).left;
                if(l_tiles(x, y).up != nullptr) tile += *l_tiles(x, y).up;
                if(l_tiles(x, y).down != nullptr) tile += *l_tiles(x, y).down;
                model.add(tile >= 1);
            }
        IloCP solver(model);
        solver.startNewSearch();
        solver.next();
        for(int y=0; y<2*p_y; y++)
        {
            for(int x=0; x<p_x; x++)
            {
                if(y%2 == 0)
                {
                    if(l_tiles(x, y/2).up != nullptr && solver.getValue(*l_tiles(x, y/2).up) == 1)
                        std::cout << "=";
                    else
                        std::cout << " ";
                    std::cout << " ";
                }
                else
                {
                    auto tileType = std::to_string((unsigned int)(
                             (l_tiles(x, y/2).right != nullptr?solver.getValue(*l_tiles(x, y/2).right):0) +
                             (l_tiles(x, y/2).left != nullptr?solver.getValue(*l_tiles(x, y/2).left):0) +
                             (l_tiles(x, y/2).up != nullptr?solver.getValue(*l_tiles(x, y/2).up):0) +
                             (l_tiles(x, y/2).down != nullptr?solver.getValue(*l_tiles(x, y/2).down):0)
                        ));
                    std::cout << tileType;
                    if(l_tiles(x, y/2).right != nullptr && solver.getValue(*l_tiles(x, y/2).right) == 1)
                        std::cout << "|";
                    else
                        std::cout << " ";
                }
            }
            std::cout << std::endl;
        }
    }
    catch (IloException& ex)
    {
        std::cerr << "Error:" << ex << std::endl;
    }
    env.end();
}
