prime(LO, HI, N) :- N > LO-1, N < HI+1, S is round(sqrt(N))+1, forall(between(2, S, I), N mod I =\= 0).
