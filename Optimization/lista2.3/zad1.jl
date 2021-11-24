using JuMP
using GLPKMathProgInterface
using PyPlot

function generalisedAssigmentProblem(W::Matrix{Int64}, P::Matrix{Int64}, T::Matrix{Int64})
  machines = size(W)[1]
  jobs = size(W)[2]
  F = zeros(Int64, size(W))
  M = Set{Int64}(1:machines)
  T = copy(T)
  iterations = 0
  Excluded = ones(Int64, size(W))
  for x=1:machines, y=1:jobs
    Excluded[x, y] = P[x, y] > T[x] ? 0 : Excluded[x, y]
  end
  while count(x -> x==1, Excluded) != 0
    iterations += 1
    model = Model(solver=GLPKSolverLP())
    @variable(model, E[1:machines, 1:jobs], lowerbound = 0)
    E = convert(Array{Any, 2}, E)
    for x=1:machines, y=1:jobs
      E[x, y] = Excluded[x, y] == 0? 0 : E[x, y]
    end
    for i in M
      @constraint(model, vecdot(E[i, :] , P[i, :]) <= T[i])
    end
    for i=1:jobs
      if count(x-> x==1, Excluded[:, i]) != 0
        @constraint(model, sum(E[:, i]) == 1)
      end
    end
    @objective(model, Max, vecdot(E[:], W[:]))
    if solve(model) != :Optimal
      println("Nie udało się rozwiązać modelu!")
      return zeros(Int64, size(W))
    end
    E = map(x -> typeof(x) == JuMP.Variable ? getvalue(x) : 0, E)
    for x=1:machines, y=1:jobs
      if E[x, y] == 0
        Excluded[x, y] = 0
      elseif E[x, y] == 1
        Excluded[:, y] = 0
        F[x, y] = 1
        T[x] -= P[x, y]
      end
    end
    for i in M
      if (count(x-> 0<x<1, E[i, :]) == 1) || (count(x-> 0<x<1, E[i, :]) == 2 && sum(E[i, :]) >= 1)
        delete!(M, i)
      end
    end
  end
  return F, iterations
end


function readWholeRow(file, cols)
  numbers = []
  while length(numbers) != cols
    append!(numbers, split(readline(file)))
  end
  return numbers
end


function readInputData(filePath)
  f = open(filePath)
  n = parse(Int64, readline(f))
  result = []
  for k=1:n
    machines, jobs = map(x -> parse(Int64, x), split(readline(f)))
    W = Matrix{Int64}(machines, jobs)
    P = Matrix{Int64}(machines, jobs)
    T = Matrix{Int64}(machines, 1)
    for i=1:machines
      W[i, :] = map(x -> parse(Int, x), readWholeRow(f, jobs))
    end
    for i=1:machines
      P[i, :] = map(x -> parse(Int, x), readWholeRow(f, jobs))
    end
    T[:] = map(x -> parse(Int, x), readWholeRow(f, machines))
    append!(result, [(W, P, T)])
  end
  return result
end


function solveGapForGivenFilesAndOptimalResults(filesAndOptResults::Array{Tuple{String, Matrix{Int64}}, 1})
  cd(dirname(@__FILE__)*"/tests/")
  results = Matrix{Any}(0, 7)
  for file in filesAndOptResults
    dataWPT = readInputData(file[1])
    # println("Wyniki dla pliku $(file[1]):")
    # println("")
    for dat in 1:length(dataWPT)
      println("Plik $(file[1]), zestaw $(dat):")
      W = dataWPT[dat][1]
      P = dataWPT[dat][2]
      T = dataWPT[dat][3]
      m, iterations = generalisedAssigmentProblem(W, P, T)
      cost = vecdot(m[:], W[:])
      deviation = [(vecdot(m[x,:], P[x,:])) / T[x] for x=1:length(T)]
      maxDeviation = maximum(deviation)
      avgDeviation = mean(deviation)
      optimalResults = file[2][dat]
      # println("Koszt: $(cost)")
      # println("Odchylenie od optymalnego kosztu: $(abs(optimalResults-cost))")
      # println("Maksymalne wykorzystanie możliwości maszyn: $(maxDeviation)")
      # println("Średnie wykorzystanie możliwości maszyn: $(avgDeviation)")
      # println()
      results = [ results ; [file[1] dat optimalResults cost maxDeviation avgDeviation iterations] ]
    end
  end
  return results
end


function plotHistogram(optimalCost, apxCost, maxMachineUse, avgMachineUse, iterations)
  close()
  bins = 1:length(optimalCost)
  fig, plt1 = subplots(1)
  plt2 = plt1[:twinx]()
  maxY = maximum(map(x -> maximum(x), [optimalCost ; apxCost]))
  plt1[:vlines](bins, 0, maxMachineUse, linewidth=12.0, color=(0.5, 0, 0))
  plt1[:vlines](bins, 0, avgMachineUse, linewidth=12.0, color=(0, 0, 1))
  plt1[:plot](bins, map(x -> abs(x[1]-x[2]) / x[2], zip(apxCost, optimalCost)), "o-", color=(0, 0.7, 1))
  plt1[:set_ylim](0, 2.0)
  plt2[:plot](bins, optimalCost, "o-", color=(0, 1, 0))
  plt2[:plot](bins, apxCost, "o-", color=(1, 0.6, 0))
  grid(true)
end


input =
[("gap1.txt", [336 327 339 341 326]),
("gap2.txt", [434 436 420 419 428]),
("gap3.txt", [580 564 573 570 564]),
("gap4.txt", [656 644 673 647 664]),
("gap5.txt", [563 558 564 568 559]),
("gap6.txt", [761 759 758 752 747]),
("gap7.txt", [942 949 968 945 951]),
("gap8.txt", [1133 1134 1141 1117 1127]),
("gap9.txt", [709 717 712 723 706]),
("gap10.txt", [958 963 960 947 947]),
("gap11.txt", [1139 1178 1195 1171 1171]),
("gap12.txt", [1451 1449 1433 1447 1446])]

results = solveGapForGivenFilesAndOptimalResults(input)
# open("results.txt", "w") do f
  # write(f, results)
println(results)
 # end

# plotHistogram(results[:,3], results[:,4], results[:,5] ,results[:,6], results[:,7])
