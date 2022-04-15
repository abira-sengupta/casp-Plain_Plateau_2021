package caspPlainPlateau;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

import casp.Log;
import casp.environment.Environment;
import casp.repast.IRepastEnvironment;
import casp.repast.RepastEnvironment;
import casp.repast.RepastSpatial;
import casp.scenario.Scenario;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

/**
 * The builder for the CASP PlainPlateau Scenario Implements the Repast ContextBuilder build() method.
 * 
 * @author senab196
 */

public class PlainPlateauBuilder implements ContextBuilder<Object>
{

	@Override
	public Context<Object> build(Context<Object> context)
	{

		String scenarioFileName = "casp-config/plainplateau/scenarios/institution/plainplateauScenario.json ";

		String scenarioPath = "";

		Log.setLogger(scenarioPath + "casp-config/CaspLogger.log4j2.properties");

		Log.info("Building with: " + this.getClass().getName());

		Log.info("This is from Builder : ");

		Log.setLogger(scenarioPath + "casp-config/CaspLogger.log4j2.properties");

		/*************************************************************************
		 * Set the context ID. Should be the same string that appears in the repast project generated context.xml file
		 */

		context.setId("CaspPlainPlateau");

		/*************************************************************************
		 * Set up the Repast spatial stuff Note that these projections need to also exist in the context.xml file
		 * otherwise weird errors result, e.g. ArrayIndexOutOfBounds Exception.
		 * <projection type="continuous space" id="space"></projection> <projection type="grid" id="grid"></projection>
		 */

		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("space", context,
				new RandomCartesianAdder<Object>(), new repast.simphony.space.continuous.WrapAroundBorders(), 50, 50);

		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context, new GridBuilderParameters<Object>(
				new WrapAroundBorders(), new SimpleGridAdder<Object>(), true, 50, 50));

		try
		{
			System.setProperty("mvel2.disable.jit", "true");
		}
		catch (SecurityException se)
		{
			se.printStackTrace();
		}
		/*************************************************************************
		 * Loading the Scenario - abort run if this fails - see Log files for details The Logger (including the name of
		 * the output file) is defined in CaspLogger.properties
		 */

		Log.info("Loading scenario from : " + scenarioPath + scenarioFileName);

		Scenario scenario = null;

		try
		{
			scenario = Scenario.getLoadedScenario(scenarioPath, scenarioFileName);
		}
		catch (FileNotFoundException e)
		{
			Log.error("Exception loading Scenario : " + e.getMessage());
			e.printStackTrace();
		}

		if (scenario == null || !scenario.isLoaded())
		{
			Log.info("Scenario load failed - Ending run.");
			RunEnvironment.getInstance().endRun();
			return context;
		}
		else
			Log.info("Scenario loaded Successfully.");

		/*************************************************************************
		 * Set up the Environment. - set the Spatial object which provides a small set of spatial operations which might
		 * be needed by some simulations. - Add the agents by passing the loaded Scenario object and the context object
		 */

		IRepastEnvironment environment;
		Controller controller;
		try
		{

			environment = Environment.getInstance(RepastEnvironment.class);
			environment.setSpatial(new RepastSpatial(space, grid));
			environment.init(scenario, context);
			controller = new Controller(environment);
			context.add(controller);
			Log.info("Environment ready.");

			/*************************************************************************
			 * add a stop method
			 */

			/*
			 * what to do at the end of the simulation
			 */
			ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
			ScheduleParameters stop = ScheduleParameters.createAtEnd(ScheduleParameters.LAST_PRIORITY);
			schedule.schedule(stop, controller, "endMethod");

		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e)
		{
			Log.fatal("Environment creation failed.");
			e.printStackTrace();
		}

		/*************************************************************************
		 * Scenario is ready to go?
		 */
		Log.report();
		if (Log.getErrorCount() > 0)
			Log.info("context.setId()");
		else
			Log.info("Casp PlainPlateau Scenario is Ready");
		return context;

	}
}
