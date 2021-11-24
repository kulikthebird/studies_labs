# autor: Tomasz Kulik


function rozkladLU(A::Matrix{Float64}, pivot::Bool)
	n = size(A, 1)
	L = zeros(Float64, n, n)
	ipvt = convert(Vector{Int}, reshape(1:(n-1), n-1))
	U = zeros(Float64, n, n)
	U += A

	# Checking if pivot choice mode is on
	if pivot == true
		
		for k=1:(n-1)	
			# Swaping rows (pivot element)
			max = k
			for x=(k+1):n
				if abs(U[x, k]) > abs(U[max, k])
					max = x
				end
			end

			ipvt[k] = max

			if max != k
				for x=k:n
					temp = U[max, x]
					U[max, x] = U[k, x]
					U[k, x] = temp
				end
			end
			
			if U[k, k] == 0 || abs(U[k, k]) < eps(Float64)
				return (0, 0, 1)
			end

			for i=(k+1):n
				L[i, k] = U[i, k] / U[k, k]
				U[i, k] = 0
				for j=(k+1):n
					U[i, j] = U[i, j] - L[i, k]*U[k, j]
				end
			end
		end

	else
		for x=1:n
			if U[x, x] == 0 || abs(U[x, x]) < eps(Float64)
				return (0, 0, 1)
			end
		end
		for k=1:(n-1)
			for i=(k+1):n
				L[i, k] = U[i, k] / U[k, k]
				U[i, k] = 0
				for j=(k+1):n
					U[i, j] = U[i, j] - L[i, k]*U[k, j]
				end
			end
		end
	end
	
	U = U + L
	return (U, ipvt, 0)
end
