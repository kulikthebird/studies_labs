# autor: Tomasz Kulik

function f(x)
  return sqrt(x^2 + float64(1.0)) - float64(1.0)
end

function g(x)
  return x^2 / (sqrt(x^2 + float64(1.0)) + float64(1.0))
end

for i=1:7
  x = float64(1.0) / 8^i
  println("Dla x = ", x)
  println("f(", x, ") = ", f(x))
  println("g(", x, ") = ", g(x))
end
