#pragma once

#include <Matrix.hpp>


class DominoProblem
{
public:
    DominoProblem();
    void solve();

private:
    Matrix<int> m_numbers;
    int countPair(int p_a, int p_b);
};
