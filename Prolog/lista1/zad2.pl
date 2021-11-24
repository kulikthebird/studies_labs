on(b3, b2).
on(b2, b1).

above(X, Y) :- on(X, Y).
above(X, Y) :- on(X, Z), above(Z, Y).
