-module(pmap).
-export([unordered/2,unordered/3]).


unordered(F,L) ->
    Pids = [spawn_work(F,E)|| E  <- L],
    gather(Pids).
unordered(F,L,MaxWorker) when length(L) =< MaxWorker ->
    unordered(F,L);
unordered(F,L,MaxWorker) when MaxWorker > 0 ->
    Pids = spawn_work(F,L,MaxWorker, []),

    WorkList = [lists:sublist(L,X,length(L) div MaxWorker) || X <- lists:seq(1, length(L),length(L) div MaxWorker)], 
    workHandeler(Pids,WorkList,F).

workHandeler(_,[],F) ->
    [];
workHandeler([Pid|Rest],[Work|WRest],F) ->
    lists:foreach(fun (E) -> Pid ! {self(),{F,E}} end, Work ),
    workHandeler(Rest,WRest,F).

spawn_work(F,E) ->
    Pid = spawn(fun worker/0),
    Pid ! {self(), {F,E}},
    Pid.
spawn_work(F,L,0,Pids)->
    Pids;
spawn_work(F,L,MaxWorker,Pids) when MaxWorker > 0 ->
    Pids = [spawn(fun worker/0) | spawn_work(F,L,MaxWorker -1, Pids)].

worker() ->
    receive
        {Master, {F,E}}->
            Master ! {self(),{result, F(E)}}
        end.

gather([])->
    [];
gather([Pid|Pids])->
    receive
        {Pid,{result,R}} ->
            [R | gather(Pids)]
        end.