package caspPlainPlateauNorm;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.jpl7.Atom;
import org.jpl7.Query;
import org.jpl7.Term;

import casp.Log;
import casp.agent.IAgent;
import casp.environment.ControllerAgent;
import casp.environment.IEnvironment;
import casp.prolog.ExtendedQuery;
import casp.roles.NoSuchRoleException;
import casp.roles.Role;
import caspPlainPlateauNorm.agents.CitizenAgent;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.parameter.Parameters;
import repast.simphony.util.ContextUtils;

/**
 * This is the controller class for PlainPlateau Repast simulation runs. It implements the step() method and sends
 * control info to the environment including current tick. Decides when the run is finished.
 * 
 * @author senab196
 */

public class Controller extends ControllerAgent
{

	public static final double INCOME = 1.2;
	public static final double FLOOD_PROBABILITY = 0.5;
	private static final int CYCLE_LENGTH = 7;
	private static double random = 0.21753313144345698;
	private static final double second_priority = 1000;
	private static final double third_priority = 100;
	private static final double fourth_priority = 100;

	public Controller(IEnvironment environment)
	{
		super(environment);
		Parameters params = RunEnvironment.getInstance().getParameters();
		int stopTime = (Integer) params.getValue("stop_time");
		Log.info("Stop time is = " + stopTime);
		RunEnvironment.getInstance().endAt(stopTime);

		params = RunEnvironment.getInstance().getParameters();
		String regime = (String) params.getValueAsString("regime");

		if (regime.equals("discretionary"))
		{
			Term[] queryParams;
			queryParams = new Term[] { new Atom("government_agent"), new Atom("government"),
					new Atom("discretionaryregimerole"), new org.jpl7.Integer(0) };
			Query.hasSolution("assert(happensAtNarrative(join(?, ?, ?), ?)).", queryParams);
			Log.info("Discretionar based Parameter selected..");
		}

		else
		{
			Log.info("Nothing is going on...");
		}

	}

	/**
	 * called by Repast - set the controller with interval and priority priority
	 */

