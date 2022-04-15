
:-discontiguous initiates/3.
:-discontiguous terminates/3.



terminates(change_role(A,citizens,_,_),location(A,_),T):-
	holdsAt(member(A,citizens),T).

initiates(change_role(A,citizens,_,citizens_plaindwellerrole),location(A,plain),T):-
	holdsAt(member(A,citizens),T).

initiates(change_role(A,citizens,_,citizens_plateaudwellerrole),location(A,plateau),T):-
	holdsAt(member(A,citizens),T).

terminates(join(A,citizens,_),location(A,_),T):-
	\+ holdsAt(member(A,citizens),T).

initiates(join(A,citizens,citizens_plaindwellerrole),location(A,plain),T):-
	\+ holdsAt(member(A,citizens),T).

initiates(join(A,citizens,citizens_plateaudwellerrole),location(A,plateau),T):-
	\+ holdsAt(member(A,citizens),T).


