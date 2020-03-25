-module(task1).
-export([zip_apply/3,zip_apply/4, pzip_apply/3,pzip_apply/4]).


% zip_apply([],F2) -> 
%     [];
% zip_apply([H1|T1],F2) ->
%     [H2|T2] = T1,    
%     [F2(H1,H2)| zip_apply(T2,F2)].

zip_apply(_F,[],[]) ->
     [];
zip_apply(F, [H1|T1],[H2|T2])when length(T1) == length(T2) ->
[F(H1,H2)|zip_apply(F,T1,T2)].
zip_apply(_F1,_F2,[],[])-> 
    [];
zip_apply(F1, F2, [H1|T1],[H2|T2]) when length(T1) == length(T2) ->
    E1 = F1(H1,H2),
    zip_apply(F1,F2,T1,T2, E1).
zip_apply(_F1, _F2, [],[],Acc) ->
    Acc;
zip_apply(F1, F2, [H1|T1],[H2|T2],Acc) ->
    E = F1(H1,H2),
    TempResult = F2(Acc,E),
    zip_apply(F1,F2,T1,T2, TempResult).  




pzip_apply(F1, L1,L2) when length(L1) == length(L2) ->
Pids = spawnWorker(F1,L1,L2),
gather(Pids).
pzip_apply(F1,F2,L1,L2) when length(L1) == length(L2) ->
AccPid = acc(F2, length(L1)),
Pids = spawnWorker(F1, L1, L2),
gather2(Pids, F2, AccPid).



acc(F2,Length) ->
    Pid = spawn(fun accWorker/0 ),
    Pid ! {self(), 0, F2, init, Length },
    Pid.


accWorker() ->
    receive        
        {Pid,0,F2,init, Length}->
            accWorker(Pid,0,Length)
        end.
accWorker(Pid,Counter,0) ->
    Pid ! {self(),Counter};
accWorker(Pid,Counter,Length) ->
    receive
        {Pid, Acc, F2}->
            accWorker(Pid, F2(Counter,Acc), Length -1)
        end.


gather2([],_F2,_AccPid)->
    receive
        {Pid,Result} ->
            Result
        end;
gather2([Pid|Pids],F2,AccPid)->
    receive
        {Pid, Result}->
            AccPid ! {self(), Result, F2},
            gather2(Pids,F2,AccPid)
        end.





gather([])->
    [];
gather([Pid|Pids])->
    receive
        {Pid, Result}->
            [Result| gather(Pids)]
        end.


spawnWorker(F1,[],[]) ->
[];
spawnWorker(F1,[H1|T1],[H2|T2]) ->
Pid = spawn(fun worker1/0),
    Pid ! {self(), F1,H1,H2 },
    [Pid | spawnWorker(F1,T1,T2)].

worker1() ->
    receive
        {Pid, F1, E1,E2} ->
            Pid ! {self(),F1(E1,E2)}
        end.

% zip_apply(F1,F2,T1,T2, 0)
    % addall(List,F2).

% Result =addall(List,F2),

% addall([], _F2) ->
%     [];
% addall([H|TList],F2) ->
%       [addall(H,TList,F2)];
% addall(H,[H1|T1],F2) ->
%     [F2(H,H1)| addall(T2,F2)].
