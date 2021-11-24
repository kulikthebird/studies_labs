g1 --> g(a, N), g(b, N).
g2 --> g(a, N), g(b, N), g(c, N).
g3 --> g(a, N), { fib(N, M) }, g(b, M).

g(A, N) --> { N = 0 }; [A], g(A, M), { N is M + 1 }.

fib(N, M) :-
  N < 2 ->
    M = N;
  fib(N, 1, 1, M).
fib(N, A1, A2, M) :-
  N = 2 ->
    M = A2;
  N1 is N - 1,
  A3 is A1 + A2,
  fib(N1, A2, A3, M).

p([]) --> [].
p([X|Xs]) --> [X], p(Xs).

% phrase(p(L1), L2, L3) <=> append(L1, L3, L2)