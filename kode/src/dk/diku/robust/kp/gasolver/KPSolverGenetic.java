package dk.diku.robust.kp.gasolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.Population;
import org.jgap.impl.BooleanGene;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.MutationOperator;
import org.jgap.impl.WeightedRouletteSelector;

import dk.diku.robust.autogen.knapsackexperiment.KnapsackExperiment;
import dk.diku.robust.autogen.stochknapsack.StochasticKnapsackProblem;
import dk.diku.robust.core.Solver;
import dk.diku.robust.core.model.SolutionData;
import dk.diku.robust.kp.SKPCalculator;
import dk.diku.robust.kp.exactsolver.KPSolverExact;
import dk.diku.robust.kp.model.KP;
import dk.diku.robust.kp.model.KPFactory;
import dk.diku.robust.kp.model.KPSolution;

public class KPSolverGenetic extends Solver {

	private static Random rand = new Random(System.currentTimeMillis());
	private static int mutationRate = 1000;
	private KnapsackExperiment experiment;
	private int populationSize;
	private Genotype genotype;
	private StochasticKnapsackProblem autogenSKP;
	private Configuration conf;

	public KPSolverGenetic(KnapsackExperiment exp, StochasticKnapsackProblem autogenSKP) throws Exception {
		super();
		this.experiment = exp;
		this.autogenSKP = autogenSKP;
		// pop size and num simulations
		try {
			populationSize = experiment.getGeneticAlgorithm().getPopulationSize().intValue();
		}
		catch(NullPointerException e) {
			System.err.println("Error, check for missing values in experiment xml file");
			System.exit(1);
		}
		// Make configuration (JGap thing)
		Configuration.reset();
		conf = new DefaultConfiguration();
		Configuration.reset();
		// Inverse fitness evaluation
		conf.setFitnessEvaluator(new RobustnessFitnessEvaluator());
		// Always preserve fittest individual
		conf.setPreservFittestIndividual(true);
		// Generate all new individuals based on previous generation
		conf.setSelectFromPrevGen(1.0);
		//Set mutationrate
		conf.addGeneticOperator(new MutationOperator(conf, mutationRate));
		// Remove the default natural selector and add a weighted roulette wheel
		//conf.removeNaturalSelectors(false);
		//conf.addNaturalSelector(conf.getNaturalSelector(false, 0), true);
		//conf.removeNaturalSelectors(false);
		//conf.addNaturalSelector(new WeightedRouletteSelector(conf), false);
		// Set the fitness function we want to use. We construct it with
		// the target volume passed in to this method.
		KPFitnessFunction myFunc = new KPFitnessFunction(autogenSKP, experiment);
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
		if(!experiment.getGeneticAlgorithm().isUseScenarioSeeding()){
			// Generate a random startin population
			int numItems = autogenSKP.getItems().getItem().size();
			IChromosome sampleChromosome = randomChromosome(numItems);
			conf.setSampleChromosome(sampleChromosome);
			conf.setPopulationSize(populationSize);
			genotype = Genotype.randomInitialGenotype(conf);
		}
		else{
			IChromosome[] chromosomes = new IChromosome[populationSize];
			KPFactory fact = KPFactory.getInstance();
			KPSolverExact solver = new KPSolverExact();
			KPSolution solution;
			KP kp;
			// Inserting fat solution as a seed
			chromosomes[0] = chromosomeFromSolution(SKPCalculator.findFatSolution(autogenSKP, experiment.getDesiredRobustness()));
				
			// Inserting deterministic solution as a seed
			kp = fact.getDeterministicScenario(autogenSKP);
			chromosomes[1] = chromosomeFromSolution(solver.solve(kp));
			
			System.out.println(myFunc.evaluate(chromosomes[1]));
				
			for(int j=2;j<populationSize;j++){
				kp = fact.sampleScenario(autogenSKP);
				solution = solver.solve(kp);
				chromosomes[j] = chromosomeFromSolution(solution);
				
				//System.out.println(myFunc.calcAverageProfitAndRobustness(chromosomes[j])[2]);
							
				//chromosomes[j] = chromosomes[0];
			}
			conf.setSampleChromosome(chromosomes[0]);
			conf.setPopulationSize(populationSize);
			Population pop = new Population(conf,chromosomes);
			genotype = new Genotype(conf,pop);
			
		}



	}

	public IChromosome chromosomeFromSolution(KPSolution sol) throws Exception{
		int numItems = sol.getKp().getItems();
		boolean[] itemSelected = sol.getItemSelected();
		Gene[] genes = new Gene[numItems];
		for (int i = 0; i < numItems; i++) {
			BooleanGene g = new BooleanGene(conf);
			g.setAllele(itemSelected[i+1]);
			genes[i] = g;
		}
		IChromosome solutionChromosome = new Chromosome(conf, genes);
		return solutionChromosome;
	}

	public IChromosome randomChromosome(int numItems) throws Exception{
		Gene[] sampleGenes = new Gene[numItems];
		for (int i = 0; i < numItems; i++) {
			BooleanGene g = new BooleanGene(conf);
			g.setAllele(rand.nextBoolean());
			sampleGenes[i] = g;
		}
		IChromosome sampleChromosome = new Chromosome(conf, sampleGenes);
		return sampleChromosome;
	}

	@Override
	public synchronized void doIteration() {
		genotype.evolve();
	}

	@Override
	public synchronized SolutionData getCurrentBest() {
		return getGlobalBest();
	}

	@Override
	public synchronized SolutionData getGlobalBest() {
		IChromosome bestSolutionSoFar = genotype.getFittestChromosome();
		SolutionData data = (SolutionData) bestSolutionSoFar.getApplicationData();
		return data;
	}

	@Override
	public synchronized List<SolutionData> getSolutions() {
		ArrayList<SolutionData> solutions = new ArrayList<SolutionData>();

		Population population = genotype.getPopulation();
		for(int i=0; i<populationSize; i++) {
			IChromosome chrom = (IChromosome)population.getChromosomes().get(i);
			if(chrom.getApplicationData() != null) {
				solutions.add((SolutionData)chrom.getApplicationData());
			}
		}
		return solutions;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	@Override
	public void finalTask() {
		IChromosome bestSolutionSoFar = genotype.getFittestChromosome();
		System.out.println("The best solution has a fitness value of " +
				bestSolutionSoFar.getFitnessValue());
		System.out.println("It contained the following: ");
		Gene[] genes = bestSolutionSoFar.getGenes();
		int numItems = autogenSKP.getItems().getItem().size();
		for(int i=0; i<numItems; i++) {
			BooleanGene iGene = (BooleanGene)genes[i];
			System.out.print(iGene.booleanValue() + ", ");
		}
		System.out.println();

		System.out.println();
		System.out.println("Thank you for watching");

	}
}
