using JuMP
using GLPKMathProgInterface

function buildA(graph::Array{Array{Int64, 1}, 1})
  maxNode = max(length(graph), reduce((x, y) -> max(x, reduce(max, 0, y)), 0, graph))
  A = zeros(maxNode, maxNode)
  for i=1:maxNode
    for x in graph[i]
      if x == 0
        break
      end
      A[i, x] = 1/length(graph[i])
    end
  end
  return A
end

function calculateP(graph::Array{Array{Int64, 1}, 1}, alpha::Float64)
  A = buildA(graph)
  n = size(A)[1]
  B = ones(n, n)
  P = (1.0-alpha) * A + (alpha / n) * B
  return P
end

function stationaryDist(P::Matrix)
  model = Model(solver = GLPKSolverLP())
  @variable(model, pi[1:size(P)[1]])
  @constraint(model, P'*pi .== pi)
  @constraint(model, sum(pi) == 1.0)
  @objective(model, Max, sum(pi))
  solve(model)
  println(getvalue(pi))
end


graph = [[1], [3, 5], [1], [2, 5], [4], [3]]
# alphas = [0.0, 0.15, 0.5, 1.0]
alphas = [0.5]
for a in alphas
  display("dla alpha = $a")
  P = calculateP(copy(graph), a)
  display(stationaryDist(P))
  display(P)
end
