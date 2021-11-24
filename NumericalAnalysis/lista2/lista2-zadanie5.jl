# autor: Tomasz Kulik

function compute_x(x0::Float64, c::Float64, n::Int)
  result = zeros(Float64, n)
  x = x0
  for i=0:(n-1)
    x = x^2 + c
    result[i+1] = x
  end
  return result
end


println(compute_x(1.0, -2.0, 40))
println()
println(compute_x(2.0, -2.0, 40))
println()
println(compute_x(1.99999999999999, -2.0, 40))
println()
println(compute_x(1.0, -1.0, 40))
println()
println(compute_x(-1.0, -1.0, 40))
println()
println(compute_x(0.75, -1.0, 40))
println()
println(compute_x(0.25, -1.0, 40))
println()

"""
1. c = −2 i x0 = 1
2. c = −2 i x0 = 2
3. c = −2 i x0 = 1.99999999999999
4. c = −1 i x0 = 1
5. c = −1 i x0 = −1
6. c = −1 i x0 = 0.75
7. c = −1 i x0 = 0.25
"""

