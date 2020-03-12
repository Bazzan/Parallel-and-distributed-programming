-module(double).

-export([double/1, start/0]).

start() -> register(double,spawn(fun double/0)).

double() ->
io:fwrite("dude ~n", []),
    receive
      {Pid, Ref, N} ->

	    Pid ! {Ref, N *2},
	    
	  double()
    end.
double(X) ->
    case whereis(double) of 
        Pid ->
            Ref = make_ref(),
            Pid ! {self(), Ref, X};
        undefined ->
            double(X)
        
        end.