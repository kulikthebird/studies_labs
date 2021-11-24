# autor: Tomasz Kulik

include("lista4-modul.jl")
using lista4

#Zmienna n[5,10,15]
n=int(5)
n=int(10)
#n=int(15)

#Test A
a=float64(-1)
b=float64(1)
#Wynik
rysujNnfx(x -> abs(x),a,b,n)


#Test B(zjawisko Rungeg'o)
a=float64(-5)
b=float64(5)
#Wynik
rysujNnfx(x -> 1/(1+x^2),a,b,n)
