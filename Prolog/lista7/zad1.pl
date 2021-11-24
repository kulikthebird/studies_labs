merge(X, Y, Z) :- freeze(X, freeze(Y, (Y = [] -> Z = X))).
merge(X, Y, Z) :- freeze(X, freeze(Y, (X = [] -> Z = Y))).
merge([Gx|Ox],[Gy|Oy],Z) :-
        ( 
          (
            Gx=<Gy -> Z = [Gx|Oz], 
            X2 = Ox, 
            Y2 = [Gy|Oy]
          )
          ;
           ( Z = [Gy|Oz],
            X2 = [Gx|Ox],
            Y2 = Oy
           )
        ),
        merge(X2, Y2, Oz).



