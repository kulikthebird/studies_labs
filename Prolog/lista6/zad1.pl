:- ['scanner'].
% program(?PROGRAM, ?LIST, ?END)
program([]) --> [].
program([INSTRUKCJA|PROGRAM]) --> instrukcja(INSTRUKCJA), [sep(;)], program(PROGRAM).
instrukcja(assign(ID, WYRAZENIE)) --> [id(ID)], [sep(':=')], wyrazenie(WYRAZENIE).
instrukcja(read(ID)) --> [key(read)], [id(ID)].
instrukcja(write(WYRAZENIE)) --> [key(write)], wyrazenie(WYRAZENIE).
instrukcja(if(WARUNEK, PROGRAM)) --> [key(if)], warunek(WARUNEK), [key(then)], program(PROGRAM), [key(fi)].
instrukcja(if(WARUNEK, PROGRAM, PROGRAM)) --> [key(if)], warunek(WARUNEK), [key(then)], program(PROGRAM), [key(else)], program(PROGRAM), [key(fi)].
instrukcja(while(WARUNEK, PROGRAM)) --> [key(while)], warunek(WARUNEK), [key(do)], program(PROGRAM), [key(od)].
wyrazenie(WYRAZENIE1 + WYRAZENIE2) --> skladnik(WYRAZENIE1), [sep(+)], wyrazenie(WYRAZENIE2).
wyrazenie(WYRAZENIE1 - WYRAZENIE2) --> skladnik(WYRAZENIE1), [sep(-)], wyrazenie(WYRAZENIE2).
wyrazenie(SKLADNIK) --> skladnik(SKLADNIK).
skladnik(WYRAZENIE1 * WYRAZENIE2) --> czynnik(WYRAZENIE1), [sep(*)], skladnik(WYRAZENIE2).
skladnik(WYRAZENIE1 / WYRAZENIE2) --> czynnik(WYRAZENIE1), [sep(/)], skladnik(WYRAZENIE2).
skladnik(WYRAZENIE1 mod WYRAZENIE2) --> czynnik(WYRAZENIE1), [key(mod)], skladnik(WYRAZENIE2).
skladnik(WYRAZENIE) --> czynnik(WYRAZENIE).
czynnik(id(ID)) --> [id(ID)].
czynnik(int(NUM)) --> [int(NUM)].
czynnik(WYRAZENIE) --> [sep('(')], wyrazenie(WYRAZENIE), [sep(')')].
warunek((WARUNEK1 ; WARUNEK2)) --> koniunkcja(WARUNEK1), [key(or)], warunek(WARUNEK2).
warunek(WARUNEK) --> koniunkcja(WARUNEK).
koniunkcja((WARUNEK1 , WARUNEK2)) --> prosty(WARUNEK1), [key(and)], koniunkcja(WARUNEK2).
koniunkcja(WARUNEK) --> prosty(WARUNEK).
prosty(WYRAZENIE1 =:= WYRAZENIE2) --> wyrazenie(WYRAZENIE1), [sep(=)], wyrazenie(WYRAZENIE2).
prosty(WYRAZENIE1 =\= WYRAZENIE2) --> wyrazenie(WYRAZENIE1), [sep(/=)], wyrazenie(WYRAZENIE2).
prosty(WYRAZENIE1 < WYRAZENIE2) --> wyrazenie(WYRAZENIE1), [sep(<)], wyrazenie(WYRAZENIE2).
prosty(WYRAZENIE1 > WYRAZENIE2) --> wyrazenie(WYRAZENIE1), [sep(>)], wyrazenie(WYRAZENIE2).
prosty(WYRAZENIE1 =< WYRAZENIE2) --> wyrazenie(WYRAZENIE1), [sep(=<)], wyrazenie(WYRAZENIE2).
prosty(WYRAZENIE1 >= WYRAZENIE2) --> wyrazenie(WYRAZENIE1), [sep(>=)], wyrazenie(WYRAZENIE2).
prosty(WARUNEK) --> [sep('(')], warunek(WARUNEK), [sep(')')].
