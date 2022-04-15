package caspPlainPlateauNorm;

//import caspPlainPlateau.agents.CitizenAgent.EnumTest;
//import caspPlainPlateau.agents.CitizenAgent.Strategy;

public class Test
{
	public static void main(String[] args)
	{
		EnumTest st1 = new Test.EnumTest(Strategy.PLATEAU);
		System.out.println(Strategy.PLAIN.ordinal());
		System.out.println(Strategy.PLAIN.name());
		System.out.println(Strategy.PLATEAU.ordinal());
		System.out.println(Strategy.PLATEAU.name());

		st1.tellStrategy();
		EnumTest st2 = new EnumTest(Strategy.PLAIN);
		st2.tellStrategy();
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
}
