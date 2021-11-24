:- use_module(library('sgml')), use_module(library('xpath')).

serwery(NazwaPliku, ListaSerwerow) :- 
	load_html(NazwaPliku, T, []),
	findall(Z, (xpath(T, //a(@href), X), parse_url(X, 'http://localhost/', [ _, host(Z), _])), L),
	list_to_set(L, ListaSerwerow).