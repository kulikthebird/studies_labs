sep(T).
id(T).
key(T).
int(T).

name_tokens([TOKEN|LIST], Y) :- 
	( (LIST==[], Y2=[]); (LIST\=[], name_tokens(LIST, Y2)) ),
	( (member(TOKEN, ['read', 'write', 'if', 'then', 'else', 'fi', 'while', 'do', 'od', 'and', 'or', 'mod']), append([key(TOKEN)], Y2, Y));
	(member(TOKEN, [';', '+', '-', '*', '/', '(', ')', '<', '>', '=<', '>=', ':=', '=', '/=']), append([sep(TOKEN)], Y2, Y));
	(integer(TOKEN), append([int(TOKEN)], Y2, Y));
	(append([id(TOKEN)], Y2, Y)) ).


scanner(X, Y)    :- atomic_list_concat(T, ' ', X),
	name_tokens(T, Y).


test2 :- name_tokens(['ala', 'wer', 'sdf', '/'], Y), write(Y).
test :- name_tokens([], Y), write(Y).
% do_it  :- open(’program’, read, X), scanner(X, Y), close(X), write(Y).
