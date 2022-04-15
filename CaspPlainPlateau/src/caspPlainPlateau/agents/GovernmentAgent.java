package caspPlainPlateau.agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;
import org.jpl7.Atom;
import org.jpl7.Query;
import org.jpl7.Term;

import casp.Log;
import casp.agent.JSONRoleAgent;
import casp.prolog.ExtendedQuery;
import ch.maxant.rules.Rule;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.util.ContextUtils;

/**
 * A GovernmentAgent in the PlainPlateau Scenario
 * 
 * @author senab196
 */

// GovernmentAgent Class extends JSONRoleAgent from casp project.
// JSONRoleAgent is a class to make an agent a JSON specified agent.

public class GovernmentAgent extends JSONRoleAgent
{

	private static final int CYCLE_LENGTH = 6;
	// private static final double DAMAGE = 1.1;
	private static float CONS = (float) 1.1;

	private HashMap<String, Integer> utilities = new HashMap<String, Integer>();

	List<Pair<Object, String>> pair = new ArrayList<Pair<Object, String>>();

	// constructor

	public GovernmentAgent()
	{

		utilities.put("compensate", 708);
		utilities.put("do_not_compensate", 698);

	}

	public void step()
	{

		Log.info("This is the base method of GovernmentAgent of the PlainPlateau Scenario.");

	}

	/**
	 * implementation of the run method called by an action - note 'Void' as specified in the Action class
	 * 
	 * @return null
	 */

	// collectTax() is used by the GovermentAgent for collect the Tax from the citizen
	// who are living in the plateau.

	public void collectTax()
	{
		prologQuery("assert(happensAtNarrative(taxed(%s), %d)).", this.getPrologName(), getEnvironment().getTick());
		Log.info("GovernmentAgent " + this.getPrologName() + "asserted taxed()");

	}

	public void makeCompensate()
	{
		prologQuery("assert(happensAtNarrative(compensate(%s), %d)).", this.getPrologName(),
				getEnvironment().getTick());
		Log.info("GovernmentAgent " + this.getPrologName() + "asserted compensate()");

	}

	// TO DO: Allow some actions to be done at the same time (e.g. join institution and do another
	// action)

	/**
	 * step method for GovernmentAgent
	 */

	public String chooseAction(List<String> possActs)
	{
		String highestUtilityAction = "";
		int highestUtility = 0;
		for (int i = 0; i < possActs.size(); i++)
		{
			String actionName = possActs.get(i);
			Log.info("Action Name is.......... " + actionName);
			int utility = utilities.get(actionName);
			System.out.println("Value of the utility is = " + utility);
			if (utility > highestUtility)
			{
				highestUtility = utility;
				highestUtilityAction = actionName;
			}
			Log.info("highestUtility......." + highestUtility);
		}
		return highestUtilityAction;
	}

	/*
	 * Tax or Compensation which one citizen will select
	 */
	public void TaxOrCompensate()
	{

		Double tickcount = RepastEssentials.GetTickCount();
		int currentTick = tickcount.intValue();
		Log.info("Print current Tick value within the TaxOrCompensate : " + currentTick);

		Term[] queryParams;
		queryParams = new Term[] { new org.jpl7.Integer(currentTick) };
		Map<String, Term> resultMap = Query.oneSolution("findall(A, holdsAt(damage(A,_),?), DamagedAgents)",
				queryParams);
		String[] da = org.jpl7.Util.atomListToStringArray(resultMap.get("DamagedAgents"));
		Set<String> daSet = new HashSet<>(Arrays.asList(da));
		Log.info("Number of Damage citizen : " + daSet);
		int numOfDamagedAgents = daSet.size();

		double COMPENSATE = numOfDamagedAgents * 1.1; // Value of the compensate
		double X = Math.abs(1 - numOfDamagedAgents);
		System.out.println("The value of the X is :" + X);

		double TAX = 5; // Value of the Tax

		if (numOfDamagedAgents != 0) // If number of damage agent is not equals to zero
		{
			Context context = ContextUtils.getContext(this);
			Iterable<CitizenAgent> i = context.getAgentLayer(CitizenAgent.class);
			Iterator<CitizenAgent> it = i.iterator();
			while (it.hasNext())
			{
				CitizenAgent c = it.next();

				String name = c.getPrologName();

				if (daSet.contains(c.getPrologName()))
				{
					Log.info("damaged citizen will get the compensate");
					Term[] queryParams1;
					queryParams1 = new Term[] { new Atom(name), new org.jpl7.Float(COMPENSATE),
							new org.jpl7.Integer(currentTick) };

					Query.hasSolution("assert(happensAtNarrative(compensate(?, ?), ?)).", queryParams1);
				}

				else if (!daSet.contains(c.getPrologName()))
				{
					Log.info("non damaged agents will not get any compensate");
					Term[] queryParams1;
					queryParams1 = new Term[] { new Atom(name), new org.jpl7.Float(TAX),
							new org.jpl7.Integer(currentTick) };

					Query.hasSolution("assert(happensAtNarrative(taxed(?, ?), ?)).", queryParams1);
				}
			}
		}
	}

	@ScheduledMethod(start = 4, interval = CYCLE_LENGTH)
	public void makeDecision()
	{
		Log.info("--------------------------------------------------------------------");
		int tick = getEnvironment().getTick();
		Log.info(" Step: " + this + " t=" + tick);

		List<Rule> mr = getRuleEngine().getEngine().getMatchingRules(this);
		List<String> possibleActions = new ArrayList<>();

		TaxOrCompensate();

		for (int i = 0; i < mr.size(); i++)
		{
			possibleActions.add(mr.get(i).getOutcome());
		}

		String selectedActionName = "";

		if (possibleActions.size() > 0)
		{

			selectedActionName = chooseAction(possibleActions);
		}
		if (selectedActionName.equals(""))
		{

		}
		else if (selectedActionName.equals("do_not_compensate"))
		{
			collectTax();
		}
		else if (selectedActionName.equals("compensate"))
		{
			makeCompensate();
		}

	}

	public boolean prologQuery(String q, Object... params)
	{
		return ExtendedQuery.hasSolution(q, params);
	}

}
