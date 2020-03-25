-module(gen_worker).
-export([start/2, call/2,async/2,await/2,await/1,await_all/1]).

% -callback start(Callback :: term(), MaxWorker :: term()) -> Pid :: term().
% -callback stop(Pid :: term()) -> stop.
% -callback async(Pid :: term(), Work :: term()) -> Ref ::term().
% -callback await(Ref :: term()) -> Result_of_ref_or_error :: term().
% -callback await_all(Refs :: term()) -> Results_of_all_work :: term().
-callback handle_work(Callback :: term()) -> Result :: term().

start(Module,Max) ->
    % worker_loop([],Module),
    spawn_workers(Module,[],Max).

% worker_loop(State,Module) ->
%     receive
%         {From, Ref,Pids,{request, Work}}->
%             [Worker|Workers] = Pids,
%             case Module:handle_work(Work) of
%                 {reply, NewState, Response} ->
%                     From ! {response,Ref,Response},
%                 worker_loop(NewState,Module)
%             end
%         end.

call(Pid,Request) ->
    Ref = make_ref(),
    Pid ! {self(), Ref, {request,Request}},
    receive
        {response,Ref,Response} ->
            Response
        end.    

spawn_workers(_,WorkerList,0)->
    WorkerList;
spawn_workers(Module,WorkerList,WorkerCount) ->
[spawn(fun Module:handle_work/1)| spawn_workers(Module,WorkerList,WorkerCount-1) ].


await(Ref) ->
    await.
await(One,Two) ->
    await2args.

await_all(Refs) ->
await_all.
%fun (X) -> X*2 end, [1,2,3]
async([Pid|WorkPool],Work) ->
    
     case Work of 
         {F,L}  ->
             Pid ! {BlaBla},
             Ref = make_ref(),
             lists:append(WorkPool, [Pid])


    Ref.

stop(WorkPool) ->
    stop.

worker(Module) ->
    receive                         %work
        {From,Ref,{request,Work}} ->
            From ! {Ref,{reply ,Module:handle_work(Work)}},
            worker(Module)
end.

