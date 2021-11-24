using JuMP
using GLPKMathProgInterface


P =[
0.0 3/10 1/10 3/5 ;
1/10 1/10 7/10 1/10 ;
1/10 7/10 1/10 1/10 ;
9/10 1/10 0.0 0.0
]

display(P)

function stationaryDist(P::Matrix{Float64})
  model = Model(solver = GLPKSolverLP())
  @variable(model, pi[1:size(P)[1]])
  @constraint(model, P'*pi .== pi)
  @constraint(model, sum(pi) == 1.0)
  @objective(model, Max, sum(pi))
  solve(model)
  return getvalue(pi)
end

# function foo(P::Matrix{Float64}, epsilon::Float64)
#   model = Model(solver = GLPKSolverLP())
#   @variable(model, pi[1:size(P)[1]])
#   @constraint(model, P'*pi .== pi)
#   @constraint(model, sum(pi) == 1.0)
#   @objective(model, Max, sum(pi))
#   solve(model)
#   return getvalue(pi)
# end

# function foo(P::Matrix{Float64}, epsilon::Float64)
#   upperT = 1
#   dupa = stationaryDist(P)
#   while !(reduce(max, (P^upperT)[1,:] - dupa) <= epsilon)
#     upperT *= 2
#   end
#   lowerT = Int(floor(upperT/2))
#   while upperT - lowerT > 1
#     t = Int(floor(lowerT + (upperT - lowerT) / 2))
#     if reduce(max, (P^t)[1,:] - dupa) <= epsilon
#       upperT = t
#     else
#       lowerT = t
#     end
#   end
#   return upperT -2
# end


function foo(P::Matrix{Float64}, epsilon::Float64)
  t = 1
  P = copy(P)
  Pprim = copy(P)
  while !(reduce(max, P[1,:] - stationaryDist(P)) <= epsilon)
    P *= Pprimd
    t += 1
  end
  return t
end


println(stationaryDist(P))
display(P^32)
display(sum((P^128)[:,4])/ size(P)[1])
println(foo(P, 1/1000))
