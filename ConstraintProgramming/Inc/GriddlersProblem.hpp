#pragma once

#include <Matrix.hpp>


class GriddlersProblem
{
public:
    GriddlersProblem(unsigned int p_x, unsigned int p_y, std::vector<std::vector<unsigned int>> p_rows, std::vector<std::vector<unsigned int>> p_cols);
    void solve();
private:
    unsigned int m_x;
    unsigned int m_y;
    std::vector<std::vector<unsigned int>> m_rows;
    std::vector<std::vector<unsigned int>> m_cols;
};
