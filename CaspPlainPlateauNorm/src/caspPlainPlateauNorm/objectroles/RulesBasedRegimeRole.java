package caspPlainPlateauNorm.objectroles;

import org.jfree.util.Log;

import casp.action.Action;
import casp.roles.InstitutionRole;
import casp.roles.ObjectRole;
import caspPlainPlateauNorm.agents.CitizenAgent;
import caspPlainPlateauNorm.agents.GovernmentAgent;
import ch.maxant.rules.ExecutableAction;
import ch.maxant.rules.Rule;
import repast.simphony.essentials.RepastEssentials;


import repast.simphony.ui.GUIScheduleRunner;
/**
 * the RulesBasedRegime role for GovernmentAgent
 * @author senab196
 *
 */
public class RulesBasedRegimeRole extends ObjectRole<GovernmentAgent> {
	
	public RulesBasedRegimeRole() {
		super("rulesbasedregimerole");
	
	}
	
	//@Override
		public void init(){
			
			addRule("not_compensateRule",
					new Rule(
					"not_compensateRule",
		    		"prolog(\"holdsAt(damage(_),%d) .\", ag.environment.tick)",
		    		"do_not_compensate",
		    		1,
		    		"GovernmentAgent_RulesBasedRegime",
		    		"Do not compensate even if agent's house was damage"));
			
		}
}