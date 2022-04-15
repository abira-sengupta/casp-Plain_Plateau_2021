package caspPlainPlateauNorm.institutions;

import casp.Log;
import casp.institution.Institution;
import caspPlainPlateauNorm.objectroles.PlainDwellerRole;
import caspPlainPlateauNorm.objectroles.PlateauDwellerRole;
import caspPlainPlateauNorm.objectroles.PossibleViolPunisherRole;

public class Citizens extends Institution
{

	public Citizens()
	{
	}

	/**
	 * add the shooter role to this institution
	 */
	@Override
	public void init()
	{
		this.addAvailableRoleClassName(PlainDwellerRole.class.getName()); // These are the institution role, so added
																			// here.
		this.addAvailableRoleClassName(PlateauDwellerRole.class.getName());
		this.addAvailableRoleClassName(PossibleViolPunisherRole.class.getName());
		super.init();

	}

	/**
	 * the institution's step method
	 */
	public void step()
	{
		Log.info("--------------------------------------------------------------------");
		Log.info("Step:" + getName());
		getRuleEngine().executeAllActions();
	}

	/**
	 * process members - currently does nothing
	 */
	public void processMembers()
	{
		Log.info("Processing membership of " + this.getName());

	}

}
