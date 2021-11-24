#####################################################
# Tomasz Kulik
#####################################################

using JuMP
using GLPKMathProgInterface


function findAllDeskDivisions(inputWidthList::Array{Int, 1}, inputStandardWidth::Int)
  inputSortedWidthList = sort(inputWidthList, rev=true)
  outputCombinationList = Array{Tuple{Array{Int, 1}, Int}, 1}()
  currentParametersList = zeros(Int, length(inputWidthList))

  function findDivisions(pivot::Int, currentValue::Int)
    maxPivot = max(0, Int(floor((inputStandardWidth - currentValue) / inputSortedWidthList[pivot])))
    currentParametersList[pivot] = maxPivot
    if pivot >= length(inputSortedWidthList)
      append!(outputCombinationList, [(copy(currentParametersList), inputStandardWidth - currentValue - maxPivot*inputSortedWidthList[pivot])] )
      return
    end
    for i=maxPivot:-1:0
      findDivisions(pivot+1, currentValue + i*inputSortedWidthList[pivot])
      currentParametersList[pivot] -= 1
    end
  end

  findDivisions(1, 0)
  return outputCombinationList
end


deskWidthTypes = [7, 5, 3]
deskRequests = [110, 120, 80]

divisions = findAllDeskDivisions(deskWidthTypes, 22)
# display(divi)

model = Model(solver = GLPKSolverMIP())
variables = []
for i=1:length(divisions)
  append!(variables, [@variable(model, category=:Int, basename="divisionType$(i)", lowerbound = 0)])
end
for i=1:length(deskWidthTypes)
  x = []
  for j=1:length(divisions)
    append!(x, [ divisions[j][1][i] ])
  end
  @constraint(model, dot(x, variables) >= deskRequests[i])
end
@objective(model, Min, dot(variables, map(x -> x[2], divisions)))
solve(model)
println(getobjectivevalue(model))
println(divisions)
println( [ "$(Int(x[1])) * cięcie typu $(x[2])" for x in zip(getvalue(variables), collect(1:length(variables)) ) if x[1] > 0] )
