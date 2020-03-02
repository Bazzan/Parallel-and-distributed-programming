-module(helloWorld).
-export([hello_world/0, bajs/1]).

hello_world() -> io:fwrite("hello, world\n").

bajs(1) ->
    ok;
bajs(2) ->
    nej;
bajs({X,Y}) ->
    {ok, X+Y}.