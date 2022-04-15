package caspPlainPlateau.agents;

import java.util.ArrayList;
import java.util.HashMap;
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
	private static final int CYCLE_LENGTH = 6;

	List<Pair<Object, String>> pair = new ArrayList<Pair<Object, String>>();

	List<Pair<Object, String>> pair1 = new ArrayList<Pair<Object, String>>();

	List<Pair<Object, String>> pair2 = new ArrayList<Pair<Object, String>>();

	private int[][][] game2 = { { { 7, 7 }, { 7, 5 } }, { { 5, 7 }, { 5, 5 } } };

	private int[][][] game = { { { 365, 365 }, { 290, 418 } }, { { 418, 290 }, { 333, 333 } } };

	private int[][][] game1 = { { { 365, 365 }, { 333, 365 } }, { { 365, 333 }, { 333, 333 } } };

	public enum regimeSelection
	{
		rulesbasedregime, discretionaryregime
	}

	// constructor
	public CitizenAgent()
	{

	}

	@ScheduledMethod(start = 1)
	public void checkGame()
	{
		if (Query.hasSolution("holdsAt(exp_rule(damage(A,_),not(happ(compensate(A,_)))),1)."))
		{
			this.pair1 = isSecondNashEquilibrium();
			if (this.pair1.get(0).getValue(1).equals(this.pair1.get(1).getValue(1)))
			{
				System.out.println("Value of nash from Second Nash Equilibrium : " + "[" + pair1.get(0).getValue(1)
						+ ", " + pair1.get(1).getValue(1) + "]");
				Log.info("--------------------------------------------------------------------");

				String location1 = pair1.get(0).getValue(1).toString();

				Term[] queryParams;
				queryParams = new Term[] { new Atom(location1) };
				Query.hasSolution(
						"assert(holdsAtCached(exp_rule(and([@(choose_location),member(C,citizens)]),next(location(C,?))),1)).",
						queryParams);
			}
			else
			{
				System.out.println("There is no such expectation after rule-based Parameter has been selected");
			}

		}
		else
		{
			this.pair = isFirstNashEquilibrium();
			if (this.pair.get(0).getValue(1).equals(this.pair.get(1).getValue(1)))
			{
				System.out.println("Value of nash from First Nash Equilibrium : " + "[" + pair.get(0).getValue(1) + ", "
						+ pair.get(1).getValue(1) + "]");
				Log.info("--------------------------------------------------------------------");
				String location2 = pair.get(0).getValue(1).toString();
				Term[] queryParams;
				queryParams = new Term[] { new Atom(location2) };
				Query.hasSolution(
						"assert(holdsAtCached(exp_rule(and([@(choose_location),member(C,citizens)]),next(location(C,?))),1)).",
						queryParams);
			}
			else
			{
				System.out
						.println("There is no such expectation after discretionary based Parameter has been selected");
			}
		}
	}

	public enum Strategy
	{
		PLATEAU, PLAIN;
	}

	public enum DorC
	{
		D, C;
	}

	public static class EnumTest
	{
		Strategy strategy;

		public EnumTest(Strategy strategy)
		{
			this.strategy = strategy;
		}

		public void tellStrategy()
		{
			switch (strategy)
			{
				case PLATEAU:
					System.out.println("When citizen in plateau position they are doing Cooperation with each other");
					break;
				case PLAIN:
					System.out.println("When citizen in plain position they are Defecting with each other");
					break;
				default:
					System.out.println("Nothing to do");
					break;
			}
		}
	}

	private static Map<Pair<String, String>, Map<String, Integer>> gameMap = new HashMap<Pair<String, String>, Map<String, Integer>>();
	private static Map<Strategy, DorC> stategyClassification = new HashMap<Strategy, DorC>();

	static
	{

		EnumTest st1 = new EnumTest(Strategy.PLATEAU);
		st1.tellStrategy();
		EnumTest st2 = new EnumTest(Strategy.PLAIN);
		st2.tellStrategy();

		Map<String, Integer> ddPayoffMap = new HashMap<String, Integer>();
		ddPayoffMap.put("D", 333);
		gameMap.put(new Pair<String, String>("D", "D"), ddPayoffMap);
		Map<String, Integer> ccPayoffMap = new HashMap<String, Integer>();
		ccPayoffMap.put("C", 365);
		gameMap.put(new Pair<String, String>("C", "C"), ccPayoffMap);
		Map<String, Integer> cdPayoffMap = new HashMap<String, Integer>();
		cdPayoffMap.put("C", 290);
		cdPayoffMap.put("D", 418);
		gameMap.put(new Pair<String, String>("C", "D"), cdPayoffMap);
		Map<String, Integer> dcPayoffMap = cdPayoffMap;
		gameMap.put(new Pair<String, String>("C", "D"), dcPayoffMap);

		stategyClassification.put(Strategy.PLAIN, DorC.D);
		stategyClassification.put(Strategy.PLATEAU, DorC.C);

	}

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

	public void make_change_role(String newroleName)
	{
		Double tickcount = RepastEssentials.GetTickCount();
		int currentTick = tickcount.intValue();
		Log.info("Print current Tick value within the make_change_role method : " + currentTick);

		String name = this.getPrologName();

		if (newroleName.equals("citizens_plateaudwellerrole") && this.hasRole("citizens_plaindwellerrole"))
		{
			Term[] queryParams;
			queryParams = new Term[] { new Atom(name), new Atom("citizens"), new Atom("citizens_plaindwellerrole"),
					new Atom("citizens_plateaudwellerrole"), new org.jpl7.Integer(currentTick) };
			Query.hasSolution("assert(happensAtNarrative(change_role(?,?,?,?),?)).", queryParams);
		}
		else if (newroleName.equals("citizens_plaindwellerrole") && this.hasRole("citizens_plateaudwellerrole"))
		{
			Term[] queryParams;
			queryParams = new Term[] { new Atom(name), new Atom("citizens"), new Atom("citizens_plateaudwellerrole"),
					new Atom("citizens_plaindwellerrole"), new org.jpl7.Integer(currentTick) };
			Query.hasSolution("assert(happensAtNarrative(change_role(?,?,?,?),?)).", queryParams);
		}
		else
		{
			System.out.println("No change role happen");
		}

	}

	// TO DO: Allow some actions to be done at the same time (e.g. join institution and do another
	// action)

	public String chooseDecision(List<String> possActs, String nashLocation)
	{

		System.out.println("Print game :" + gameMap);
		System.out.println("Print the value of stategyClassification : " + stategyClassification);
		System.out.println(Strategy.PLAIN.ordinal());
		System.out.println(Strategy.PLAIN.name());
		System.out.println(Strategy.PLATEAU.ordinal());
		System.out.println(Strategy.PLATEAU.name());

		String nashAction = " ";

		System.out.println("Possible actions are : " + possActs);
		System.out.println("Possible nash location is : " + nashLocation);
		for (int i = 0; i < possActs.size(); i++)
		{
			if (possActs.get(i).contains(nashLocation.toLowerCase()))
			{
				nashAction = possActs.get(i);
				System.out.println("Possible nash Action is : " + nashAction);
			}
		}
		return nashAction;
	}

	/**
	 * * Solve Nash equlibria and print the results.
	 */

	// public List<Pair<Object, String>> ispreferenceGame()
	public List<Pair<Object, String>> ispreferenceGame()
	{
		float max = 0;
		for (int i = 0; i < 2; i++)
		{
			for (int j = 0; j < 2; j++)
			{
				float x = ((this.game2[i][j][0] + this.game2[i][j][1]) / 2);
				if (max < x)
				{
					max = x;
				}
			}
		}
		System.out.println("Prefered maximum average value is: " + max);
		List<Pair<Integer, Integer>> pref = new ArrayList<Pair<Integer, Integer>>();
		List<Pair<Object, String>> replacements = new ArrayList<Pair<Object, String>>();
		for (int i = 0; i < 2; i++)
		{
			for (int j = 0; j < 2; j++)
			{
				if (((this.game2[i][j][0] + this.game2[i][j][1]) / 2) == max)
				{
					System.out.println("prefered max value found at: (" + i + ", " + j + ")");
					pref.add(new Pair<Integer, Integer>(this.game[i][j][0], this.game[i][j][1]));

					replacements.add(new Pair<Object, String>(pref.get(0).getValue(0), "plateau"));
					replacements.add(new Pair<Object, String>(pref.get(0).getValue(1), "plateau"));
					System.out.println("Within the Pair first place value ::" + replacements.get(0).getValue(0) + " "
							+ " whisch is replaced by ::" + replacements.get(0).getValue(1));

					System.out.println("Within the Pair second place value ::" + replacements.get(1).getValue(0) + " "
							+ "which is replaced by  ::" + replacements.get(1).getValue(1));
				}

			}
		}

		return replacements;
	}

	public List<Pair<Object, String>> isFirstNashEquilibrium()

	{
		int[] b_array = new int[2];
		int[] a_array = new int[2];

		for (int i = 0; i < 2; i++)
		{
			int b_max = Integer.MIN_VALUE;
			int a_max = Integer.MIN_VALUE;
			for (int j = 0; j < 2; j++)
			{
				if (this.game[i][j][1] > b_max)
				{
					b_array[i] = this.game[i][j][0];
					b_max = this.game[i][j][1];

				}
				if (this.game[j][i][0] > a_max)
				{
					a_array[i] = this.game[i][j][1];
					a_max = this.game[j][i][0];// each row is another b_array
				}

			}

		}

		int found = 0;

		Character[][] myArray = new Character[2][2];
		List<Pair<Integer, Integer>> nash = new ArrayList<Pair<Integer, Integer>>();
		List<Pair<Object, String>> replacements = new ArrayList<Pair<Object, String>>();

		for (int i = 0; i < 2; ++i)
		{
			for (int j = 0; j < 2; ++j)
			{
				if (this.game[i][j][1] == b_array[i] && this.game[i][j][0] == a_array[j])
				{

					Log.info("--------------------------------------------------------------------");
					System.out.println("Nash equilibrium found at: (" + i + ", " + j + ") => Value: ["
							+ this.game[i][j][0] + ", " + this.game[i][j][1] + "]");

					myArray[i][j] = 'T';
					++found;

					nash.add(new Pair<Integer, Integer>(this.game[i][j][0], this.game[i][j][1]));

					replacements.add(new Pair<Object, String>(nash.get(0).getValue(0), "plain"));
					replacements.add(new Pair<Object, String>(nash.get(0).getValue(1), "plain"));

					System.out.println("Within the Pair first place value ::" + replacements.get(0).getValue(0) + " "
							+ " replaced by ::" + replacements.get(0).getValue(1));

					System.out.println("Within the Pair second place value ::" + replacements.get(1).getValue(0) + " "
							+ "replaced by  ::" + replacements.get(1).getValue(1));

					if (nash.size() == 1)
					{
						System.out.println("There is only one nash value !!!");

					}
					else if (nash.size() > 1)
					{
						System.out.println(" Stop, more than one nash are there !!!");

					}
					else
					{
						System.out.println("There is no nash value within the list ...");
					}

				}

				else
				{
					myArray[i][j] = 'F';

				}
			}

		}

		System.out.println("The Nash matrix is :");
		for (int i = 0; i < myArray.length; i++)
		{
			for (int j = 0; j < myArray[i].length; j++)
			{
				System.out.print(myArray[i][j] + " ");
			}
			System.out.println();
		}

		System.out.println("Found " + found + " Nash equilibria\n");
		return replacements;

	} // end findNashEquilibrium

	public List<Pair<Object, String>> isSecondNashEquilibrium()

	{
		int[] b_array = new int[2];
		int[] a_array = new int[2];

		for (int i = 0; i < 2; i++)
		{
			int b_max = Integer.MIN_VALUE;
			int a_max = Integer.MIN_VALUE;
			for (int j = 0; j < 2; j++)
			{
				if (this.game1[i][j][1] > b_max)
				{
					b_array[i] = this.game1[i][j][0];
					b_max = this.game1[i][j][1];

				}
				if (this.game1[j][i][0] > a_max)
				{
					a_array[i] = this.game1[i][j][1];
					a_max = this.game1[j][i][0];// each row is another b_array
				}

			}

		}

		int found = 0;

		Character[][] myArray = new Character[2][2];
		List<Pair<Integer, Integer>> nash = new ArrayList<Pair<Integer, Integer>>();
		List<Pair<Object, String>> replacements = new ArrayList<Pair<Object, String>>();

		for (int i = 0; i < 2; ++i)
		{
			for (int j = 0; j < 2; ++j)
			{
				if (this.game1[i][j][1] == b_array[i] && this.game[i][j][0] == a_array[j])
				{

					Log.info("--------------------------------------------------------------------");
					System.out.println("Nash equilibrium found at: (" + i + ", " + j + ") => Value: ["
							+ this.game1[i][j][0] + ", " + this.game1[i][j][1] + "]");

					myArray[i][j] = 'T';
					++found;

					nash.add(new Pair<Integer, Integer>(this.game1[i][j][0], this.game1[i][j][1]));

					replacements.add(new Pair<Object, String>(nash.get(0).getValue(0), "plateau"));
					replacements.add(new Pair<Object, String>(nash.get(0).getValue(1), "plateau"));

					System.out.println("Within the Pair first place value ::" + replacements.get(0).getValue(0) + " "
							+ " replaced by ::" + replacements.get(0).getValue(1));

					System.out.println("Within the Pair second place value ::" + replacements.get(1).getValue(0) + " "
							+ "replaced by  ::" + replacements.get(1).getValue(1));

					if (nash.size() == 1)
					{
						System.out.println("There is only one nash value !!!");

					}
					else if (nash.size() > 1)
					{
						System.out.println(" Stop, more than one nash are there !!!");

					}
					else
					{
						System.out.println("There is no nash value within the list ...");
					}

				}

				else
				{
					myArray[i][j] = 'F';

				}
			}

		}

		System.out.println("The Nash matrix is :");
		for (int i = 0; i < myArray.length; i++)
		{
			for (int j = 0; j < myArray[i].length; j++)
			{
				System.out.print(myArray[i][j] + " ");
			}
			System.out.println();
		}

		System.out.println("Found " + found + " Nash equilibria\n");
		return replacements;

	} // end findNashEquilibrium

	/**
	 * consume method for CitizenAgent
	 */

	@ScheduledMethod(start = 6, interval = CYCLE_LENGTH)
	public void consume()
	{

		Double tickcount = RepastEssentials.GetTickCount();
		int currentTick = tickcount.intValue();
		String name = this.getPrologName();
		Term[] queryParams;
		queryParams = new Term[] { new Atom(name), new org.jpl7.Integer(currentTick) };
		Query.hasSolution("assert(happensAtNarrative(consumed(?),?)).", queryParams);
	}

	/**
	 * Repair method for the CitizenAgent
	 */
	@ScheduledMethod(start = 5, interval = CYCLE_LENGTH)
	public void repair()
	{

		Double tickcount = RepastEssentials.GetTickCount();
		int currentTick = tickcount.intValue();
		String name = this.getPrologName();
		if (this.hasRole("citizens_plateaudwellerrole"))
		{
			Term[] queryParams;
			queryParams = new Term[] { new Atom(name), new org.jpl7.Integer(SPENT), new org.jpl7.Integer(currentTick) };
			Query.hasSolution("assert(happensAtNarrative(repair(?, ?), ?)).", queryParams);
		}
	}

	/**
	 * makeDecision method for the CitizenAgent
	 */
	@ScheduledMethod(start = 2, interval = CYCLE_LENGTH)
	public void choose_location()
	{
		Double tickcount = RepastEssentials.GetTickCount();
		int currentTick = tickcount.intValue();

		String location = null;

		Term[] queryParams;
		queryParams = new Term[] { new org.jpl7.Integer(currentTick) };

		Map<String, Term> resultMap = Query
				.oneSolution("setof(L, holdsAt(exp(_,_,_,next(location(_,L))),?), Locations)", queryParams);

		String[] da = org.jpl7.Util.atomListToStringArray(resultMap.get("Locations"));

		int numOfLocations = da.length;

		Log.info("Location is : " + da[0]);

		if (numOfLocations == 1)
		{
			location = da[0];
			Log.info("Citizen will go to: " + location);
		}
		else
		{
			List<Rule> mr = getRuleEngine().getEngine().getMatchingRules(this);
			List<String> possibleActions = new ArrayList<>();
			for (int i = 0; i < mr.size(); i++)
			{
				possibleActions.add(mr.get(i).getOutcome());
			}
			Log.info(" Randomly selected possible action " + possibleActions.get(0) + " or " + possibleActions.get(1));

		}

		Log.info("--------------------------------------------------------------------");
		int tick = getEnvironment().getTick();
		Log.info(" Step: " + this + " t=" + tick);

		List<Rule> mr = getRuleEngine().getEngine().getMatchingRules(this);
		List<String> possibleActions = new ArrayList<>();

		for (int i = 0; i < mr.size(); i++)
		{
			possibleActions.add(mr.get(i).getOutcome());
		}
		String selectedActionName = "";

		if (possibleActions.size() > 0)
		{

			selectedActionName = chooseDecision(possibleActions, location);
			Log.info("selectedActionName is : " + selectedActionName);
		}
		if (selectedActionName.equals(""))
		{

		}
		else if (selectedActionName.equals("moveplain"))
		{
			make_change_role("citizens_plaindwellerrole");
		}
		else if (selectedActionName.equals("stayplateau"))
		{
			make_change_role("citizens_plaindwellerrole");
		}
		else if (selectedActionName.equals("moveplateau"))
		{
			make_change_role("citizens_plateaudwellerrole");
		}
		else if (selectedActionName.equals("stayplain"))
		{
			make_change_role("citizens_plateaudwellerrole");
		}
	}

	public boolean prologQuery(String q, Object... params)
	{
		return ExtendedQuery.hasSolution(q, params);
	}

}
