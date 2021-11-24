size(8).

queens(N, P) :-
	numlist(1, N, L),
	permutation(L, P),
	\+ bad(P).

bad(P) :-
	append(_, [I | L1], P),
	append(L2, [J | _], L1),
	length(L2, K),
	abs(I-J) =:= K+1.
	
range(Low, Low, High).
range(Out,Low,High) :- Low < High, NewLow is Low+1, range(Out, NewLow, High).

iter([X|Rest], O) :- O = X; ( iter(Rest, Z), O = Z).