# autor: Tomasz Kulik

function compute_b(A, n)
  x = ones(Float64, n)
  return *(A,x)
end

function hilb(n::Int)
# Function generates the Hilbert matrix  A of size n,
#  A (i, j) = 1 / (i + j - 1)
# Inputs:
#	n: size of matrix A, n>0
#
#
# Usage: hilb (10)
#
# Pawel Zielinski
        if n < 1
         error("size n should be > 0")
        end
        A= Array(Float64, n,n)
        for j=1:n, i=1:n
                A[i,j]= 1 / (i + j - 1)
        end
        return A
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



# Podpunkt a)
println("Obliczanie rownania b = Ax przy podanej macierzy Hilberta przy pomocy eliminacji Gausa oraz wzoru x=inv(A)*b")
for n=2:6
  A = hilb(n)
  b = compute_b(A, n)
  accurate_x = ones(Float64, n)
  x = A\b - accurate_x
  for i=1:n
    x[i] = abs(x[i]) / accurate_x[i]
  end
  println(x)


  x = *(inv(A), b) - accurate_x
  for i=1:n
    x[i] = abs(x[i]) / accurate_x[i]
  end
  println(x)
  println()
end

# Podpunkt b)
println("Obliczanie rownania b = Ax przy podanej macierzy o okreslonym wsp. conditional przy pomocy eliminacji Gausa oraz wzoru x=inv(A)*b")
function compute_x(n, c)
  A = matcond(n, float64(c))
  b = compute_b(A, n)
  accurate_x = ones(Float64, n)
  x = A\b - accurate_x
  for i=1:n
    x[i] = abs(x[i]) / accurate_x[i]
  end
  println(x)

  x = *(inv(A), b) - accurate_x
  for i=1:n
    x[i] = abs(x[i]) / accurate_x[i]
  end
  println(x)
  println()
end

compute_x(5, 10^0)
compute_x(5, 10^1)
compute_x(5, 10^3)
compute_x(5, 10^7)
compute_x(5, 10^12)
compute_x(5, 10^16)

compute_x(10, 10^0)
compute_x(10, 10^1)
compute_x(10, 10^3)
compute_x(10, 10^7)
compute_x(10, 10^12)
compute_x(10, 10^16)

compute_x(20, 10^0)
compute_x(20, 10^1)
compute_x(20, 10^3)
compute_x(20, 10^7)
compute_x(20, 10^12)
compute_x(20, 10^16)
