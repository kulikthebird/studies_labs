# autor: Tomasz Kulik

function macheps_float(typ)
  actual = convert(typ, 1.0)
  previous = convert(typ, 1.0)
  while convert(typ, 1.0) + actual != convert(typ, 1.0)
    previous = actual
    actual = actual / 2
  end
  return previous
end

function eta_float(typ)
  actual = convert(typ, 1.0)
  previous = float16(1.0)
  while convert(typ, 0.0) + actual != 0.0
    previous = actual
    actual = actual / 2
  end
  return previous
end


println("Wyznaczenie maszynowego epsilonu.")
println("Typ Float16")
println("Wynik funkcji iteracyjnej: ", macheps_float(Float16))
println("Wynik funkcji eps(Float16): ", eps(Float16))
println("Typ Float32")
println("Wynik funkcji iteracyjnej: ", macheps_float(Float32))
println("Wynik funkcji eps(Float32): ", eps(Float32))
println("Typ Float64")
println("Wynik funkcji iteracyjnej: ", macheps_float(Float64))
println("Wynik funkcji eps(Float64): ", eps(Float64))

println("Wyznaczenie wartości eta.")
println("Typ Float16")
println("Wynik funkcji iteracyjnej: ", eta_float(Float16))
println("Wynik funkcji nextfloat(float16(0.0)): ", nextfloat(float16(0.0)))
println("Typ Float32")
println("Wynik funkcji iteracyjnej: ", eta_float(Float32))
println("Wynik funkcji nextfloat(float32(0.0)): ", nextfloat(float32(0.0)))
println("Typ Float64")
println("Wynik funkcji iteracyjnej: ", eta_float(Float64))
println("Wynik funkcji nextfloat(float64(0.0)): ", nextfloat(float64(0.0)))
