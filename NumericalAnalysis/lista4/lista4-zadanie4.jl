# autor: Tomasz Kulik


include("lista4-modul.jl")
using lista4

#Zmienna n[5,10,15]
#n=int(5)
n=int(10)
#n=int(15)


#Test A
a=float64(0)
b=float64(1)
#Wynik
rysujNnfx(x->e^x,a,b,n)


#Test B
a=float64(-1)
b=float64(1)
#Wynik
rysujNnfx(x -> ((x^2)*sin(x)),a,b,n)
