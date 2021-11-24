# autor: Tomasz Kulik


using systems

function get_result(A, b, dokl)
	n = size(A, 1)
	(x1, err) = Gauss(A, b, false)
	if err == 1
		println("Blad w funkcji Gauss")
	end
	(LU, ipvt, err) = rozkladLU(A, false)
	if err == 1
		println("Blad w funkcji rozkladLU")
	end
	x2 = LUxb(LU, false, b, ipvt)
	println("||x1-dokl||/||dokl||=", norm(x1-dokl)/norm(dokl))
	println("||x2-dokl||/||dokl||=", norm(x2-dokl)/norm(dokl))
end

function compute_b(A, n)
  x = ones(Float64, n)
  return *(A,x)
end

function matcond(n::Int, c::Float64)
# Function generates a random square matrix A of size n with
# a given condition number c.
# Inputs:
#	n: size of matrix A, n>1
#	c: condition of matrix A, c>= 1.0
#
# Usage: matcond (10, 100.0);
#
# Pawel Zielinski
        if n < 2
         error("size n should be > 1")
        end
        if c< 1.0
         error("condition number  c of a matrix  should be >= 1.0")
        end
        (U,S,V)=svd(rand(n,n))
        return U*diagm(linspace(1.0,c,n))*V'
end

println("Obliczanie rownania b = Ax przy podanej macierzy o okreslonym wsp. conditional przy pomocy eliminacji Gausa")
function compute_x(n, c)
  A = matcond(n, float64(c))
  b = compute_b(A, n)
  accurate_x = ones(Float64, n)
  get_result(A, b, accurate_x)
end

for i in [5, 10, 20]
	println("n=", i, " c=10^0")
	compute_x(i, 10^0)
	println("n=", i, " c=10^1")
	compute_x(i, 10^1)
	println("n=", i, " c=10^3")
	compute_x(i, 10^3)
	println("n=", i, " c=10^7")
	compute_x(i, 10^7)
	println("n=", i, " c=10^12")
	compute_x(i, 10^12)
	println("n=", i, " c=10^16")
	compute_x(i, 10^16)
	println()
end