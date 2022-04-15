package caspPlainPlateau.objectroles;

import casp.roles.ObjectRole;
import caspPlainPlateau.agents.GovernmentAgent;
import ch.maxant.rules.Rule;

/**
 * the DiscretionaryRegime role for GovermentAgent
 * 
 * @author senab196
 */
public class DiscretionaryRegimeRole extends ObjectRole<GovernmentAgent>
{

	public DiscretionaryRegimeRole()
	{
		super("discretionaryregimerole");

	}

	public void init()
	{
		addRule("compensateRule",
				new Rule("compensateRule", "prolog(\"holdsAt(damage(_), %d).\", ag.environment.tick)", "compensate", 3,
						"GovernmentAgent_DiscretionaryRegime",
						"compensate if some agent is on the plateau and some agent(s) have damage"));

		addRule("do_not_compensateRule",
				new Rule("do_not_compensateRule", "prolog(\"holdsAt(damage(_), %d).\", ag.environment.tick)",
						"do_not_compensate", 1, "GovernmentAgent_DiscretionaryRegime",
						"Do not compensate if no agent(s) is on the plateau or no agent(s) house is damage"));

	}
}
