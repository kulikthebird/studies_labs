# autor: Tomasz Kulik

using m1
using m2
using m3

# Testy pierwszego zadania
x = Float64[0,1,3,6]
f = Float64[-40,-12,2,8]
#1
result = m1.ilorazyRoznicowe(x,f)
if result == []
#2
result = m1.ilorazyRoznicowe([2.0, 3.0, 4.0, 5.0, 6.0], [4.0, 6.0, 8.0, 10.0, 12.0])


# Testy drugiego zadania
#1
x = Float64[3,1,5,6]
f = Float64[1,-3,2,4]
fx = ilorazyRoznicowe(x,f)
t = float64(2)  #x dla naszego wielomianu
#Wynik:
result = m2.warNewton(x,fx,t)
#2
fx = m2.ilorazyRoznicowe([2.0, 3.0, 4.0, 5.0, 6.0], [4.0, 6.0, 8.0, 10.0, 12.0])
m2.warNewton([2.0, 3.0, 4.0, 5.0, 6.0],  fx, 3.0)


# Testy trzeciego zadania
#Zalozenia poczatkowe
n = int(2)
a = float64(2)
b = float64(4)
#Wynik
m3.rysujNnfx(y -> y*2,a,b,n)
