# autor: Tomasz Kulik

function scalar_product_up(x, y, typ)
  result = convert(typ, 0.0)
  for i=1:length(x)
    result += convert(typ, x[i]) * convert(typ, y[i])
  end
  return result
end

function scalar_product_down(x, y, typ)
  result = convert(typ, 0.0)
  for i=length(x):-1:1
    result += convert(typ, x[i]) * convert(typ, y[i])
  end
  return result
end


function scalar_product_split1(x, y, typ)
  mul_result = typ[]
  for i=1:length(x)
    push!(mul_result, convert(typ, x[i]) * convert(typ, y[i]))
  end
  mul_result = sort(mul_result)
  addition_part_minus = convert(typ, 0.0)
  addition_part_plus = convert(typ, 0.0)
  middle = 0
  for i=1:length(mul_result)
    if(mul_result[i] >= 0.0)
      middle = i
      break
    end
  end
  for i=1:(middle-1)
    addition_part_minus += mul_result[i]
  end
  for i=length(mul_result):-1:middle
    addition_part_plus += mul_result[i]
  end
  return addition_part_minus + addition_part_plus
end


function scalar_product_split2(x, y, typ)
  mul_result = typ[]
  for i=1:length(x)
    push!(mul_result, convert(typ, x[i]) * convert(typ, y[i]))
  end
  mul_result = sort(mul_result)
  addition_part_minus = convert(typ, 0.0)
  addition_part_plus = convert(typ, 0.0)
  middle = 0
  for i=1:length(mul_result)
    if(mul_result[i] >= 0.0)
      middle = i
      break
    end
  end
  for i=(middle-1):-1:1
    addition_part_minus += mul_result[i]
  end
  for i=middle:length(mul_result)
    addition_part_plus += mul_result[i]
  end
  return addition_part_minus + addition_part_plus
end


x = [2.718281828, -(3.141592654), 1.414213562, 0.5772156649, 0.3010299957]
y = [1486.2497, 878366.9879, -(22.37492), 4773714.647, 0.000185049]
fact = -(1.0065710700000010e-11)


a = scalar_product_up(x, y, Float64)
b = scalar_product_down(x, y, Float64)
c = scalar_product_split1(x, y, Float64)
d = scalar_product_split2(x, y, Float64)

println("Dokładność Float64")
println("Pierwsza wersja iloczynu: ", a, " Różnica z wartością prawdziwą: ", abs(fact - a))
println("Druga wersja iloczynu: ", b, " Różnica z wartością prawdziwą: ", abs(fact - b))
println("Trzecia wersja iloczynu: ", c, " Różnica z wartością prawdziwą: ", abs(fact - c))
println("Czwarta wersja iloczynu: ", d, " Różnica z wartością prawdziwą: ", abs(fact - d))

fact = convert(Float32, fact)
a = scalar_product_up(x, y, Float32)
b = scalar_product_down(x, y, Float32)
c = scalar_product_split1(x, y, Float32)
d = scalar_product_split2(x, y, Float32)

println("Dokładność Float32")
println("Pierwsza wersja iloczynu: ", a, " Różnica z wartością prawdziwą: ", abs(fact - a))
println("Druga wersja iloczynu: ", b, " Różnica z wartością prawdziwą: ", abs(fact - b))
println("Trzecia wersja iloczynu: ", c, " Różnica z wartością prawdziwą: ", abs(fact - c))
println("Czwarta wersja iloczynu: ", d, " Różnica z wartością prawdziwą: ", abs(fact - d))
