package caspPlainPlateau.objectroles;

import casp.roles.InstitutionRole;
import caspPlainPlateau.agents.CitizenAgent;
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

		addRule("moveRule", new Rule("moveRule", "true", "moveplain", 1, "", "Move_to_plain!"));

		addRule("stayRule", new Rule("stayRule", "true", "stayplateau", 2, "", "stay_on_plateau!"));

	}
}
