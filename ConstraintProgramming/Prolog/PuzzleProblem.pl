:- use_module(library(clpfd)).


createList([], 0) :- !.
createList([_|Ls], N) :-
    N2 is N - 1,
    createList(Ls, N2).

createMatrix([], _, 0) :- !.
createMatrix([M|Ms], X, Y) :-
    createList(M, X),
    Y2 is Y - 1,
    createMatrix(Ms, X, Y2).

index(Matrix, Row, Col, Value):-
    nth1(Row, Matrix, MatrixRow),
    nth1(Col, MatrixRow, Value).
indexAssign(Matrix, Row, Col, C) :-
    index(Matrix, Row, Col, Val),
    call(C, Val).

forEveryMatElement(C, M) :-
    append(M, Array),
    maplist(C, Array).

matDomain(M, Dom) :-
    append(M, Array),
    Array ins Dom.

inputData(InMat) :-
    InMat =
    [
        [0,0,0,0,0],
        [0,0,1,0,0],
        [0,1,1,1,0],
        [0,0,1,0,0],
        [0,0,0,0,0]
    ].

outputData(OutMat) :-
    OutMat =
    [
        [1,1,1,1,1],
        [1,1,1,1,1],
        [1,1,1,1,1],
        [1,1,1,1,1],
        [1,1,1,1,1]
    ].

for1(I, N, _, _, _) :- I>N, !.
for1(X, N, Input, Moves, Output) :-
    for2(1, N, X, Input, Moves, Output),
    X1 is X+1,
    for1(X1, N, Input, Moves, Output).

for2(I, N, _, _, _, _) :- I>N, !.
for2(Y, N, X, Input, Moves, Output) :-
    index(Input, X, Y, In),
    index(Output, X, Y, Out),
    index(Moves, X, Y, A),
    ((NewX1 is X-1, index(Moves, NewX1, Y, B)) ; B=0),
    ((NewX2 is X+1, index(Moves, NewX2, Y, C)) ; C=0),
    ((NewY3 is Y-1, index(Moves, X, NewY3, D)) ; D=0),
    ((NewY4 is Y+1, index(Moves, X, NewY4, E)) ; E=0),
    Out #= (In+A+B+C+D+E) mod 2,
    Y1 is Y+1,
    for2(Y1, N, X, Input, Moves, Output).

model(Moves) :-
    inputData(Input),
    outputData(Output),
    createMatrix(Moves, 5, 5),
    matDomain(Moves, 0..1),
    for1(1, 5, Input, Moves, Output).

