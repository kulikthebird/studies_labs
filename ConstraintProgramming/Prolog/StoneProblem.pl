:- use_module(library(clpfd)).


domain1([], 0).
domain1([A|As], W) :-
    X in -1 .. 1,
    W #= X*A + W2,
    domain1(As, W2).

foo2(_, _, _, _, []).
foo2(A, B, C, D, [W|Ws]) :-
    domain1([A,B,C,D], W),
    foo2(A, B, C, D, Ws).

foo(A, B, C, D) :-
        A in 1 .. 40,
        B in 1 .. 40,
        C in 1 .. 40,
        D in 1 .. 40,
        numlist(1, 40, L),
    foo2(A, B, C, D, L).

start([A,B,C,D]) :- foo(A,B,C,D), indomain(A), indomain(B), indomain(C), indomain(D).
