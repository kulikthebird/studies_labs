# autor: Tomasz Kulik


function warNewton(x:: Vector{Float64},fx::Vector{Float64},t::Float64)
	k = int64(1)
	n = int64(length(x))
	return Wk(x,fx,t,k,n)
end

function Wk(x:: Vector{Float64},fx::Vector{Float64},t::Float64,k::Int64,n::Int64)
	if(k==n)
		return fx[k]
	end
	return fx[k] + (t-x[k])*Wk(x,fx,t,k+1,n)
end
