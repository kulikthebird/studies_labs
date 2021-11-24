# autor: Tomasz Kulik


using systems

function get_result(A, b, dokl)
	n = size(A, 1)
	println("kurwa1 ", A)
	(x1, err) = Gauss(A, b, true)
	println("kurwa2 ", A)
	if err == 1
		println("Blad w funkcji Gauss")
	end
	(LU, ipvt, err) = rozkladLU(A, true)
	if err == 1
		println("Blad w funkcji rozkladLU")
	end
	x2 = LUxb(LU, true, b, ipvt)
	println(A\b)
	println(x1)
	println(x2)
	println("||x1-dokl||/||dokl||=", norm(x1-dokl)/norm(dokl))
	println("||x2-dokl||/||dokl||=", norm(x2-dokl)/norm(dokl))
	println()
end

############# Zadanie 1
A = convert(Matrix{Float64}, zeros(Float64, 3, 3))
b = convert(Vector{Float64}, zeros(Float64, 3))
A[1, 1] = 2
A[1, 2] = -2
A[1, 3] = 0
A[2, 1] = -2
A[2, 2] = 0
A[2, 3] = 2
A[3, 1] = 0
A[3, 2] = -2
A[3, 3] = 0
b[1] = 6
b[2] = 4
b[3] = 2
get_result(A, b, A\b)
############# koniec Zadania 1

############# Zadanie 2
A = convert(Matrix{Float64}, zeros(Float64, 4, 4))
b = convert(Vector{Float64}, zeros(Float64, 4))
A[1, 1] = 0
A[1, 2] = 2
A[1, 3] = -1
A[1, 4] = -2
A[2, 1] = 2
A[2, 2] = -2
A[2, 3] = 4
A[2, 4] = -1
A[3, 1] = 1
A[3, 2] = 1
A[3, 3] = 1
A[3, 4] = 1
A[4, 1] = -2
A[4, 2] = 1
A[4, 3] = -2
A[4, 4] = 1
b[1] = -7
b[2] = 6
b[3] = 10
b[4] = -2
get_result(A, b, A\b)
############# koniec Zadania 2

############# Zadanie 3
A = convert(Matrix{Float64}, zeros(Float64, 3, 3))
b = convert(Vector{Float64}, zeros(Float64, 3))
A[1, 1] = 2
A[1, 2] = -2
A[1, 3] = 0
A[2, 1] = -2
A[2, 2] = 0
A[2, 3] = 2
A[3, 1] = 0
A[3, 2] = -2
A[3, 3] = 0
b[1] = 6
b[2] = 0
b[3] = -2
get_result(A, b, A\b)
############# koniec Zadania 3