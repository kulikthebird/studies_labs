size(4).

table(Q) --> table_begin, rows(Q) ,table_end.
table_begin --> "<table>".
table_end --> "</table>".
rows([Index|Rest]) --> row(Index), rows(Rest).
rows([]) --> [].
row(Index) --> row_begin, {size(Size)}, cols(Index, Size), row_end.
row_begin --> "<tr>".
row_end --> "</tr>".
cols(K, I) --> {I > 0}, col(K, I), {NewIndex is I - 1}, cols(K, NewIndex).
cols(_, 0) --> [].
col(K, X) --> {(K == X)}, "<td>", "<p><img src='pic/queen.png'></p>", "</td>".
col(K, X) --> {(K =\= X)}, "<td>", "<p><img src='pic/empty.png'></p>", "</td>".


test :- phrase( table([0,1,2,3]), T), string_to_list(Out, T).