le(a, b).
le(a, c).
le(a, d).
le(b, c).
le(b, d).
le(c, d).

le(a, a).
le(b, b).
le(c, c).
le(d, d).

minimalny(X) :- le(Z, X) -> Z = X.
maksymalny(X) :- le(X, Z) -> Z = X.

help1(X) :- not((le(X, Z), write(Z))).
najmniejszy(X) :- not(help1(X)).
najwiekszy(X) :- le(Z, X), not(X = Z).
