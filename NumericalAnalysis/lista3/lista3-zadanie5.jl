# autor: Tomasz Kulik

include("ZerosCounter/src/ZerosCounter.jl")

function f(x::Float64)
  return exp(x) - 3.0*x
end

x0 = 0.0
x1 = 1.0
result1 = mbisekcji(f, x0, x1, 0.0001, 0.0001)
x0 = nextfloat(result1.k)
x1 = 10.0
result2 = mbisekcji(f, x0, x1, 0.0001, 0.0001)

println(result1)
println(result2)
