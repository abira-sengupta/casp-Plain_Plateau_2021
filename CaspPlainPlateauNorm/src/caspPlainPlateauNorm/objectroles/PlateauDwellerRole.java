package caspPlainPlateauNorm.objectroles;

import casp.roles.InstitutionRole;
import caspPlainPlateauNorm.agents.CitizenAgent;
import ch.maxant.rules.Rule;

/**
 * the PlateauDweller role for CitizenAgent
 * 
 * @author senab196
 */
public class PlateauDwellerRole extends InstitutionRole<CitizenAgent>
{

	public PlateauDwellerRole()
	{
		super("plateaudwellerrole");

	}

	// @Override
	public void init()
	{
		addRule("moveRule", new Rule("moveRule", "prolog(\"label(choose_location, %d).\",ag.environment.tick)",
				"moveplain", 1, "", "Move_to_plain!"));

		addRule("stayRule", new Rule("stayRule", "prolog(\"label(choose_location, %d).\",ag.environment.tick)",
				"stayplateau", 2, "", "stay_on_plateau!"));

	}
}
