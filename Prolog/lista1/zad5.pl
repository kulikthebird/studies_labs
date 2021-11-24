le(X,Y) :- X=<Y.

czesciowy_porzadek :- foreach((between(1,10,X), between(1,10,Y)),
    (le(X,X),
    not(( X =\= Y, le(X,Y), le(Y,X) )),
    forall(between(1,10,Z), (not((le(X,Y), le(Y,Z))); le(X,Z)) ))).
