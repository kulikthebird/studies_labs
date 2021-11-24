#include <CubeProblem.hpp>
#include <Matrix.hpp>
#include <vector>
#include <algorithm>
#include <ilconcert/iloalg.h>
#include <ilcp/cp.h>
#include <set>


template <class T>
class Cube
{
public:
    Cube(unsigned int n)
    {
        m_cube = std::vector<std::vector<std::vector<T>>>(n, std::vector<std::vector<T>> (n, std::vector<T>(n)) );
    }

    std::vector<std::vector<T>>& operator[](size_t index)
    {
        return m_cube[index];
    }

    std::vector<std::vector<std::vector<T>>> m_cube;
};

struct Point
{
    int x, y, z;
    bool operator==(const Point& p) const
    {
        return x == p.x && y == p.y && z == p.z;
    }
    bool operator<(const Point& another) const
    {
        if(x < another.x)
            return true;
        if(x > another.x)
            return false;
        if(y < another.y)
            return true;
        if(y > another.y)
            return false;
        if(z < another.z)
            return true;
        if(z > another.z)
            return false;
        return false;
    }
};
typedef std::vector<Point> Polygon;

typedef std::vector<Polygon> Variations;
Polygon createPolygon(Polygon& poly, bool p_xSymetry, bool p_ySymetry, bool p_zSymetry, bool p_swapXY, bool p_swapXZ, bool p_swapYZ)
{
    if(p_swapXY)
        for(auto& p : poly)
            p = {p.y, p.x, p.z};
    else if(p_swapXZ)
        for(auto& p : poly)
            p = {p.z, p.y, p.x};
    else if(p_swapYZ)
        for(auto& p : poly)
            p = {p.x, p.z, p.y};
    else if(p_xSymetry)
        for(auto& p : poly)
            p = {-p.x, p.y, p.z};
    else if(p_ySymetry)
        for(auto& p : poly)
            p = {p.x, -p.y, p.z};
    else if(p_zSymetry)
        for(auto& p : poly)
            p = {p.x, p.y, -p.z};
    std::sort(poly.begin(), poly.end());
    return poly;
}

Variations generateAllVariations(Polygon polygon)
{
    Variations l_result;
    l_result.push_back({createPolygon(polygon, 0, 0, 0, 0, 0, 0)});
    l_result.push_back({createPolygon(polygon, 0, 0, 0, 1, 0, 0)});
    l_result.push_back({createPolygon(polygon, 1, 0, 0, 0, 0, 0)});
    l_result.push_back({createPolygon(polygon, 0, 0, 0, 1, 0, 0)});
    l_result.push_back({createPolygon(polygon, 1, 0, 0, 0, 0, 0)});
    l_result.push_back({createPolygon(polygon, 0, 0, 0, 1, 0, 0)});
    l_result.push_back({createPolygon(polygon, 1, 0, 0, 0, 0, 0)});
    l_result.push_back({createPolygon(polygon, 0, 0, 0, 1, 0, 0)});

    l_result.push_back({createPolygon(polygon, 0, 0, 0, 0, 1, 0)});
    l_result.push_back({createPolygon(polygon, 0, 0, 0, 1, 0, 0)});
    l_result.push_back({createPolygon(polygon, 1, 0, 0, 0, 0, 0)});
    l_result.push_back({createPolygon(polygon, 0, 0, 0, 1, 0, 0)});
    l_result.push_back({createPolygon(polygon, 0, 0, 1, 0, 0, 0)});
    l_result.push_back({createPolygon(polygon, 0, 0, 0, 0, 0, 1)});
    l_result.push_back({createPolygon(polygon, 0, 1, 0, 0, 0, 0)});
    l_result.push_back({createPolygon(polygon, 0, 0, 0, 1, 0, 0)});

    l_result.push_back({createPolygon(polygon, 0, 0, 1, 0, 0, 0)});
    l_result.push_back({createPolygon(polygon, 0, 0, 0, 1, 0, 0)});
    l_result.push_back({createPolygon(polygon, 0, 1, 0, 0, 0, 0)});
    l_result.push_back({createPolygon(polygon, 0, 0, 0, 1, 0, 0)});
    l_result.push_back({createPolygon(polygon, 0, 0, 1, 0, 0, 0)});
    l_result.push_back({createPolygon(polygon, 0, 0, 0, 0, 1, 0)});
    l_result.push_back({createPolygon(polygon, 1, 0, 0, 0, 0, 0)});
    l_result.push_back({createPolygon(polygon, 0, 0, 0, 1, 0, 0)});

    std::set<Polygon> s( l_result.begin(), l_result.end() );
    Variations vec;
    vec.assign( s.begin(), s.end() );
    return vec;
}

