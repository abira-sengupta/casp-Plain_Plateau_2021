package caspPlainPlateauNorm.agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.jpl7.Atom;
import org.jpl7.JPLException;
import org.jpl7.Query;
import org.jpl7.Term;

import casp.Log;
import casp.agent.JSONRoleAgent;
import casp.prolog.ExtendedQuery;
import ch.maxant.rules.Rule;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;

/**
 * A CitizenAgent in the PlainPlateau Scenario
 * 
 * @author senab196
 */

// CitizenAgent Class extends JSONRoleAgent from casp project.
// JSONRoleAgent is a class to make an agent a JSON specified agent.

public class CitizenAgent extends JSONRoleAgent
{

	private static final int SPENT = 5;
	private final int CYCLE_LENGTH = 7;

	private double violCost1, violCost2;
	private Map<String, Term> actionToEcEvents = null;

	public enum regimeSelection
	{
		rulesbasedregime, discretionaryregime
	}

	// constructor
	public CitizenAgent()
	{
		// CYCLE_LENGTH = caspPlainPlateauNorm.Controller.CYCLE_LENGTH;
	}

	/**
	 * *Agent has role
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void printMyRoles()
	{
		Set<String> myRoles = this.getRoleNames();
		String myName = this.getName();
		Log.info("Agent " + myName + " has roles " + myRoles);
		Log.info("--------------------------------------------------------------------");
	}

	/*
	 * /** implementation of the run method called by an action - note 'Void' as specified in the Action class
	 * 
	 * @return null
	 */

	public void step()
	{
		Log.info("This is the base method of CitizenAgent of the PlainPlateau Scenario.");
	}

	/**
	 * choose location method for the CitizenAgent
	 */

	@ScheduledMethod(start = 2, interval = CYCLE_LENGTH)
	public void choose_location()
	{
		List<Rule> mr = getRuleEngine().getEngine().getMatchingRules(this);
		List<String> possibleActions = new ArrayList<>();
		Double tickcount = RepastEssentials.GetTickCount();
		int currentTick = tickcount.intValue();
		for (int i = 0; i < mr.size(); i++)
		{
			possibleActions.add(mr.get(i).getOutcome());
		}

		Log.info("--------------------------------------------------------------------");
		int tick = getEnvironment().getTick();
		Log.info(" Step: " + this + " t=" + tick);

		if (possibleActions.size() > 0)
		{
			choose_and_perform_action(possibleActions);
		}
	}

	/**
	 * who can get punishment
	 */

	@ScheduledMethod(start = 3, interval = CYCLE_LENGTH)
	public void consider_punishment()
	{
		Double tickcount = RepastEssentials.GetTickCount();
		int currentTick = tickcount.intValue();
		List<Rule> mr = getRuleEngine().getEngine().getMatchingRules(this);
		List<String> possibleActions = new ArrayList<>();
		for (int i = 0; i < mr.size(); i++)
		{
			possibleActions.add(mr.get(i).getOutcome());
		}
		Log.info("At time tick = 3, possible actions names are " + possibleActions);

		Log.info(" Step: " + this);

		if (possibleActions.size() > 0)
		{
			choose_and_perform_action(possibleActions);
		}
	}

	/**
	 * This method for choose and perform the action
	 */

