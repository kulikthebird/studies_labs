#include <iostream>
#include <StoneProblem.hpp>
#include <PuzzleProblem.hpp>
#include <DominoProblem.hpp>
#include <CelticProblem.hpp>
#include <CubeProblem.hpp>
#include <GriddlersProblem.hpp>


int main(int argc, char** argv)
{
    std::cout << "$$$ StoneProblem:" << std::endl;
    StoneProblem p1(40, 4);
    p1.solve();
    for(int i=0; i<4; i++)
        std::cout << p1.getPartOfTheStoneWeight(i) << std::endl;
    std::cout << "\n$$$ PuzzleProblem:" << std::endl;
    PuzzleProblem p2;
    p2.solve();
    std::cout << "\n$$$ DominoProblem:" << std::endl;
    DominoProblem p3;
    p3.solve();
    std::cout << "\n$$$ CelticProblem:" << std::endl;
    CelticProblem p4;
    p4.solve(5, 5);
    std::cout << "\n$$$ GriddlersProblem:" << std::endl;
    GriddlersProblem p5(6, 6,
        {{2}, {2,1}, {4,1}, {4}, {1,1}, {1,1}},
        {{2}, {2}, {1,4}, {4},{1},{4}});
    p5.solve();
    std::cout << "\n$$$ CubeProblem:" << std::endl;
    CubeProblem p6;
    p6.solve();
    return 0;
}
