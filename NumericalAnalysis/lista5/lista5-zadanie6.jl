# autor: Tomasz Kulik


using systems

function get_result(A, b)
	(x1, err) = Gauss(A, b, true)
	if err == 1
		println("Blad w funkcji Gauss")
	end
	(LU, ipvt, err) = rozkladLU(A, true)
	if err == 1
		println("Blad w funkcji rozkladLU")
	end
	x2 = LUxb(LU, true, b, ipvt)
	dokl = [3, 2, 1]
	println("x1=",x1)
	println("||x1-dokl||/||dokl||=", norm(x1-dokl)/norm(dokl))
	println("x2=",x2)
	println("||x2-dokl||/||dokl||=", norm(x2-dokl)/norm(dokl))
end

#############
A = convert(Matrix{Float64}, zeros(Float64, 3, 3))
b = convert(Vector{Float64}, zeros(Float64, 3))
A[1, 1] =  3282825675.08941
A[1, 2] = -5013081565.65267
A[1, 3] =  3409304728.02911
A[2, 1] =  3256050991.27407
A[2, 2] =  439858221.670267
A[2, 3] = -3005859117.97034
A[3, 1] = -5931951819.47511
A[3, 2] =  4642259422.30978
A[3, 3] = -948447572.032458
b[1] =  3231618621.992
b[2] =  7642010299.1924
b[3] = -9459784185.83823
get_result(A, b)
#############