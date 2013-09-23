

/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licensing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package dk.diku.robust.kp.gasolver;


import java.util.Random;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.Population;
import org.jgap.impl.BooleanGene;
import org.jgap.impl.DefaultConfiguration;

import dk.diku.robust.autogen.stochknapsack.ItemList;
import dk.diku.robust.autogen.stochknapsack.ItemType;
import dk.diku.robust.autogen.stochknapsack.StochasticKnapsackProblem;
import dk.diku.robust.core.model.SolutionData;
import dk.diku.robust.kp.exactsolver.KPSolverExact;
import dk.diku.robust.kp.model.KP;
import dk.diku.robust.kp.model.KPFactory;
import dk.diku.robust.kp.model.KPSolution;
import dk.diku.robust.stat.StatMath;
import dk.diku.robust.view.OldPlot;

/**
 * This class provides an implementation of the classic knapsack problem
 * using a genetic algorithm. The goal of the problem is to reach a given
 * volume (of a knapsack) by putting a number of items into the knapsack.
 * The closer the sum of the item volumes to the given volume the better.
 * <p>
 * For further descriptions, compare the "coins" example also provided.
 *
 * @author Klaus Meffert
 * @since 2.3
 */
public class GAKnapsackMain {

	private static Random rand = new Random(System.currentTimeMillis());
	private static KP problem;
	private static KPFactory factory;
	private static String xmlLocation;

	public static final int POPULATION = 50;
	private static final int MAX_GENERATIONS = 1000;
	
	public final static int PRINT_INTERVAL = 1;
	private static final double DESIRED_ROBUSTNESS = 0.95;

	public static void findSorting() throws Exception {
		// plotter
		OldPlot plot = new OldPlot(POPULATION);
		plot.show();
		
		// Start with a DefaultConfiguration, which comes setup with the
		// most common settings.
		// -------------------------------------------------------------
		Configuration conf = new DefaultConfiguration();
		Configuration.reset();
		// inverse fitness evaluation
		conf.setFitnessEvaluator(new RobustnessFitnessEvaluator());
		conf.setPreservFittestIndividual(true);
		// Set the fitness function we want to use. We construct it with
		// the target volume passed in to this method.
		// ---------------------------------------------------------
		KPFitnessFunction myFunc = new KPFitnessFunction();
		conf.setFitnessFunction(myFunc);

		// Now we need to tell the Configuration object how we want our
		// Chromosomes to be setup. We do that by actually creating a
		// sample Chromosome and then setting it on the Configuration
		// object. As mentioned earlier, we want our Chromosomes to each
		// have as many genes as there are different items available. We want the
		// values (alleles) of those genes to be integers, which represent
		// how many items of that type we have. We therefore use the
		// IntegerGene class to represent each of the genes. That class
		// also lets us specify a lower and upper bound, which we set
		// to senseful values (i.e. maximum possible) for each item type.
		// --------------------------------------------------------------
		Gene[] sampleGenes = new Gene[problem.getItems()];
		for (int i = 0; i < problem.getItems(); i++) {
			BooleanGene g = new BooleanGene(conf);
			g.setAllele(rand.nextBoolean());
			sampleGenes[i] = g;
		}
		IChromosome sampleChromosome = new Chromosome(conf, sampleGenes);
		conf.setSampleChromosome(sampleChromosome);
		conf.setPopulationSize(POPULATION);
		Genotype population;
		population = Genotype.randomInitialGenotype(conf);
		KPSolution solution = getFatSolutionNormal(DESIRED_ROBUSTNESS);
		int generation = 0;
		while (generation < MAX_GENERATIONS) {
			population.evolve();
			double[] robustness = new double[POPULATION];
			double[] solutionValues = new double[POPULATION];
			populateArrays(population, robustness, solutionValues);
			IChromosome bestSolution = population.getFittestChromosome();
			SolutionData dataBest = ((SolutionData)bestSolution.getApplicationData());
			plot.plotGeneration(robustness, solutionValues);
			plot.plotValuesFatVersusGA(solution.getTotalProfit(), dataBest.objectiveValue);
			generation++;
			if(generation % PRINT_INTERVAL == 0) {
				System.out.print("G"+ generation + " /// GA sol: ");
				System.out.print(dataBest.objectiveValue);
				System.out.print(", GA Rob L: ");
				System.out.print(dataBest.robustnessLow);
				System.out.print(", GA Rob H: ");
				System.out.print(dataBest.robustnessHigh);
				System.out.print(" /// Fat sol: ");
				System.out.print(solution.getTotalProfit());
//				System.out.print(", Fat cap: ");
//				System.out.print(solution.getUsedCapacity());
				System.out.print(", Fat rob L: ");
				System.out.print(myFunc.calcAverageProfitAndRobustness(solution.getChromosome(conf))[0]);
				System.out.print(", Fat rob H: ");
				System.out.println(myFunc.calcAverageProfitAndRobustness(solution.getChromosome(conf))[1]);
				//printSolution(population.getFittestChromosome());
			}
		}

		IChromosome bestSolutionSoFar = population.getFittestChromosome();
		System.out.println("The best solution has a fitness value of " +
				bestSolutionSoFar.getFitnessValue());
		System.out.println("It contained the following: ");
		printSolution(bestSolutionSoFar);

		System.out.println();
		System.out.println("Generations: " + generation);
		System.out.println("Thank you for watching");
	}
	


