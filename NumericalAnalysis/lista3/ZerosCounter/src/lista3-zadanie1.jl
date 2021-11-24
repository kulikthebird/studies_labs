# autor: Tomasz Kulik


include("ResultTuple.jl")


function mbisekcji(f::Function,a::Float64, b::Float64, delta::Float64, epsilon::Float64)
  u = f(a)
  v = f(b)
  e = b - a
  if sign(u) == sign(v)
    return ResultTuple(0, 0, 0, int32(1))
  end
  k = int32(1)
  while 1 == 1
    e = b - a
    e = e / 2.0
    c = a+e
    w = f(c)
    if (abs(e) < delta) || (abs(w) < epsilon)
      return ResultTuple(c, w, k, int32(0))
    end
    if sign(w) == sign(v)
      b = c
      v = w
    else
      a = c
      u = w
    end
    k = k + 1
  end
  return ResultTuple(c, w, k, int32(1))
end
