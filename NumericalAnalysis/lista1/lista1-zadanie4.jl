# autor: Tomasz Kulik

function macheps_float64()
  actual = float64(1.0)
  previous = float64(1.0)
  while float64(1.0) + actual != 1.0
    previous = actual
    actual = actual / 2
  end
  return previous
end

function foo()
  meps = macheps_float64()
  x = float64(1.0)
  while x * (float64(1.0)/x) == 1.0
    x = x + meps
  end
  return x
end

println("Najmniejsza liczba, która nie spełnia równania x*(1/x)=1 w arytmetyce zmiennoprzecinkowej Float64 to: ", foo())
