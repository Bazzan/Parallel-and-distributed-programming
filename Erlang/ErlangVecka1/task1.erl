-module(task1).


-export([eval/1, eval/2, ifTuple/1, map/2, filter/2, split/2]).

eval(X) ->
  case X of
    {Atom, A, B} when is_tuple(A) ->
        eval({Atom, ifTuple(A), B});
    {Atom, A, B} when is_tuple(B) ->
        eval({Atom, A, ifTuple(B)});
    {add, A, B} -> {ok, A + B};
    {sub, A, B} -> {ok, A - B};
    {'div', A, B} -> {ok, A / B};
    {mul, A, B} -> {ok, A * B};
    {_,_,_} -> {error}
  end.
eval(X,M) ->
  case X of  
    {Atom, A, B} when is_tuple(A) ->
      eval({Atom,ifTuple(A), B}, M);

    {Atom, A, B} when is_tuple(B) ->
      eval({Atom, A, ifTuple(B)}, M);

    {Atom, A, B} when is_atom(A) ,is_map_key(A,M) ->
      eval ({Atom, maps:get(A,M), B}, M);

    {Atom, A, B} when is_atom(B), is_map_key(B,M)  ->

      eval ({Atom, A, maps:get(B,M)}, M);
    
    {_Atom, A, _B} when is_atom(A) -> 
      {error, variable_not_found};
    
    {_Atom, _A, B} when is_atom(B) -> 
      {error, variable_not_found};

    {add, A, B} -> {ok, A + B};
    {sub, A, B} -> {ok, A - B};
    {'div', A, B} -> {ok, A / B};
    {mul, A, B} -> {ok, A * B};
    {_,_,_} -> {error, variable_not_found}
  end.


ifTuple(X2) ->
    case X2 of
    {Atom, A, B} when is_tuple(A) ->
      ifTuple({Atom, ifTuple(A), B});
    {Atom, A, B} when is_tuple(B) ->
      ifTuple({Atom, A, ifTuple(B)});

    {add, A, B} -> A + B;
    {sub, A, B} -> A - B;
    {'div', A, B} -> A / B;
    {mul, A, B} -> A * B
  end.


map(F,L) -> map(F, L,[]).
map(_,[], Acc) -> lists:reverse(Acc);
map(F,[H|T], Acc) -> map(F,T,[F(H)|Acc]).


filter(F,L) -> filter(F,L,[]).
filter(_,[],Acc) -> lists:reverse(Acc);
filter(F,[H|T],Acc) ->
  case F(H) of
    true -> 
      filter(F,T,[H|Acc]);
    false -> 
      filter(F,T,Acc)
    end.

split(F,L) -> split(F,L,[],[]).
split(_,[],True,False) -> 
  {lists:reverse(True), lists:reverse(False)};
split(F,[H|T],True,False) -> 
  case F(H) of
    true ->
      split(F,T,[H|True],False);
    false ->
      split(F,T,True,[H|False])
    end.

