# autor: Tomasz Kulik


function Gauss(A::Matrix{Float64}, b::Vector{Float64}, pivot::Bool)
	n = size(A, 1)
	ipvt = convert(Vector{Int}, reshape(1:(n-1), n-1))
	U = zeros(Float64, n, n)
	b2 = zeros(Float64, n)
	U += A
	b2 += b

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
				temp = b2[max]
				b2[max] = b2[k]
				b2[k] = temp
			end
			
			if U[k, k] == 0 || abs(U[k, k]) < eps(Float64)
				return (0, 1)
			end
			
			for i=(k+1):n
				L = U[i, k] / U[k, k]
				for j=(k):n
					U[i, j] = U[i, j] - L*U[k, j]
				end
				b2[i] = b2[i] - L*b2[k]
			end
		end
	
	else
		for x=1:n
			if U[x, x] == 0 || abs(U[x, x]) < eps(Float64)
        println(U[x,x])
				return (0, 1)
			end
		end
		for k=1:(n-1)
			for i=(k+1):n
				L = U[i, k] / U[k, k]
				for j=(k):n
					U[i, j] = U[i, j] -  L*U[k, j]
				end
				b2[i] = b2[i] - L*b2[k]
			end
		end
	end

	x = zeros(Float64, n)
	for i=n:-1:1
		sum = float64(0.0)
		for j=(i+1):n
			sum = sum + (U[i, j]*x[j])
		end
		x[i] = (b2[i] - sum) / U[i, i]
	end

	return (x, 0)
end
