-module(bank).
-export([start/0,balance/2, deposit/3]).


start() ->
     spawn(fun bank/0).


bank() ->
    ets:new(bs, [set, private, named_table]),
    bank_loop().



bank_loop() ->
io:fwrite("bank_loop() ~n", []),

    receive
        {Pid, {balance,Name}} ->
            case ets:lookup(bs,Name) of
                [] ->
                    Pid ! {no_account};
                [{Name, Balance}] ->
                    Pid ! {ok, Balance}
                end;
            
        {Pid,{deposit,Name,Amount}} ->
            case ets:lookup(bs,Name) of
                [] ->
                    ets:insert(bs,{Name,Amount}),
                    Pid ! {ok, Amount};
                [{Name,Balance}] ->
                    Pid ! {ok, ets:update_counter(bs, Name, Amount)}
                end
            end,


           bank_loop().

                



balance(Pid, Who) ->
    Pid ! {self(), {balance, Who}},
    receive
        {no_account} ->
            no_account;
        {Name, Balance}->
            {ok,Balance}
        end.


deposit(Pid,Who,Amount) ->
    Pid ! {self(), {deposit, Who,Amount}},
    receive
        {ok, NewBalance} ->
            {ok, NewBalance}
        end.
        

