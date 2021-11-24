#####################################################
# Tomasz Kulik
#####################################################

using JuMP
using GLPKMathProgInterface
using Clp

importall MathProgBase.SolverInterface


function generateHilbertMatrix(n::Int)
	if n < 1
	  error("Size of Hilbert matrix should be > 0")
	end
	A = Array(Float64, n,n)
	for j=1:n, i=1:n
		A[i,j]= 1 / (i + j - 1)
	end
	return A
end

function solveLpProblem(constraintsMatrix::Matrix{Float64},
						coefficientVector::Vector{Float64},
						costVector::Vector{Float64},
						solverInstance::AbstractMathProgSolver)
  (constraintsNumber, varsNumber)=size(constraintsMatrix)
	model = Model(solver = solverInstance)
	@variable(model, x[1:varsNumber]>=0)
	@objective(model, Max, vecdot(costVector, x))
  @constraint(model, constraintsMatrix*x .<= coefficientVector)
	status = solve(model, suppress_warnings=true)
	if status == :Optimal
		 return status, getobjectivevalue(model), getvalue(x)
	else
		return status, nothing, nothing
	end
end

function testWithGivenSolverAndMatrixSize(matSize::Int, solverInstance::AbstractMathProgSolver)
	cstrMatrix = generateHilbertMatrix(matSize)
	coefficient = [reduce(+, cstrMatrix[n,:]) for n=1:size(cstrMatrix)[1]]
	cost = copy(coefficient)
	(status, fval, x)=solveLpProblem(cstrMatrix, coefficient, cost, solverInstance)
	return x
end

function testSolverAccuracy(solverInstance::AbstractMathProgSolver)
	maxAccuracy = 1
	for i=1:10
		result = testWithGivenSolverAndMatrixSize(i, solverInstance)
		maxErr = reduce(max, abs(result-ones(Float64, i)))
		println("Error for ", i, ": ", maxErr)
		if maxErr > 0.01
			maxAccuracy = i
			break
		end
	end
	return maxAccuracy-1
end


println("Maksymalna dokładność dla GLPK to: $(testSolverAccuracy(GLPKSolverLP()))")
println("Maksymalna dokładność dla Clp to: $(testSolverAccuracy(ClpSolver()))")
