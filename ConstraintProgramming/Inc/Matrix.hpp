#pragma once
#include <vector>
#include <initializer_list>


template <typename T>
struct Matrix
{
public:
    Matrix()
    {}

    template <typename ... Args>
    Matrix(unsigned int x, unsigned int y, Args... Fargs) :
         m_x(x), m_y(y)
    {
        m_mat.reserve(x*y);
        for(int i=0; i<x*y; i++)
            m_mat.push_back(T(Fargs...));
    }

    Matrix(std::initializer_list<std::initializer_list<T>> p_list)
    {
        m_x = (*p_list.begin()).size();
        m_y = p_list.size();
        m_mat.reserve(m_x * m_y);
        int y=0;
        for(auto l_row : p_list)
        {
            int x=0;
            for(auto l_cell : l_row)
            {
                (*this)(x, y) = l_cell;
                x++;
            }
            y++;
        }
    }

    T& operator()(unsigned int p_index1, unsigned int p_index2)
    {
        if(p_index1 >= m_x || p_index2 >= m_y)
            throw OutOfRangeException();
        return m_mat[p_index1 + m_x*p_index2];
    }

    const T& operator()(unsigned int p_index1, unsigned int p_index2) const
    {
        if(p_index1 >= m_x || p_index2 >= m_y)
            throw OutOfRangeException();
        return m_mat[p_index1 + m_x*p_index2];
    }

    template<typename R, typename ... Args>
    R getColumn(unsigned int p_column, Args... Fargs)
    {
        R l_col(Fargs...);
        for(int y=0; y<m_y; y++)
            l_col[y] = (*this)(p_column, y);
        return l_col;
    }

    template<typename R, typename ... Args>
    R getRow(unsigned int p_row, Args... Fargs)
    {
        R l_row(Fargs...);
        for(int x=0; x<m_x; x++)
            l_row[x] = (*this)(x, p_row);
        return l_row;
    }

    template<typename R, typename ... Args>
    R getAllElements(Args... Fargs)
    {
        R l_array(Fargs...);
        for(int y=0; y<m_y; y++)
            for(int x=0; x<m_x; x++)
                l_array[x + m_x*y] = (*this)(x, y);
        return l_array;
    }

    unsigned int getXsize()
    { return m_x; }

    unsigned int getYsize()
    { return m_y; }

    class OutOfRangeException
    {
        public:
            const char* what()
            {
                return "Indices out of scope";
            }
    };

private:
    std::vector<T> m_mat;
    unsigned int m_x;
    unsigned int m_y;
};
