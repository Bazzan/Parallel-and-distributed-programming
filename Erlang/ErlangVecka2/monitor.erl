
-module(monitor).
-export([start/0]).

start() ->
    spawn(fun() -> init() end).

init() ->
    double:start(),
    monitor(process, double),
    loop().

loop() ->
    receive
        {'DOWN', Ref, process, _Pid, _Reason} ->
            demonitor(Ref),
            init()
    end.