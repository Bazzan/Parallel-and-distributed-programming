-module(bank).
-export([start/0,balance/2, deposit/3, withdraw/3, lend/4]).


start() ->
    spawn(fun() -> bank() end).

bank() ->
    ets:new(bs, [set, private, named_table]),
    bank_loop().

bank_loop() ->
    receive
        {Pid,Ref ,{balance,Name}} ->
            case ets:lookup(bs,Name) of
                [] ->
                    Pid ! {Ref,no_account};
                [{Name, Balance}] ->
                    Pid ! {ok, Ref, Balance}
                end;
            
        {Pid,Ref,{deposit,Name,Amount}} when Amount >= 0 ->
            case ets:lookup(bs,Name) of
                [] ->
                    ets:insert(bs,{Name,Amount}),
                    Pid ! {ok,Ref, Amount};
                [{Name,_Balance}]->
                    Pid ! {ok, Ref,ets:update_counter(bs, Name, Amount)}
                end;


        {Pid,Ref,{withdraw,Name,Amount}} ->
            case ets:lookup(bs,Name) of
                [] ->
                   Pid ! {Ref,no_account};
                [{Name, Balance}] when Balance - Amount >= 0 ->
                    Pid ! {ok,Ref, ets:update_counter(bs, Name, -Amount)};
                [{Name, _Balance}] ->
                    Pid ! {Ref,insufficient_funds}
            end
        end,
        bank_loop().

                



balance(Pid, Who) when is_atom(Who)->
    Ref = make_ref(),
    Mref = monitor(process,Pid),
    Pid ! {self(),Ref ,{balance, Who}},
    receive
        {'DOWN', Mref, process, _Pid, _Reason} ->
            demonitor(Mref),
            no_bank;
        {Ref,no_account} ->
            no_account;
        {ok, Ref, Balance}->
            {ok,Balance}
        end.


deposit(Pid,Who,Amount) when is_number(Amount) ->
    Mref = monitor(process,Pid),
    Ref = make_ref(),

    Pid ! {self(),Ref, {deposit, Who,Amount}},
    receive
        {'DOWN', Mref, process, _Pid, _Reason} ->
            demonitor(Mref),
            no_bank;
        {ok,Ref, NewBalance} ->
            {ok, NewBalance}
        end.
        
withdraw(Pid,Who,Amount)  ->
    Mref = monitor(process,Pid),
    Ref = make_ref(),

    Pid ! {self(),Ref, {withdraw, Who, Amount}},
    receive
        {'DOWN', Mref, process, _Pid, _Reason} ->
            demonitor(Mref),
            no_bank;
        {Ref,no_account} ->
            no_account;
        {ok,Ref, NewBalance} ->
            {ok, NewBalance};
        {Ref,insufficient_funds}->
            insufficient_funds
        end.
lend(Pid,From,To,Amount)  ->
    case balance(Pid,From) of
        no_bank->
            no_bank;
         no_account ->
             case balance(Pid,To) of
                no_account->
                     {no_account, both};
                {ok,_Balance} ->
                    {no_account,From}
                end;

        {ok, _FromBalance}->
                case balance(Pid,To) of
                    no_account->
                         {no_account, To};
                    {ok,_ToBalance} ->
                        case withdraw(Pid,From,Amount) of
                            insufficient_funds ->
                                insufficient_funds;
                            {ok, _NewBalance}->
                                deposit(Pid,To,Amount), ok
                end
            end
        end.

        