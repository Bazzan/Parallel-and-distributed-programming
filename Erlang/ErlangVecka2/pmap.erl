-module(pmap).
-export([unordered/2,unordered/3,ordered/3]).


unordered(F,L) ->
    Pids = [spawn_work(F,E)|| E  <- L],
    wait_for_result_loop([],length(L)).
    %gather(Pids).
unordered(F,L,MaxWorker) when length(L) =< MaxWorker ->
    unordered(F,L);
unordered(F,L,MaxWorker) when MaxWorker > 0 ->
    Pids = spawn_workers([],MaxWorker),

    % NewList = split(L,MaxWorker),

    sendWork(L,Pids,F),


    wait_for_result_loop([],length(L)).

ordered(F,L,MaxWorker) ->
    Pids = spawn_workers([],MaxWorker),
    sendWork(L,Pids,F),
    gather(Pids).
    
wait_for_result_loop(Result, 0) ->
    Result;
wait_for_result_loop(ResultList, Length) when Length > 0 ->
    receive
        {_Pid, {result, Result}} ->
            [Result | wait_for_result_loop(ResultList, Length -1)]
    end.


spawn_work(F,E) ->
    Pid = spawn(fun worker/0),
    Pid ! {self(), {F,E}},
    Pid.
spawn_workers(WorkerList, 0) ->
    WorkerList;
spawn_workers(WorkerList, WorkerCount) when WorkerCount > 0 ->
    [spawn(fun worker/0) | spawn_workers(WorkerList, WorkerCount - 1)].


sendWork([],X,_F) ->[];
sendWork([H|T], Pid, F) when is_pid(Pid) ->
    Pid ! {self(),{F,H}},
    sendWork(T,Pid,F);
sendWork([H|T], [Pid|Pids], F) ->
    Pid ! {self(),{F,H}},
    sendWork(T,lists:append(Pids, [Pid]), F).


worker() ->
    receive
        {Master, {F,E}}->
            Master ! {self(),{result, F(E)}},
            % io:fwrite("dude ~n~p", [self()]),
        worker()
        end.


gather([])->
    [];
gather([Pid|Pids])->
    receive
        {Pid,{result,R}} ->
            [R | gather(Pids)]
        end.




% sendWork([H|T], Pids, F) ->
%    [sendWork2(Pid,H,F) || Pid <- Pids].

% sendWork2(Pid,HeadList,F) ->
%     [Pid ! {self(),{F,E}} || E <- HeadList].

% split(List,MaxWorker) ->
%     L = length(List),
%     splitBy(List,L rem MaxWorker + L div MaxWorker,[]).
% splitBy([],_,R) -> lists:reverse(R);
% splitBy(List,N,Result)->
%     {Part,NewList} = lists:split(N,List),
%     splitBy(NewList,N,[Part|Result]).





% Pids = [spawn(fun woker/0| spawn_workers(workerList,WorkerCouint -1)]

% sendWork(F,List, Pids,Len , Start) when Len > Start ->

%     [Pid ! {self(),{F,lists:get}} | Pids],
%     sendWork(F,List,Pids,Len,Start + lenght(Pids))
% end.



% workHandeler(_,[],F) ->
%     [];
% workHandeler([Pid|Rest],[Work|WRest],F) ->
%     lists:foreach(fun (E) -> Pid ! {self(),{F,E}} end, Work ),
%     workHandeler(Rest,WRest,F).