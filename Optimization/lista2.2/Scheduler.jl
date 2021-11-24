using JuMP

function buildScheduleConstraints(model::JuMP.Model, taskExecutionTimeArray::Array{Int64, 1},
        resourcesRequirements::Array{Array{Int64, 1}, 1}, limitOfResource::Array{Int64, 1})
  n = length(taskExecutionTimeArray)
  M = sum(taskExecutionTimeArray)+1
  @variable(model, B[1:n, 1:n], category=:Bin)
  @variable(model, ends[1:n], lowerbound = 0)
  @variable(model, beginning[1:n], lowerbound = 0)
  for t1 in 1:n
    @constraint(model, ends[t1] - beginning[t1] == taskExecutionTimeArray[t1])
    for t2 in 1:n
      @variable(model, C, Bin)
      @constraint(model, beginning[t2] <= beginning[t1] + (1-B[t2, t1])*M)
      @constraint(model, ends[t2] +1 >= beginning[t1] - (1-B[t2, t1])*M)
      @constraint(model, beginning[t2] -1 >=  beginning[t1] - C*M - B[t2, t1]*M)
      @constraint(model, ends[t2]  <= beginning[t1] + (1-C)*M + B[t2, t1]*M)
    end
    for i in 1:length(limitOfResource)
      @constraint(model, vecdot(B[:, t1], resourcesRequirements[i]) <= limitOfResource[i])
    end
  end
  return beginning, ends, B
end
