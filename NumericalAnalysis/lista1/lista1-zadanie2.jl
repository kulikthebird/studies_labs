# autor: Tomasz Kulik

function macheps_float16(typ)
  return convert(typ, 3.0) * (convert(typ, 4.0)/convert(typ, 3.0) - convert(typ, 1.0) ) - convert(typ, 1.0)
end

println("Wyznaczenie maszynowego epsilonu.")
println("Typ Float16")
println("Wynik funkcji Kahana: ", macheps_float(Float16))
println("Wynik funkcji eps(Float16): ", eps(Float16))
println("Typ Float32")
println("Wynik funkcji Kahana: ", macheps_float(Float32))
println("Wynik funkcji eps(Float32): ", eps(Float32))
println("Typ Float64")
println("Wynik funkcji Kahana: ", macheps_float(Float64))
println("Wynik funkcji eps(Float64): ", eps(Float64))
