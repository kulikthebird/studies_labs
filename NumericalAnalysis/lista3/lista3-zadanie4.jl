# autor: Tomasz Kulik

include("ZerosCounter/src/ZerosCounter.jl")

function f(x::Float64)
  return sin(x) - (0.5*x)^2
end

function pf(x::Float64)
  return cos(x) - 0.5*x
end

result = mbisekcji(f, 1.5, 2.0, 0.000005, 0.000005)
println(result)
result = mstycznych(f, pf, 1.5, 0.000005, 0.000005, int32(10000))
println(result)
result = msiecznych(f, 1.0, 2.0, 0.000005, 0.000005, int32(10000))
println(result)