bool checkVariant(int x, int y, int z, Polygon poly, unsigned int n)
{
    for(auto p : poly)
        if(p.x + x > n-1 || p.x + x < 0 || p.y + y > n-1 || p.y + y < 0 || p.z + z > n-1 || p.z + z < 0)
            return false;
    return true;
}

template<class T>
std::vector<T> vector_diff( const std::vector<T>& model, const std::vector<T>& pattern )
{
    std::set<T> s_model( model.begin(), model.end() );
    std::set<T> s_pattern( pattern.begin(), pattern.end() );
    std::vector<T> result;
    std::set_difference( s_model.begin(), s_model.end(), s_pattern.begin(), s_pattern.end(),
        std::back_inserter( result ) );
    return result;
}

void addPolygoneToModel(Variations p_vars, int polyNumber, unsigned int n, Cube<IloIntVar>& p_results, IloEnv& env, IloModel& model)
{
    IloOr l_alternative(env);
    std::vector<Point> l_allCubePoints(n*n*n);
    for(int x=0; x<n; x++)
        for(int y=0; y<n; y++)
            for(int z=0; z<n; z++)
                l_allCubePoints[x+n*y+n*n*z] = {x, y, z};
    for(int x=0; x<n; x++)
        for(int y=0; y<n; y++)
            for(int z=0; z<n; z++)
                for(Polygon& variant : p_vars)
                    if(checkVariant(x, y, z, variant, n))
                    {
                        IloAnd expr(env);
                        for(Point p : variant)
                            expr.add(p_results[p.x+x][p.y+y][p.z+z] == polyNumber);
                        for(int x2=0; x2<n; x2++)
                            for(int y2=0; y2<n; y2++)
                                for(int z2=0; z2<n; z2++)
                                    if(std::find(variant.cbegin(), variant.cend(), Point{x2 - x, y2 - y, z2 - z} ) == variant.end())
                                        expr.add(p_results[x2][y2][z2] != polyNumber);
                        l_alternative.add(expr);
                    }
    model.add(l_alternative);
}

void CubeProblem::solve()
{
    IloEnv env;
    try
    {
        const unsigned int n = 3;
        IloModel model(env);
        Cube<IloIntVar> l_result(n);
        for(int x=0; x<n; x++)
            for(int y=0; y<n; y++)
                for(int z=0; z<n; z++)
                    l_result[x][y][z] = IloIntVar(env, 0, 6);
        addPolygoneToModel(Variations{{{0,0,0}, {1,0,0}, {2,0,0}, {2,1,0}}}, 0, n, l_result, env, model);
        addPolygoneToModel(generateAllVariations(Polygon{{0,0,0}, {1,0,0}, {2,0,0}, {2,1,0}}), 1, n, l_result, env, model);
        addPolygoneToModel(generateAllVariations(Polygon{{0,0,0}, {1,0,0}, {2,0,0}, {2,1,0}}), 2, n, l_result, env, model);
        addPolygoneToModel(generateAllVariations(Polygon{{0,0,0}, {1,0,0}, {2,0,0}, {2,1,0}}), 3, n, l_result, env, model);
        addPolygoneToModel(generateAllVariations(Polygon{{0,0,0}, {1,0,0}, {0,1,0}}), 4, n, l_result, env, model);
        addPolygoneToModel(generateAllVariations(Polygon{{-1,0,0}, {0,0,0}, {1,0,0}, {0,1,0}}), 5, n, l_result, env, model);
        addPolygoneToModel(generateAllVariations(Polygon{{0,0,0}, {1,0,0}, {1,1,0}, {2,1,0}}), 6, n, l_result, env, model);
        IloCP solver(model);
        solver.startNewSearch();
        solver.next();
        for(int z=0; z<n; z++)
        {
            for(int x=0; x<n; x++)
            {
                for(int y=0; y<n; y++)
                    std::cout << solver.getValue(l_result[x][y][z]);
                std::cout << std::endl;
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
