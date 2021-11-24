# autor: Tomasz Kulik


include("ResultTuple.jl")


function mstycznych(f::Function, pf::Function, x0::Float64, delta::Float64, epsilon::Float64, maxint::Int32)
  v = f(x0)
  if abs(v) < epsilon
    return ResultTuple(0, x0, v, float32(2))
  end
  for k=1:maxint
    x1 = x0 - v/pf(x0)
    v = f(x1)
    if (abs(x1 - x0) < delta) || (abs(v) < epsilon)
      return ResultTuple(x1, v, k, int32(0))
    end
    x0 = x1
  end
  return ResultTuple(0,0,0,int32(1))
end
