# autor: Tomasz Kulik


include("ResultTuple.jl")


function msiecznych(f::Function,x0::Float64, x1::Float64, delta::Float64, epsilon::Float64, maxint::Int32)
  fa = f(x0)
  fb = f(x1)
  for k=1:maxint
    if abs(fa) > abs(fb)
      temp = x0
      x0 = x1
      x1 = temp
      temp = fa
      fa = fb
      fb = temp
    end
    s = (x1 - x0)/(fb - fa)
    x1 = x0
    fb = fa
    x0 = x0 - fa*s
    fa = f(x0)
    if (abs(x1-x0) < delta) || (abs(fa) < epsilon)
      return ResultTuple(x0, fa, k, int32(0))
    end
  end
  return ResultTuple(0,0,0,int32(1))
end
