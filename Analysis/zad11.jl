using PyPlot


function plotHistogram(visits, time)
  res_maximum = maximum([maximum(x) for x in visits])
  bins = linspace(1, res_maximum, res_maximum)
  fig, plts = subplots(2)
  h1 = plts[1][:hist](visits, bins, align="left")
  # h2 = plts[2][:bar](map(x -> Int64(x), keys(time)), map(x -> Int64(x), values(time)), align="center")
  grid(true)
end


function pushVisitToPositionDict(visitCountersDict::Dict{Int64, Array{Int64, 1}}, position::Int64, iteration::Int64)
  if !(position in keys(visitCountersDict))
    visitCountersDict[position] = [iteration]
  else
    last = pop!(visitCountersDict[position])
    append!(visitCountersDict[position], [iteration - last, iteration])
  end
end

function queue(lam, mi, n)::Tuple{Array{Int64, 1}, Dict{Int64, Int64}}
  visitCountersDict = Dict{Int64, Array{Int64, 1}}()
  statesList = [0]
  currentState = 0
  for i=1:n-1
    x = rand()
    if x <= lam
      currentState += 1
    elseif lam < x <= mi + lam
      currentState -= 1
    end
    currentState = max(0, currentState)
    pushVisitToPositionDict(visitCountersDict, currentState, n)
    append!(statesList, [currentState])
  end
  return (statesList, map(x->Pair(x[1], Int64(round(mean(x[2])))), visitCountersDict))
end


visits, time = queue(0.6, 0.3, 3000)
println(length(visits))
println(count(x -> x==0, visits))
println(count(x -> x==1, visits))
close()
plotHistogram(visits, time)
