package caspPlainPlateauNorm.objectroles;

import casp.roles.InstitutionRole;
import caspPlainPlateauNorm.agents.CitizenAgent;
import ch.maxant.rules.Rule;

/**
 * the PlainDweller role for CitizenAgent
 * 
 * @author senab196
 */
public class PlainDwellerRole extends InstitutionRole<CitizenAgent>
{

	public PlainDwellerRole()
	{
		super("plaindwellerrole");
	}

	// @Override
	public void init()
	{
		addRule("moveRule", new Rule("moveRule", "prolog(\"label(choose_location, %d).\",ag.environment.tick)",
				"moveplateau", 1, "", "Move_to_Plateau!"));

		addRule("stayRule", new Rule("stayRule", "prolog(\"label(choose_location, %d).\",ag.environment.tick)",
				"stayplain", 2, "", "Stay_on_Plain!"));

	}
}
