:-discontiguous initiates/3.
:-discontiguous terminates/3.





initiates(join(A,I,_), member(A,I), _).

initiates(join(A,I,R), has_role(A,I,R), _).


terminates(leave(A,I), member(A,I), _).
terminates(leave(A,I), has_role(A,I,_), _).


initiates(add_role(A,I,R), has_role(A,I,R), T):-
	holdsAt(member(A,I), T).

%terminates(add_role(A,I,R), has_role(A,I,R),T):-
%	holdsAt(\+(member(A,I,_)),T).

terminates(drop_role(A,I,R), has_role(A,I,R), _).


terminates(drop_role(A,I,R), member(A,I), T):-
	\+( holdsAt(has_role(A,I,R2), T),
	    R2 \== R
	  ).

initiates(change_role(A,I,_,_),member(A,I),_).


%terminates(change_role(A,I,OLDR,_), has_role(A,I,OLDR), T) :-
 %       holdsAt(member(A,I), T).

terminates(change_role(A,I,NEWR,_), has_role(A,I,NEWR), T) :-
         holdsAt(member(A,I), T).

initiates(change_role(A,I,NEWR,_), has_role(A,I,NEWR), T):-
        holdsAt(member(A,I), T).

