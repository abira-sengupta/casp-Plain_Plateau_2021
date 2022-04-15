package caspPlainPlateau.institutions;

import casp.Log;
import casp.institution.Institution;

// to do: we may not use this institution - delete?

public class Government extends Institution
{
	// constructor
	public Government()
	{

	}

	/**
	 * add the shooter role to this institution
	 */
	@Override
	public void init()
	{
		super.init();
	}

	/**
	 * the institution's step method
	 */
	public void step()
	{
		Log.info("--------------------------------------------------------------------");
		Log.info("Step: " + getName());
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
