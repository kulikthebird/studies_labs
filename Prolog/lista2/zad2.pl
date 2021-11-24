jednokrotnie(X, L) :-  select(X, L, LX), not(member(X, LX)).
dwukrotnie(X, L) :- select(X, L, LX1), select(X, LX1, LX2), not(member(X, LX2)).