	private static void populateArrays(Genotype genotype, double[] robustness, double[] solutionValues) {
		Population population = genotype.getPopulation();
		for(int i=0; i<POPULATION; i++) {
			IChromosome chrom = (IChromosome)population.getChromosomes().get(i);
			robustness[i] = 
				chrom.getApplicationData() == null ? 
						0 : ((SolutionData)chrom.getApplicationData()).robustnessLow;
			solutionValues[i] = 
				chrom.getApplicationData() == null ? 
						0 : ((SolutionData)chrom.getApplicationData()).objectiveValue;
		}
	}

	public static void printSolution(IChromosome c) {
		Gene[] genes = c.getGenes();
		for(int i=0; i<problem.getItems(); i++) {
			BooleanGene iGene = (BooleanGene)genes[i];
			System.out.print(iGene.booleanValue() + ", ");
		}
		System.out.println();
	}

	public static KPSolution getFatSolutionNormal(double robustness) throws Exception{
		StochasticKnapsackProblem skp = factory.readKnapsackProblem(xmlLocation);
		KP newProblem = factory.getInstance().sampleScenario(skp);
		if(skp.getStochasticCapacity()!=null){
			double mean = skp.getStochasticCapacity().getNormal().getMean();
			double variance = skp.getStochasticCapacity().getNormal().getVariance();
			int capacity = (int)StatMath.getLowerBoundNormal(mean, variance, robustness);
			newProblem.setCapacity(capacity);
		}
		else{
			int capacity = (int) skp.getCapacity().floatValue();
			newProblem.setCapacity(capacity);
		}
		ItemList items = skp.getItems();
		int[] profits = new int[items.getItem().size()+1];
		int[] weights = new int[items.getItem().size()+1];
		int j = 1;
		double robustnessInv = 1-robustness;
		for(ItemType i: items.getItem()){
		
			if(i.getStochasticProfit() != null){
				double mean = i.getStochasticProfit().getNormal().getMean();
				double variance = i.getStochasticProfit().getNormal().getVariance();
				int profit = (int)StatMath.getLowerBoundNormal(mean, variance, robustness);
				newProblem.setProfit(j, profit);
			}
			if(i.getStochasticWeight() != null){
				double mean = i.getStochasticWeight().getNormal().getMean();
				double variance = i.getStochasticWeight().getNormal().getVariance();
				int weight = (int)StatMath.getLowerBoundNormal(mean, variance, robustnessInv);
				newProblem.setWeight(j, weight);
			}

			
			j++;
		}
		KPSolverExact solver = new KPSolverExact();
		KPSolution solution = solver.solve(newProblem);
			
		return solution;
	}
	/**
	 * Main method. A single command-line argument is expected, which is the
	 * volume to create (in other words, 75 would be equal to 75 ccm).
	 *
	 * @param args first and single element in the array = volume of the knapsack
	 * to fill as a double value
	 *
	 * @author Klaus Meffert
	 * @since 2.3
	 */
	public static void main(String[] args) {
		try {
			//xmlLocation = "xml/stochastic_knapsack.xml";
			//xmlLocation = "xml/stochastic_knapsack_large_values.xml";
			xmlLocation = "xml/non_stochastic_knapsack.xml";
	    	factory = KPFactory.getInstance();
	    	StochasticKnapsackProblem skp = factory.readKnapsackProblem(xmlLocation);
	    	problem = factory.sampleScenario(skp);
			findSorting();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
