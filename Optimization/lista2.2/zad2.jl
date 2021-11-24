using JuMP
using GLPKMathProgInterface
include("Scheduler.jl")
include("Gantt.jl")


# czasy wykonia j-tego zadania
p=[3, 2, 4, 5, 1]
# momenty dostepnosci j-tego zadania
r=[2, 1, 3, 1, 0]
# wagi j-tego zadania
w=[1.0, 1.0, 1.0, 1.0, 1.0]


model = Model(solver=GLPKSolverMIP())
b, c= buildScheduleConstraints(model, p, [ones(Int64, length(p))], [1])
for j in 1:length(p)
	@constraint(model, b[j] >= r[j])
end
@objective(model,Min, vecdot(w, c))
solve(model)
println(getvalue(c))

drawGantt(b, c)
