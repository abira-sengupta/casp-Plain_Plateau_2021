package caspPlainPlateauNorm.objectroles;

import casp.roles.InstitutionRole;
import caspPlainPlateauNorm.agents.CitizenAgent;
import ch.maxant.rules.Rule;

/**
 * the PlainDweller role for CitizenAgent
 * 
 * @author senab196
 */

public class PossibleViolPunisherRole extends InstitutionRole<CitizenAgent>
{
	public PossibleViolPunisherRole()
	{
		super("possibleviolpunisherrole");

	}

	// @Override
	public void init()
	{
		addRule("punishRule", new Rule("punishRule",
				"prolog(\"label(norms_checking, %1$d), bagof(A,X^Y^Z^(happensAt(viol(X,Y,Z,never(location(A,plain))),%1$d), A\\\\==%2$s),Violators),assert(reactors(Action, Violators)).\", ag.environment.tick, ag.prologName)",
				"punish", 1, "", "punish_violation"));

		addRule("nopunishRule",
				new Rule("donotpunishRule", "prolog(\"label(norms_checking, %d).\",ag.environment.tick)", "donotpunish",
						1, "", "do_not_punish_violation"));
	}

}
