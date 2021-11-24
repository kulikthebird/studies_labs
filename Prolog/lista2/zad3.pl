arc(a, b).
arc(b, a).
arc(b, c).
arc(c, d).

jajco(X, Y, L) :- arc(X, Y);
    (arc(X, Z), not(member(Z, L))) -> append(L, [X], K), jajco(Z, Y, K).
osiagalny(X, Y) :- jajco(X, Y, []).
