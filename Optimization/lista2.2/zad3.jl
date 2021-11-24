using JuMP
using GLPKMathProgInterface
include("Gantt.jl")
include("Scheduler.jl")


p = [1, 2, 1, 2, 1, 1, 3, 6, 2]
R = [
(1, 4), (2, 4), (2, 5), (3, 4),
(3, 5), (4, 6), (4, 7), (5, 7),
(5, 8), (6, 9), (7, 9)
]
m = 3


model = Model(solver=GLPKSolverMIP())
b, c, B = buildScheduleConstraints(model, p, [ones(Int64, length(p))], [m])
@variable(model, maxTime)
for j in 1:length(p)
	@constraint(model, maxTime >= c[j])
end
for r in R
	@constraint(model, c[r[1]] <= b[r[2]])
end
@objective(model,Min, maxTime)
solve(model)

model = Model(solver=GLPKSolverMIP())
@variable(model, machines[1:m, 1:length(p)], Bin)
for i=1:length(p)
	@constraint(model, sum(machines[:, i]) == 1)
end
for i=1:m
	@constraint(model, getvalue(B')*machines[i, :] .<= ones(length(p)))
end
solve(model)

println(getvalue(b))
println(getvalue(c))
display(getvalue(machines))

machines = getvalue(machines)
drawGantt(b, c, sum(machines[i, :]*i for i=1:m))
