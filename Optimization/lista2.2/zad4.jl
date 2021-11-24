using JuMP
using GLPKMathProgInterface
include("Gantt.jl")
include("Scheduler.jl")

# DATA
Precedence = [(1, 2),(1, 3),(1, 4),(2, 5),(3, 6),(4, 6),(4, 7),(5, 8),(6, 8),(7, 8)]
p = [50, 47, 55, 46, 32, 57, 15, 62]
R = [[9,17,11,4,13,7,7,17]]
Limit = [30]


# EXECUTION
model = Model(solver=GLPKSolverMIP())
b, c = buildScheduleConstraints(model, p, R, Limit)
for precedence in Precedence
  @constraint(model, c[precedence[1]] <= b[precedence[2]])
end
@variable(model, maxTime)
for i=1:length(c)
  @constraint(model, maxTime >= c[i])
end
@objective(model,Min, maxTime)

solve(model)
display(getvalue(b))
println(getvalue(c))
drawGantt(b, c)
