# autor: Tomasz Kulik

include("ZerosCounter/src/ZerosCounter.jl")


# zero: 5.0
function f1(x::Float64)
  return 2.0*x - 10.0
end

function pf1(x::Float64)
  return 2.0
end

# zero: 5.0
function f2(x::Float64)
  return x^2 - x - 20.0
end

function pf2(x::Float64)
  return 2.0*x - 1.0
end

# zero: pi
function f3(x::Float64)
  return sin(x)
end

function pf3(x::Float64)
  return cos(x)
end

println("Test metody bisekcji:")
result_bisection_1 = mbisekcji(f1, float64(-8), float64(13), nextfloat(0.0), nextfloat(0.0))
println(result_bisection_1)
result_bisection_2 = mbisekcji(f2, float64(-2), float64(13), nextfloat(0.0), nextfloat(0.0))
println(result_bisection_2)
result_bisection_3 = mbisekcji(f3, float64(1.0), float64(4.0), nextfloat(0.0)*2, 0.00001)
println(result_bisection_3)


println("Test metody Newtona:")
result_newton_1 = mstycznych(f1, pf1, float64(1.0), nextfloat(0.0), nextfloat(0.0), int32(10000))
println(result_newton_1)
result_newton_2 = mstycznych(f2, pf2, float64(1.0), nextfloat(0.0), nextfloat(0.0), int32(10000))
println(result_newton_2)
result_newton_3 = mstycznych(f3, pf3, float64(3.0), nextfloat(0.0), nextfloat(0.0), int32(10000))
println(result_newton_3)


println("Test metody siecznych:")
result_secant_1 = msiecznych(f1, float64(0), float64(13), nextfloat(0.0)*10000, nextfloat(0.0)*10000, int32(10000))
println(result_secant_1)
result_secant_2 = msiecznych(f2, float64(1.0), float64(9.0), nextfloat(0.0)*10000, nextfloat(0.0)*10000, int32(10000))
println(result_secant_2)
result_secant_3 = msiecznych(f3, float64(1.0), float64(4.0), nextfloat(0.0)*10000, nextfloat(0.0)*10000, int32(10000))
println(result_secant_3)
