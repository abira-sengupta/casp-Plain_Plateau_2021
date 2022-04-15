:- ProjectDir = 'C:/Users/senab196/repast-workspace-2/CaspPlainPlateau/',
   cd(ProjectDir),
   [dec],
cd('casp-config/plainplateau/dec'),
   [role, plain_plateau, locationchange_role],
   initialiseDEC.

% Prolog facts queried in EC rules
initial_house_on_plain_value(1.8).
flood_causes_damage(1.1).

% Initially clauses for testing
initially(wealth(citizen_1,0)).
initially(wealth(citizen_2,0)).
initially(exp_rule(exp_rule(damage(A), next(happ(compensate(A)))),
		   do(plain(_)))).
initially(exp_rule(exp_rule(damage(A), next(not(happ(compensate(A))))),
		   do(plateau(_)))).
initially(exp_rule(and([@(makeDecision_change_role),member(A,citizens)]),next(location(A,plain)))).
initially(exp_rule(@(giveIncome),next(@(makeDecision_change_role)))).

% Narrative for testing, plus related Prolog facts
happensAtNarrative(join(citizen_1, citizens,citizens_plateaudwellerrole),0).
happensAtNarrative(join(citizen_2, citizens,citizens_plaindwellerrole),0).

label(giveIncome, 1).
happensAtNarrative(receive_income(citizen_1,1.2),1).
happensAtNarrative(receive_income(citizen_2,1.2),1).

label(makeDecision_change_role, 2).
happensAtNarrative(change_role(citizen_1,citizens,citizens_plateaudwellerrole,citizens_plaindwellerrole),2).
happensAtNarrative(change_role(citizen_2,citizens,citizens_plaindwellerrole,citizens_plateaudwellerrole),2).

%% I don't know what the other labels are - Abira, please add them
label(occurrenceOfFlood, 3).
happensAtNarrative(flood,3).

label(makeDecisionForTaxOrCompensate, 4).
happensAtNarrative(taxed(government,5.0),4).

label(repair, 5).
happensAtNarrative(repair(citizen_2,5), 5).

label(consume, 6).
happensAtNarrative(consumed(citizen_1),6).
happensAtNarrative(consumed(citizen_2),6).

%%% Run narrative:

last_time(5).

:- last_time(LT),
   forall(between(0,LT,T),
          ( PrevT is T-1,
            format('~nEvents at time ~w~n~n', [PrevT]),
            forall(happensAt(F, PrevT), (tab(4), writeq(F), nl)),
            format('~n*** Calling event_recognition(~w) ***~n', [T]),
            event_recognition(T),
            format('~nLabels at time ~w~n~n', [T]),
            forall(label(Label,T), (tab(4), writeq(Label), nl)),
            format('~nFluents at time ~w~n~n', [T]),
            forall(holdsAt(F, T), (tab(4), writeq(F), nl))
          )).
