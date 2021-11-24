# autor: Tomasz Kulik

delta = float64(2^-52.0)
x = float64(1)
println("Dla przedziału[1,2]:")
println("delta = 2^-52")
println(bits(x))

for y=1:4
x=float64(1)+y*delta
println(bits(x))
end

delta = delta / 2.0
x = float64(0.5)
println("Dla przedziału[0.5,1]:")
println("delta = 2^-52 / 2")
println(bits(x))

for y=1:4
x=0.5 +y*delta
println(bits(x))
end

delta = delta * 4.0
x = float64(2)
println("Dla przedziału[2,4]:")
println("delta = 2^-52 * 2")
println(bits(x))

for y=1:4
x=2+y*delta
println(bits(x))
end

println("Liczba 4 w postaci bitowe:")
println(bits(float64(4)))
