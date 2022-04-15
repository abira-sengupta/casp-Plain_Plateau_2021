package caspPlainPlateauNorm.institutions;

import casp.Log;
import casp.institution.Institution;
import caspPlainPlateauNorm.objectroles.DiscretionaryRegimeRole;
import caspPlainPlateauNorm.objectroles.PlainDwellerRole;
import caspPlainPlateauNorm.objectroles.PlateauDwellerRole;
import caspPlainPlateauNorm.objectroles.RulesBasedRegimeRole;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.ui.GUIScheduleRunner;

// to do: we may not use this institution - delete?

public class Government extends Institution {
	
public Government(){}
	/**
	 * add the shooter role to this institution
	 */
	@Override
	public void init(){
		//These are object roles, so should not be added to the institution
		//this.addAvailableRoleClassName(DiscretionaryRegimeRole.class.getName());
		//this.addAvailableRoleClassName(RulesBasedRegimeRole.class.getName());
		super.init();

	}
	
	/**
	 * the institution's step method
	 */
    public void step(){
		Log.info("--------------------------------------------------------------------");
		Log.info("Step: "+getName());
    	getRuleEngine().executeAllActions();
    }
    
    /**
     * process members - currently does nothing
     */
    public void processMembers(){
    	Log.info("Processing membership of "+this.getName());
    	
    }
	

}





