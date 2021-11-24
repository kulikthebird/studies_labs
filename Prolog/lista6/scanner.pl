scanner(Stream, Tokens) :-
  read_stream_to_codes(Stream, Codes),
  span(space, _, Codes, S),
  lex(S, Tokens).

span(Class, String, Codes, Codes2) :-
  Codes2 = [C|_],
  append(String, Codes2, Codes),
  \+ code_type(C, Class),
  !.
span(_, Codes, Codes, []).

lex([], []).
lex([C|Codes1], [T|Tokens]) :-
  (code_type(C, lower) ->
    span(lower, S, Codes1, Codes2),
    key([C|S], T);
  code_type(C, digit(D)) ->
    span(digit, S, Codes1, Codes2),
    int(S, D, T);
  code_type(C, upper) ->
    span(upper, S, Codes1, Codes2),
    id([C|S], T);
  code_type(C, punct) ->
    span(punct, S, Codes1, Codes2),
    sep([C|S], T)),
  span(space, _, Codes2, Codes3),
  lex(Codes3, Tokens).

key("read", key(read)) :- !.
key("write", key(write)) :- !.
key("if", key(if)) :- !.
key("then", key(then)) :- !.
key("else", key(else)) :- !.
key("fi", key(fi)) :- !.
key("while", key(while)) :- !.
key("do", key(do)) :- !.
key("od", key(od)) :- !.
key("and", key(and)) :- !.
key("or", key(or)) :- !.
key("mod", key(mod)).

int([], I, int(I)).
int([C|S], I, O) :-
  code_type(C, digit(D)),
  J is 10 * I + D,
  int(S, J, O).

id(S, id(T)) :-
  atom_codes(T, S).

sep(";", sep(';')) :- !.
sep("+", sep('+')) :- !.
sep("-", sep('-')) :- !.
sep("*", sep('*')) :- !.
sep("/", sep('/')) :- !.
sep("(", sep('(')) :- !.
sep(")", sep(')')) :- !.
sep("<", sep('<')) :- !.
sep(">", sep('>')) :- !.
sep("=<", sep('=<')) :- !.
sep(">=", sep('>=')) :- !.
sep(":=", sep(':=')) :- !.
sep("=", sep('=')) :- !.
sep("/=", sep('/=')).

