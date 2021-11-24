/* Tomasz Kulik */


set Airports;
set Company;

param fuel_cost{Airports,Company} >=0; 
param available_fuel{Company} >= 0; 
param needed_fuel{Airports} >= 0;

/*Zmienna decyzyjna - ilośc kupionego paliwa od firm  przez lotniska */
var to_buy{Airports,Company} >= 0; 

/*funkcja celu - zminimalizowanie kosztów paliwa*/
minimize final_cost : sum{a in Airports, c in Company} to_buy[a,c]*fuel_cost[a,c];


/*ograniczenia*/

/*ograniczenie ilości sprzedanego paliwa przez firmy */
s.t. sold_fuel{c in Company}: sum{a in Airports} to_buy[a,c] <= available_fuel[c]; 

/*ograniczenie zapotrzebowania paliwa dla lotnisk */
s.t. airport_request{a in Airports}: sum{c in Company} to_buy[a,c] == needed_fuel[a]; 


solve;


display '---------Sumaryczny koszt paliwa----------';
display 'Ostateczny koszt paliwa:', sum{a in Airports, c in Company} to_buy[a,c]*fuel_cost[a,c];
display '-------Koszt paliwa na poszczegolnych lotniskach------';
display {a in Airports, c in Company} fuel_cost[a,c]*to_buy[a,c];
display '-------Liczba paliwa kupionego od firma na poszczegolnych lotniskach--------';
display {a in Airports, c in Company} to_buy[a,c];
display '--------Wydane paliwo----------';
display sold_fuel;