	public void step()
	{

		Log.info("This is the base method of ControllerAgent of the PlainPlateau Norm Scenario.");

	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void startTick()
	{
		Double tickcount = RepastEssentials.GetTickCount();
		int currentTick = tickcount.intValue();
		setGlobalTick(currentTick);
		Log.info("Currenttick : " + currentTick);
		Log.info("--------------------------------------------------------------------");

		String label = null;
		switch (currentTick % CYCLE_LENGTH)
		{
			case 1:
				label = "receiveIncome"; // Controller class
				break;
			case 2:
				label = "choose_location"; // CitizenAgent class
				break;
			case 3:
				label = "norms_checking"; // CitizenAgent class
				break;
			case 4:
				label = "occurrenceOfFlood"; // Controller class
				break;
			case 5:
				label = "makeDecisionForTaxOrCompensate"; // GovernmentAgent class
				break;
			case 6:
				label = "repair"; // CitizenAgent class
				break;
			case 0:
				label = "consume"; // CitizenAgent class
				break;
			default:
				label = "Invalid label";

		}

		Term[] queryParams;
		queryParams = new Term[] { new org.jpl7.Atom(label), new org.jpl7.Integer(currentTick) };
		Query.hasSolution("assert(label(?,?)).", queryParams);

	}

	@ScheduledMethod(start = 1, interval = CYCLE_LENGTH, priority = fourth_priority)
	public void receiveIncome()
	{

		Double tickcount = RepastEssentials.GetTickCount();
		int currentTick = tickcount.intValue();

		Log.info("Print current Tick value within the giveIncome method : " + currentTick);

		Context context = ContextUtils.getContext(this);
		Iterable<CitizenAgent> i = context.getAgentLayer(CitizenAgent.class);
		Iterator<CitizenAgent> it = i.iterator();
		while (it.hasNext())
		{
			CitizenAgent c = it.next();
			String name = c.getPrologName(); // .toLowerCase();
			Term[] queryParams;
			queryParams = new Term[] { new Atom(name), new org.jpl7.Float(INCOME), new org.jpl7.Integer(currentTick) };
			Query.hasSolution("assert(happensAtNarrative(receive_income(?, ?), ?)).", queryParams);
		}

	}

	@ScheduledMethod(start = 4, interval = CYCLE_LENGTH, priority = fourth_priority)
	public void occurrenceOfFlood()
	{

		Double tickcount = RepastEssentials.GetTickCount();
		int currentTick = tickcount.intValue();

		Log.info("Print current Tick value within the occurrenceOfFlood method : " + currentTick);

		Random rand = new Random();
		if (rand.nextInt(1) < this.FLOOD_PROBABILITY)
		{
			Term[] queryParams;
			queryParams = new Term[] { new org.jpl7.Integer(currentTick) };
			Query.hasSolution("assert(happensAtNarrative(flood, ?)).", queryParams);
		}

	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.LAST_PRIORITY)
	public void addNewRoles()
	{
		// Check for new institution join events at previous time point
		// This should really be done by the parent class in CASP

		Log.info("Starting addNewRoles method");

		Double tickcount = RepastEssentials.GetTickCount();
		int currentTick = tickcount.intValue();

		Log.info("Print current Tick value within the addNewRoles method = " + currentTick);

		int previousTick = currentTick - 1;

		Map<String, Term>[] result;
		result = ExtendedQuery.allSolutions(
				"happensAt(join(Agent, Institution, Role), %1$d)," + "\\+ holdsAt(member(Agent, Institution), %1$d).",
				previousTick);
		for (Map<String, Term> solution : result)
		{
			String agentPrologName = solution.get("Agent").toString();
			String roleName = solution.get("Role").toString();
			Log.debug("Agent {} got role {}::::::::::::::::::::::::::::::::::::::", agentPrologName, roleName);
			IEnvironment env = getEnvironment();
			IAgent ct = env.getAgent(agentPrologName);

			try
			{
				Role<IAgent> r = env.getRole(roleName);
				Log.debug("Making addRole({},{}) call", ct, r);
				ct.addRole(r);
				ct.resetRuleEngine();
			}
			catch (NoSuchRoleException | NullPointerException e)
			{
				Log.error("Exception trying to find role {}: {}", roleName, e.getMessage());
			}
		}

		Map<String, Term>[] result1;
		result1 = ExtendedQuery.allSolutions("happensAt(change_role(Agent, Institution, OldRole, NewRole), %1$d),"
				+ "holdsAt(member(Agent, Institution), %1$d).", previousTick);
		for (Map<String, Term> sol1 : result1)
		{
			String agentPrologName = sol1.get("Agent").toString();
			String oldroleName = sol1.get("OldRole").toString();
			String newroleName = sol1.get("NewRole").toString();
			Log.debug("Agent {} got role {} >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", agentPrologName, oldroleName,
					newroleName);
			IEnvironment env = getEnvironment();
			IAgent ct = env.getAgent(agentPrologName);

			try
			{
				Role<IAgent> roleBeingChangedAwayFrom = env.getRole(oldroleName);
				Role<IAgent> newRoleBeingChangedTo = env.getRole(newroleName);
				Log.debug("Changing role:");
				Log.debug("Making removeRole({}, {}) call <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ", ct,
						roleBeingChangedAwayFrom);

				ct.removeRole(roleBeingChangedAwayFrom.toString());
				Log.debug("Making addRole({},{}) call <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ", ct,
						newRoleBeingChangedTo);
				ct.addRole(newRoleBeingChangedTo);
				ct.resetRuleEngine();
			}
			catch (NoSuchRoleException | NullPointerException e)
			{
				Log.error("Exception trying to find role {}: {}", oldroleName, newroleName, e.getMessage());
			}
		}

		Map<String, Term>[] result2;
		result2 = ExtendedQuery.allSolutions("happensAt(add_role(Agent, Institution, Role), %1$d).", previousTick);
		for (Map<String, Term> sol : result2)
		{
			String agentPrologName = sol.get("Agent").toString();
			String roleName = sol.get("Role").toString();
			Log.debug("Agent {} got role {} .......................................... ", agentPrologName, roleName);
			IEnvironment env = getEnvironment();
			IAgent ct = env.getAgent(agentPrologName);

			try
			{
				Role<IAgent> roleBeingChangedAwayFrom = env.getRole(roleName);
				ct.addRole(roleBeingChangedAwayFrom);
				ct.resetRuleEngine();
			}
			catch (NoSuchRoleException | NullPointerException e)
			{
				Log.error("Exception trying to find role {}: {}", roleName, e.getMessage());
			}

		}
	}

	@ScheduledMethod(start = 1, interval = 1, priority = second_priority)
	public void logNewFluents()
	{

		Double tickcount = RepastEssentials.GetTickCount();
		int currentTick = tickcount.intValue();
		Log.info("Current labels for tick : " + currentTick);
		Map<String, Term>[] solutions = new Query("label(L,?)", new org.jpl7.Integer(currentTick)).allSolutions();

		for (int i = 0; i < solutions.length; i++)
		{
			Log.info("Label = " + solutions[i].get("L"));
		}
		Log.info("--------------------------------------------------------------------");

		Log.info("Print current Tick value within the logNewFluents method : " + currentTick);
		solutions = new Query("holdsAt(Fluent, ?)", new org.jpl7.Integer(currentTick)).allSolutions();
		Log.debug(String.format("New fluents at %d recorded via holdsAt ", currentTick));

		Log.info("Value of the solution for fluents = " + solutions.length);
		for (int i = 0; i < solutions.length; i++)
		{

			Log.info("Fluent = " + solutions[i].get("Fluent"));
		}
		Log.info("--------------------------------------------------------------------");
	}

	@ScheduledMethod(start = 1, interval = 1, priority = ScheduleParameters.LAST_PRIORITY)
	public void logNewEvents()
	{
		Double tickcount = RepastEssentials.GetTickCount();
		int currentTick = tickcount.intValue();
		Log.info("Print current Tick value within the logNewEvents method : " + currentTick);

		Map<String, Term>[] solutions = new Query("happensAt(Event, ?)", new org.jpl7.Integer(currentTick))
				.allSolutions();

		Log.debug(String.format("New events at %d recorded via happensAt ", currentTick));

		Log.info("Value of the solution for events = " + solutions.length);
		for (int i = 0; i < solutions.length; i++)
		{
			Log.info("Event = " + solutions[i].get("Event"));
		}
		Log.info("--------------------------------------------------------------------");
	}

	public void endMethod()
	{
		Log.info("Simulation ended.");
	}
}
