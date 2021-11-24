# autor: Tomasz Kulik


using Winston

function rysujNnfx(f,a::Float64,b::Float64,n::Int)
	x = GenerateX(a,b,n)
	ff = GenerateFW(f,x)
	fx = ilorazyRoznicowe(x,ff)
	plot(f, a, b, "xx")
	oplot(y -> warNewton(x, fx, y), a, b, "r")
end

function GenerateX(a,b,n)
	x = zeros(Float64, n+1)
	for k=0:n
		x[k+1] = a + k*(b-a)/n
	end
	return x
end

function GenerateFW(f,x)
  n = length(x)
	result = zeros(Float64, n)
	for k=1:n
		result[k] = f(x[k])
	end
	return result
end
