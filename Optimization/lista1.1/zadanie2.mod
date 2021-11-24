/* Tomasz Kulik */


param n integer >= 2; 

set V:={1..n};       
set A  within V cross V;         
/* 
parametry modelu 
c - koszt przejazdu po łuku ij
t - czas przejazdu po łuku ij
from - wierzchołek początkowy
to - wierzchołek końcowy
T - maksymalny czas przejazdu
*/
param c{(i,j) in A} >= 0;       
param t{(i,j) in A} >= 0; 
param from > 0, <= n, integer;        
param to > 0, <= n, integer; 
param T > 0; 

/*Zmienna decyzyjna czy dany łuk i,j należy do ścieżki */
var x{(i,j) in A} binary; 


/*funkcja celu - zminimalizowanie kosztów przejazdu przy ograniczonym czasie */
minimize min_cost: sum{(i,j) in A} c[i,j]*x[i,j];
	
/* ograniczenie opisujące połączenie pomiędzy wierzchołkiem początkowym a wierzchołkiem końcowym */
s.t. node{i in V}: sum{(j,i) in A} x[j,i] + (if i = from then 1) = sum{(i,j) in A} x[i,j] + (if i = to then 1);

/* ograniczenie maksymalnego czasu przejazdu */
s.t. time: sum{(i,j) in A}t[i,j]*x[i,j] <= T;

solve;

display '--------Wierzchołek poczatkowy-------';
display from;

display '--------Wierzchołek końcowy-------';
display to;

display '--------koszt-------';
display min_cost;

display '--------czas-------';
display time;

display '--------sciezka----';
display {(i,j) in A} x[i,j]; 
