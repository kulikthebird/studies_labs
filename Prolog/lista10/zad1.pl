sep(T).
id(T).
key(T).
int(T).


program --> [].
program --> instrukcja, [;], program.
instrukcja --> identyfikator, [:=], wyrazenie.
instrukcja --> [read], identyfikator.
instrukcja --> [write], wyrazenie.
instrukcja --> [if], warunek, [then], program, [fi].
instrukcja --> [if], warunek, [then], program, [else], program, [fi].
instrukcja --> while, warunek, [do], program, [od].
wyrazenie --> skladnik, [+], wyrazenie.
wyrazenie --> skladnik, [-], wyrazenie.
wyrazenie --> skladnik.
skladnik --> czynnik, [*], skladnik.
skladnik --> czynnik, [/], skladnik.
skladnik --> czynnik, [mod], skladnik.
skladnik --> czynnik.
czynnik --> identyfikator.
czynnik --> liczba_naturalna.
czynnik --> ["("], wyrazenie, [")"].
warunek --> koniunkcja, [or], warunek.
warunek --> koniunkcja.
koniunkcja --> prosty, [and], koniunkcja.
koniunkcja --> prosty.
prosty --> wyrazenie, [=],  wyrazenie.
prosty --> wyrazenie, [/=], wyrazenie.
prosty --> wyrazenie, [<],  wyrazenie.
prosty --> wyrazenie, [>],  wyrazenie.
prosty --> wyrazenie, [>=], wyrazenie.
prosty --> wyrazenie, [=<], wyrazenie.
prosty --> ( warunek ).


name_tokens([TOKEN|LIST], Y) :-
    ( (LIST==[], Y2=[]); (LIST\=[], name_tokens(LIST, Y2)) ),
    ( (member(TOKEN, ["read", "write", "if", "then", "else", "fi", "while", "do", "od", "and", "or", "mod"]), append([key(TOKEN)], Y2, Y));
    (member(TOKEN, [";", "+", "-", "*", "/", "(", ")", "<", ">", "=<", ">=", ":=", "=", "/="]), append([sep(TOKEN)], Y2, Y));
    (integer(TOKEN), append([int(TOKEN)], Y2, Y));
    (append([id(TOKEN)], Y2, Y)) ).


check_if_token([LIST], K) :-
	member(K, LIST).
	%(T == K, R = 2); (T \= K, append(K, _, T), R = 1); (T \= K, check_if_token(LIST, K, R)).

alpha_string([L|R]) :-
	(is_alpha(L),
	((R \= [], alpha_string(R)) ; R == [])).

int_string([L|R]) :-
	(L >= 48, L =< 57,
	( (R \= [], int_string(R)) ; R == []) ).

tokenit([T|REST], A, RESULT) :- 
	(
	( not(is_alpha(T)), check_if_token(["read","write","if","then","else","fi","while","do","od","and","or","mod"], A), atom_codes(B, A), append([key(B)], RESULT2, RESULT), A2 = [] );
	( (alpha_string(T) ; int_string(T)), check_if_token([";","+","-","*","/","(",")","<",">","=<",">=",":=","=","/="], A), atom_codes(B, A), append([sep(B)], RESULT2, RESULT), A2 = [] );
	( not(int_string(T)), A \= [], int_string(A), atom_codes(B, A), append([int(B)], RESULT2, RESULT), A2 = []);
	( not(is_alpha(T)), A \= [], alpha_string(A), atom_codes(B, A), append([id(B)], RESULT2, RESULT), A2 = [] );
	A2 = A ),
	( (REST \= [], A2 == [], tokenit(REST, [T], RESULT2), RESULT = RESULT2);
	(REST \= [], A2 \= [], append(A2, [T], A3), tokenit(REST, A3, RESULT2), RESULT = RESULT2 );
	(T =:= 36, REST == [], A2 == [], RESULT2 = [])).


scanner(X, Y)    :- atomic_list_concat(T, " ", X),
    name_tokens(T, Y).


test  :- tokenit("Dupa4$", "", R).

% doit  :- open("program.prog", read, X), scanner(X, Y), close(X), write(Y).

%test :- check_if_token(["read", "write", "if", "then", "else", "fi", "while", "do", "od", "and", "or", "mod"], "b", R).

% test :- scanner().


