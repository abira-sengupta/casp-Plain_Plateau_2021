/**********************************************
                  initially
***********************************************/

:-discontiguous initiates/3.
:-discontiguous terminates/3.
:-discontiguous initiates/2.
:-discontiguous terminates/2.
:-discontiguous initiates/1.
:-discontiguous terminates/1.
:-dynamic reactors/2.


/************************************************
   This portion defines all Fluent and event
************************************************/


% fluent(wealth(_,_)).
% fluent(location(_,_)).
% fluent(damage(_,_)).
% event(taxed(_,_)).
% event(moved(_,_)).
% event(flood).
% event(consumed(_)).
% event(compensate(_,_)).
% event(repair(_,_)).


/***************************************************
                     Rules
***************************************************/

terminates(receive_income(A,_),wealth(A,_),_).

initiates(receive_income(A,RMoney),wealth(A,NMoney),T):-
    holdsAt(wealth(A,OMoney),T),
    NMoney is OMoney + RMoney.


terminates(change_role(A,citizens,_,_),location(A,_),T):-
    holdsAt(member(A,citizens),T).


initiates(change_role(A,citizens,_,citizens_plaindwellerrole),location(A,plain),T):-
    holdsAt(member(A,citizens),T).

initiates(change_role(A,citizens,_,citizens_plateaudwellerrole),location(A,plateau),T):-
    holdsAt(member(A,citizens),T).

terminates(taxed(A,_),wealth(A,_),_).
initiates(taxed(A,Tax),wealth(A,New),T) :-
    holdsAt(wealth(A,Old),T),
    New is Old - Tax.

% Add a clause for the first damage (no previous damage), i.e. \+
% holdsAt(damage(A,_), T).
terminates(flood,damage(_,_),_).
initiates(flood,damage(A,D),T):-
    holdsAt(location(A,plain),T),
   \+ holdsAt(damage(A,_),T),
    flood_causes_damage(D).

terminates(flood,damage(_,_),_).
initiates(flood,damage(A,D),T):-
     holdsAt(damage(A,CD),T),
     initial_house_on_plain_value(V),
     flood_causes_damage(FD),
     D is min(FD + CD, V).


terminates(compensate(A,_),wealth(A,_),_).
initiates(compensate(A,Money),wealth(A,New),T):-
   holdsAt(wealth(A,Old),T),
   New is Old + Money.


terminates(consumed(A),wealth(A,_),_).
initiates(consumed(A),wealth(A,0),_).


%terminates(repair(Citizen,_),wealth(Citizen,_),_).
%initiates(repair(Citizen,Money),wealth(Citizen,Current_wealth),T):-
%    holdsAt(wealth(Citizen,Old_wealth),T),
%    Current_wealth is Old_wealth - Money.




terminates(repair(Citizen,_),wealth(Citizen,_),_).
initiates(repair(Citizen,Money),wealth(Citizen,Current_wealth),T):-
    holdsAt(wealth(Citizen,Old_wealth),T),
    holdsAt(damage(Citizen,D),T),
    Current_wealth is Old_wealth - Money,
    D1 is D - Money,
    Money is min(Money, min(Current_wealth,D1)).




terminates(repair(Citizen,_),damage(Citizen,_),_).
initiates(repair(Citizen,Money),damage(Citizen,D1),T):-
   holdsAt(damage(Citizen,D),T),
   holdsAt(wealth(Citizen,W),T),
   D1 is D - Money,
   W1 is W - Money,
   Money is min(Money, min(W1,D1)).



%countsAs(plain(A),change_role(A,_,citizens_plateaudwellerrole,citizens_plaindwellerrole)).
%countsAs(plateau(A),change_role(A,_,citizens_plaindwellerrole,citizens_plateaudwellerrole)).



initiates(change_role(government_agent,government,_,discretionarybasedregimerole),
          exp_rule(damage(A),next(not(happ(compensate(A)))))).


terminates(change_role(government_agent,government,_,_),
           exp_rule(damage(_), _)).


%initiates(join(government_agent,government,rulesbasedregimerole),
%          exp_rule(damage(A,_),not(happ(compensate(A,_)))),_).


initiates(join(government_agent,government,discretionarybasedregimerole),
          exp_rule(member(A,citizens),always(not(location(A,plain)))),_).



/***************************************** Prolog *************************************************/

countsAs(stayplain,_,_,noop).

countsAs(stayplateau,_,_,noop).

countsAs(donotpunish,_,_,noop).


countsAs(moveplateau,A,_,change_role(A,citizens,citizens_plaindwellerrole,citizens_plateaudwellerrole)).

countsAs(moveplain,A,_,change_role(A,citizens,citizens_plateaudwellerrole,citizens_plaindwellerrole)).


countsAs(punish,A,B,punish(A,B,10)).

viol_cost(V,_):- format('viol_cost called with 1st argument ~w~n', [V]),
			     fail.
viol_cost(viol(_,_,_,Exp), 10) :- contains_term(location(_,_), Exp), !.

viol_cost(viol(_,_,_,Exp), 5) :- contains_term(punish(_,_,_), Exp), !.

viol_cost(V, 1).


/****************************************** Norm ***************************************************/

% Norm

initially(exp_rule(member(A,citizens),never(location(A,plain)))).

% metanorm

initially(exp_rule(and([happ(viol(_,_,_,never(location(A,plain)))), location(B,plateau)]), happ(punish(B,A)))).


