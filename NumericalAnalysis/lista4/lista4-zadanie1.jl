# autor: Tomasz Kulik


function ilorazyRoznicowe(x::Vector{Float64}, f::Vector{Float64})
  result = Float64[]
  temp=Float64[]
  size = length(f)
  push!(result,f[1])
  for(i=1:size-1)
    for j = 1:size-i
      push!(temp, Iloraz(f[j],f[j+1],x[j],x[j+i]))
    end
    push!(result,temp[1])
    f = temp
    temp = Float64[]
  end
  return result
end

function Iloraz(f,ff,x,xn)
  return (ff-f)/(xn-x)
end
