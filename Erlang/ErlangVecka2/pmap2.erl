-module(pmap2).
-behaviour(gen_worker).
-export([handle_work/1, ordered/2, start/0]).

handle_work({F,Value}) ->
    {result,F(Value)}.

ordered(F,L) ->

    WorkPool = gen_worker:start(?MODULE,2),
    Refs = [gen_worker:async(WorkPool,{F,Value}) || Value <- L ],

    Result = gen_worker:await_all(Refs),

   

    Result.


start() ->
    Pids =gen_worker:start(?MODULE,2),
    Pids.
