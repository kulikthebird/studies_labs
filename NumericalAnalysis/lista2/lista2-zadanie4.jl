# autor: Tomasz Kulik

function population(p0, r, n)
  p = p0
  for i=0:(n-1)
    p = p + r*p*(1-p)
  end
  return p
end

p = float32(0.01)
r = 3
first_result = population(p, r, 40)

p = float32(0.01)
r = 3
second_result = population(p, r, 10)
second_result = floor(second_result*1000) / 1000.0
second_result = population(second_result, r, 30)

println("Wynik 40 iteracji: ", first_result)
println("Wynik 40 iteracji z zakłóceniem przy 10 iteracji: ", second_result)

p = float64(0.01)
r = 3
third_result = population(p, r, 40)
println("Wynik 40 iteracji w dokładności Float64: ", third_result)
