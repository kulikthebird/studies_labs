# autor: Tomasz Kulik


function LUxb(lu::Matrix{Float64}, pivot::Bool, b::Vector{Float64}, ipvt::Vector{Int})
	n = size(lu, 1)
	b2 = zeros(Float64, n)
	b2 += b
	
	if pivot == true
		for i=1:(n-1)
			temp = b2[i]
			b2[i] = b2[ipvt[i]]
			b2[ipvt[i]] = temp
			for k=i+1:n
				b2[k] = b2[k] - lu[k, i]*b2[i]
			end
		end
	else
		for i=1:(n-1)
			for k=i+1:n
				b2[k] = b2[k] - lu[k, i]*b2[i]
			end
		end
	end

	x = zeros(Float64, n)
	for i=n:-1:1
		sum = float64(0.0)
		for j=(i+1):n
			sum = sum + (lu[i, j]*x[j])
		end
		x[i] = (b2[i] - sum) / lu[i, i]
	end

	return x
end
