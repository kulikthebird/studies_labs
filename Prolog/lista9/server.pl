:- use_module(library(http/thread_httpd)).
:- use_module(library(http/http_dispatch)).
:- use_module(library(http/http_client)).
:- use_module(library(http/html_write)).
:- use_module(library(http/http_files)).

:- use_module(form).
:- use_module(queens).

:- http_handler(root(queens), handler_queens, []).
:- http_handler(root(solution), handler_solution, []).
:- http_handler(root(.), http_reply_from_files('pic', []), [prefix]).

% server(+Port) uruchomienie serwera na danym porcie
%
server(Port) :-
	http_server(http_dispatch, [port(Port)]).

handler_queens(_Request) :-
	reply_html_page(
		title('queens'),
	    [p(h1('Gimme fuel\n, gimme fire,\n gimme that which i desire')),
	     form([action='/solution', method='POST'], [
		p([], [
		  label([for=size],'size:'),
		  input([name=size, type=text])
		      ]),
		p([], input([name=submit, type=submit, value='Dawaj!'], []))
	      ])]).
	
handler_solution(Request) :-
        member(method(post), Request), !,
        http_read_data(Request, [size=Size | _], []),
		atom_number(Size, N),
		queens(N, P),
		phrase(table(P, N), T),
		string_to_list(Out, T),
		atom_codes(AtomOut, Out),
		format('Content-type: text/html~n~n<html><body><h1>Solution:</h1> <p>~w</p></body></html>', [AtomOut]).
		  		  
table(Q, Size) --> table_begin, rows(Q, Size) ,table_end.
table_begin --> "<table>".
table_end --> "</table>".
rows([Index|Rest], Size) --> row(Index, Size), rows(Rest, Size).
rows([], _) --> [].
row(Index, Size) --> row_begin, cols(Index, Size), row_end.
row_begin --> "<tr>".
row_end --> "</tr>".
cols(K, I) --> {I > 0}, col(K, I), {NewIndex is I - 1}, cols(K, NewIndex).
cols(_, 0) --> [].
col(K, X) --> {(K == X)}, "<td>", "<p><img src='queen.png'></p>", "</td>".
col(K, X) --> {(K =\= X)}, "<td>", "<p><img src='empty.png'></p>", "</td>".