ojciec(franczesko, zdzichu).
ojciec(zdzichu, stanislaw).
ojciec(zdzichu, luiza).

rodzic(franczesko, zdzichu).
rodzic(zdzichu, stanislaw).
rodzic(zdzichu, luiza).
rodzic(elizabeth, stanislaw).
rodzic(elizabeth, luiza).
rodzic(krystyna, elizabeth).

matka(elizabeth, stanislaw).
matka(elizabeth, luiza).
matka(krystyna, elizabeth).

mezczyzna(franczesko).
mezczyzna(zdzichu).
mezczyzna(stanislaw).

kobieta(elizabeth).
kobieta(krystyna).
kobieta(luiza).

jest_matka(X) :- kobieta(X), matka(X, Z).
jest_ojcem(X) :- mezczyzna(X), ojciec(X, Z).
jest_synem(X) :- rodzic(X, Y), mezczyzna(X).
siostra(X, Y) :- rodzic(Z, Y), rodzic(Z, X), kobieta(X), X \= Y.
dziadek(X, Y) :- rodzic(X, Z), rodzic(Z, Y), mezczyzna(X).
rodzeństwo(X, Y) :- rodzic(Z, X), rodzic(Z, Y), X \= Y.
