#####################################################
# Tomasz Kulik
#####################################################

using JuMP
using GLPKMathProgInterface

importall MathProgBase.SolverInterface
include("zad2_data.jl")


function solveLpProblem(constraintsMatrix::Matrix{Float64},
						coefficientVector::Vector{Float64},
						costVector::Vector{Float64},
						solverInstance::AbstractMathProgSolver)
  (constraintsNumber, varsNumber)=size(constraintsMatrix)
	model = Model(solver = solverInstance)
	@variable(model, x[1:varsNumber]>=0)
	@objective(model, Min, vecdot(costVector, x))
  @constraint(model, constraintsMatrix*x .>= coefficientVector)
	status = solve(model, suppress_warnings=true)
	if status == :Optimal
		 return status, getobjectivevalue(model), getvalue(x)
	else
		return status, nothing, nothing
	end
end


function elevatorConstraintMatrixBuilder(n::Int)
	A = zeros(n*n*2, n*2)
	for x=1:n, y=1:2:n*2
		a = (x-1)*n*2
		if x==(y-1)/2+1
			for i=1:2:n*2
				A[a+i+1, y] = -1
				A[a+i, y+1] = -1
				A[a+i+1, y+1] = -1
			end
		else
			A[a+y+1, y] = 1
			A[a+y+1, y+1] = 1
			A[a+y, y+1] = 1
		end
	end
	return A'
end

columns(M) = [ view(M,:,i) for i in 1:size(M, 2) ]

function prepareData{T<:Real}(distance::Array{Array{T, 1}, 1}, currentState::Array{Array{T, 1}, 1}, transportCost::Float64, k::Float64)
	stateVector = reduce((a, b) -> append!(a, b), map((x) -> [-float(x[4]-x[2]), -(float(x[4]-x[2])+float(x[3]-x[1]))], currentState))
	transportCostVector = reduce((a,b) -> append!(a, [b*transportCost, b*transportCost*k]), Array(Float64, 0),
			reduce((a,b) -> append!(a,b), copy(distance)))
	n = size(distance)[1]
	A = elevatorConstraintMatrixBuilder(n)
	status, desc, solution = solveLpProblem(A, stateVector, transportCostVector, GLPKSolverLP())
	outbound = cumsum(reshape(solution, n*2, n))[n*2,:]
	inbound = cumsum(reshape(solution, n*2, n)')[n,:]

	cities = ["Opole", "Brzeg", "Nysa", "Prudnik", "Strzelce Opolskie", "Koźle", "Racibórz"]
	i=0
	println(columns(reshape(solution, n*2, n)))
	for cols in columns(reshape(solution, n*2, n))
		i+=1
		for j=1:length(cols)
			if cols[j] != 0.0
				println("Z ", cities[i], " do ", cities[Integer(ceil(j/2))], " wysłano ", Integer(cols[j]), " dźwigów typu ", (j-1)%2+1)
			end
		end
	end
	println(desc)
end

prepareData(distance, currentState, 1.0, 1.2)