	public void choose_and_perform_action(List<String> recActs)
	{
		Double tickcount = RepastEssentials.GetTickCount();
		int currentTick = tickcount.intValue();

		Log.info("Possible actions are in choose_and_perform_action method : " + recActs);

		int numRecActs = recActs.size();

		if (numRecActs == 0)
		{
			Log.info("The total number of actions is zero");
			return;
		}

		if (numRecActs != 2)
		{
			Random rand = new Random();
			String act = recActs.get(rand.nextInt(numRecActs));
			Term ecEventList = actionToEcEvents(act);
			Term[] queryParams = new Term[] { ecEventList, new org.jpl7.Integer(currentTick) };
			Query.hasSolution("foreach(member(E,?), assert(happensAtNarrative(E, ?))).", queryParams); // Call foreach
																										// // query
			return;
		}

		// When numRecActs == 2

		String act1 = recActs.get(0);
		String act2 = recActs.get(1);

		Term eventList1 = actionToEcEvents(act1);
		Term eventList2 = actionToEcEvents(act2);

		Term[] queryParams = new Term[] { eventList1, eventList2, new org.jpl7.Integer(currentTick) };

		Query q = new Query(
				"freeze(NonHappExp, NonHappExp \\= happ(_)), whatif2(?,?,?,viol(_,_,_,NonHappExp),do_not_care,E1,F1,E2,F2).",
				queryParams);

		Map<String, Term> solution = q.oneSolution();

		Log.info("whatif2 query: " + q);

		Log.info(" whatif2 solution to cheack events = " + solution);

		Term violEvents1 = solution.get("E1");
		Term violEvents2 = solution.get("E2");

		Term chosenEventList;

		// If length of both violation events are zero

		if (org.jpl7.Util.listToLength(violEvents1) == 0 && org.jpl7.Util.listToLength(violEvents2) == 0)
		{
			Random rand = new Random();
			chosenEventList = Math.random() < 0.5 ? eventList1 : eventList2;
		}

		// If length of both violation events are non zero

		else if (org.jpl7.Util.listToLength(violEvents1) != 0 && org.jpl7.Util.listToLength(violEvents2) != 0)
		{
			Term[] queryParams1 = new Term[] { violEvents1 };
			Map<String, Term> resultMap1 = Query
					.oneSolution("aggregate_all(sum(C), (member(V,?),viol_cost(V,C)), TotalCost)", queryParams1);

			Term[] queryParams2 = new Term[] { violEvents2 };
			Map<String, Term> resultMap2 = Query
					.oneSolution("aggregate_all(sum(C), (member(V,?),viol_cost(V,C)), TotalCost)", queryParams2);

			Term totalViolCostTerm1 = resultMap1.get("TotalCost");
			Term totalViolCostTerm2 = resultMap2.get("TotalCost");

			try
			{
				violCost1 = totalViolCostTerm1.doubleValue();
				violCost2 = totalViolCostTerm2.doubleValue();
			}
			catch (JPLException e)
			{
				System.out.println("JPL Exception occurred");
			}

			if (violCost1 < violCost2)
			{
				Log.info("When violCost1 is lesser than the violCost2, at time tick = 3");
				chosenEventList = eventList1;
			}

			else if (violCost1 > violCost2)
			{
				Log.info("When violCost1 is greater than violCost2, at time tick = 3");
				chosenEventList = eventList2;
			}
			else
			{
				Log.info("Randomly selected violcost, at time tick = 3");
				Random rand = new Random();
				chosenEventList = Math.random() < 0.5 ? eventList1 : eventList2;
			}
		}
		else
		{
			if (org.jpl7.Util.listToLength(violEvents1) > 0)
			{
				chosenEventList = eventList2;
			}
			else
			{
				chosenEventList = eventList1;
			}
		}
		String name = this.getPrologName();
		Term[] queryParams3 = new Term[] { chosenEventList, new org.jpl7.Integer(currentTick) };
		Query actionquery = new Query("foreach(member(E,?), assert(happensAtNarrative(E, ?))).", queryParams3);
		System.out.println("foreach query is:" + actionquery);
		if (!actionquery.hasSolution()) // To call foreach query
		{
			Log.info(".........foreach failed !!!!!!!..........");

		}
	}

	/*
	 * CountsAs query
	 */

	public Term actionToEcEvents(String act)
	{
		String name = this.getPrologName();
		String countsAsQuery = "(retract(reactors(?,RList)) -> true; RList = [not_applicable]), findall(E, (member(Reactor, RList), countsAs(?,?,Reactor,E)), Events).";

		Term[] queryParams = new Term[] { new Atom(act), new Atom(act), new Atom(name) };
		Map<String, Term> qsol = new Query(countsAsQuery, queryParams).oneSolution();

		return qsol.get("Events");
	}

	/**
	 * Repair method for the CitizenAgent
	 */

	@ScheduledMethod(start = 6, interval = CYCLE_LENGTH)
	public void repair()
	{
		Double tickcount = RepastEssentials.GetTickCount();
		int currentTick = tickcount.intValue();
		String name = this.getPrologName();
		Term[] queryParams1 = new Term[] { new Atom(name), new org.jpl7.Integer(currentTick) };
		if (Query.hasSolution("holdsAt(damage(?,_),?).", queryParams1))
		{
			Term[] queryParams;
			queryParams = new Term[] { new Atom(name), new org.jpl7.Integer(SPENT), new org.jpl7.Integer(currentTick) };
			Query.hasSolution("assert(happensAtNarrative(repair(?, ?), ?)).", queryParams);
		}
		else
		{
			Log.info(name + " is not at plain, no flood damage has occurred, and thus no repairs are required.");
		}
	}

	/**
	 * consume method for CitizenAgent
	 */

	@ScheduledMethod(start = 7, interval = CYCLE_LENGTH)
	public void consume()
	{
		Double tickcount = RepastEssentials.GetTickCount();
		int currentTick = tickcount.intValue();
		String name = this.getPrologName();
		Term[] queryParams;
		queryParams = new Term[] { new Atom(name), new org.jpl7.Integer(currentTick) };
		Query.hasSolution("assert(happensAtNarrative(consumed(?),?)).", queryParams);
	}

	public boolean prologQuery(String q, Object... params)
	{
		return ExtendedQuery.hasSolution(q, params);
	}

}
