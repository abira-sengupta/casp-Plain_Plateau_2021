:-discontiguous initially/3.
:-discontiguous initially/1.

/************************************************
                 initially
************************************************/

initially(wealth(citizen_1,0)).
initially(wealth(citizen_2,0)).
initially(wealth(citizen_3,0)).
initially(wealth(citizen_4,0)).
initially(wealth(citizen_5,0)).
initially(wealth(citizen_6,0)).

initial_house_on_plain_value(1.8).

flood_causes_damage(1.1).


/*************************************************
                     happensAtNarrative
*************************************************/

happensAtNarrative(join(citizen_1, citizens,citizens_plateaudwellerrole),0).
happensAtNarrative(join(citizen_2, citizens,citizens_plateaudwellerrole),0).
happensAtNarrative(join(citizen_3, citizens,citizens_plateaudwellerrole),0).
happensAtNarrative(join(citizen_4, citizens,citizens_plaindwellerrole),0).
happensAtNarrative(join(citizen_5, citizens,citizens_plaindwellerrole),0).
happensAtNarrative(join(citizen_6, citizens,citizens_plaindwellerrole),0).


happensAtNarrative(add_role(citizen_1,citizens,citizens_possibleviolpunisherrole),1).
happensAtNarrative(add_role(citizen_2,citizens,citizens_possibleviolpunisherrole),1).
happensAtNarrative(add_role(citizen_3,citizens,citizens_possibleviolpunisherrole),1).
