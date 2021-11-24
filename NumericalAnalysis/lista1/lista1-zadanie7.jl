# autor: Tomasz Kulik

function f(x)
    return float64(sin(x) + cos(3*x))
end

function derivative(h)
  x0 = float64(1.0)
  return float64((f(x0 + h) - f(x0)) / h)
end

factual_derivative_fx = convert(Float64, 0.116942)

println("Wartość h obliczana wg wzoru: h = 1/2^n")
for i=1:54
  h = float64( float64(1.0) / 2^i )
  result = derivative(h)
  println("Wynik pochodnej przy wartości n=", i, ":", result, ", różnica z faktyczną wartością:", abs(result - factual_derivative_fx), ".")
  println("Wartość 1 + h:", float64(1.0) + h)
end
