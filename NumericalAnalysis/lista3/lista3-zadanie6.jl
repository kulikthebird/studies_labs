# autor: Tomasz Kulik

include("ZerosCounter/src/ZerosCounter.jl")

function f1(x::Float64)
  return exp(1-x) - 1
end

function pf1(x::Float64)
  return -exp(1-x)
end

function f2(x::Float64)
  return x*exp(-x)
end

function pf2(x::Float64)
  return -exp(-x) * (-1.0+x)
end

result_f1_1 = mbisekcji(f1, 0.0, 3.0, 0.00001, 0.00001)
result_f1_2 = mstycznych(f1, pf1, 0.0, 0.00001, 0.00001, int32(1000000))
result_f1_3 = msiecznych(f1, 0.0, 3.0, 0.00001, 0.00001, int32(10000))

println("Miejsce zerowe f1 obliczone metodą bisekcji:   ",      result_f1_1)
println("Miejsce zerowe f1 obliczone metodą Newtona:    ",      result_f1_2)
println("Miejsce zerowe f1 obliczone metodą siecznych:  ",      result_f1_3)


result_f2_1 = mbisekcji(f2, -1.0, 2.0, 0.00001, 0.00001)
result_f2_2 = mstycznych(f2, pf2, -1.0, 0.00001, 0.00001, int32(1000000))
result_f2_3 = msiecznych(f2, -1.0, 2.0, 0.00001, 0.00001, int32(10000))

println("Miejsce zerowe f2 obliczone metodą bisekcji:   ",      result_f2_1)
println("Miejsce zerowe f2 obliczone metodą Newtona:    ",      result_f2_2)
println("Miejsce zerowe f2 obliczone metodą siecznych:  ",      result_f2_3)
