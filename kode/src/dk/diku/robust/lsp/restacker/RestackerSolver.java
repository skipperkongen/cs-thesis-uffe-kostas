package dk.diku.robust.lsp.restacker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import dk.diku.robust.autogen.sclsp_experiment.Experiment;
import dk.diku.robust.autogen.sclsp_instance.SCLSPInstance;
import dk.diku.robust.autogen.sclsp_solution.CLSPSolution;
import dk.diku.robust.autogen.sclsp_solution.ItemOnStock;
import dk.diku.robust.autogen.sclsp_solution.ItemProduction;
import dk.diku.robust.autogen.sclsp_solution.ProductionTimePeriod;
import dk.diku.robust.autogen.sclsp_solution.StockTimePeriod;
import dk.diku.robust.core.Solver;
import dk.diku.robust.core.model.SolutionData;
import dk.diku.robust.lsp.simulation.SimConclusion;
import dk.diku.robust.util.GiantUnmarshaller;

public class RestackerSolver extends Solver {
	
	private static final int MAX_STOCK = 10000;
	private static final int MAX_PRODUCTION = 10000;
	
	Experiment exp; 
	SCLSPInstance instance;
	List<LSPSolution> solutions;
	List<LSPSolution> robustSolutions;
	Uniform uniform = new Uniform(new MersenneTwister());
	RestackerOperator restacker;
	LSPSimulator simulator;
	
	public RestackerSolver(Experiment exp, SCLSPInstance instance) {
		super();
		this.exp = exp;
		this.instance = instance;
		this.restacker = new RestackerOperator(exp, instance);
		this.solutions = new ArrayList<LSPSolution>();
		this.robustSolutions = new ArrayList<LSPSolution>();
		this.simulator = new LSPSimulator(exp, instance);
		// indlæs seeds
		int popSize = exp.getGeneticAlgorithm().getPopulationSize().intValue();
		CLSPSolution[] seeds = new CLSPSolution[popSize];
		if(exp.getGeneticAlgorithm().usingScenarioSeeding()) {
			String seedsPath = exp.getPathToSeeds();
			String baseName = exp.getSeedBaseName();
			try {
				seeds = readSeeds(seedsPath, baseName, popSize);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		for(int i=0; i<seeds.length; i++) {
			solutions.set(i, makeSolution(seeds[i], instance));
		}
	}

	@Override
	public void doIteration() {
		// simulate solutions
		for(int i=0; i<solutions.size(); i++) {
			LSPSolution solution = solutions.get(i);
			SimConclusion conclusion = simulator.simulateLSP(solution);
			solution.conclusion = conclusion;
			// test if done
			if(conclusion.robustnessLB >= exp.getDesiredRobustness()) {
				robustSolutions.add(solution);
				solutions.remove(i);
				continue;
			}
			// Restack solution
			restacker.restack(solution);
		}
	}

	@Override
	public void finalTask() {
		// TODO Auto-generated method stub
		System.out.println("Done");

	}

	@Override
	public SolutionData getCurrentBest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SolutionData getGlobalBest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SolutionData> getSolutions() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private CLSPSolution[] readSeeds(String seedsPath, String baseName, int numSeeds) throws FileNotFoundException, JAXBException, SAXException {
		CLSPSolution[] seeds = new CLSPSolution[numSeeds]; 
		for(int i=0; i<numSeeds; i++) {
			String fileName = seedsPath + (!seedsPath.endsWith("/")?"/":"") + baseName + (i+1) + ".xml";
			File file = new File(fileName);
			if(file.exists()) {
				System.out.println("Reading seed: " + fileName);
				CLSPSolution sol = GiantUnmarshaller.xmlToCLSPSolution(fileName);
				seeds[i] = sol;
			}
			else {
				seeds[i] = null;
			}
		}
		return seeds;
	}

	private LSPSolution makeSolution(CLSPSolution seed, SCLSPInstance instance) {

		int numItems = instance.getNumItems();
		int numPeriods = instance.getNumPeriods();
		int numPeriodsPlus = numPeriods+1;
		int[] initialStock = new int[numItems];
		int[][] production = new int[numItems][numPeriods];

		if(seed == null) {
			// make random solution
			for(int i = 0; i < numItems; i++) {
				for(int t = 0; t < numPeriodsPlus; t++) {
					// zero stock, random production
					if(t == 0) {
						// random stock
						initialStock[i] = uniform.nextIntFromTo(0, MAX_STOCK);
					}
					else {
						// random production
						production[i][t] = uniform.nextIntFromTo(0, MAX_PRODUCTION);
					}
				}
			}
		}
		else {
			// make solution based on seed
			// set initial stock
			for(StockTimePeriod period : seed.getOnStock().getStockTimePeriod()) {
				if(period.getTimePeriod().intValue() == 0) {
					for(ItemOnStock itemOnStock : period.getItemOnStock()) {
						int id = itemOnStock.getItemId().intValue();
						int amount = itemOnStock.getAmount().intValue();
						initialStock[id-1] = amount;
					}
				}
			}
			// set production
			for(ProductionTimePeriod prodPeriod : seed.getBeingProduced().getProductionTimePeriod()) {
				int timePeriod = prodPeriod.getTimePeriod().intValue();
				for(ItemProduction itemProd : prodPeriod.getItemProduction()) {
					int id = itemProd.getItemId().intValue();
					int amount = itemProd.getAmount().intValue();
					production[id-1][timePeriod-1] = amount;
				}
			}
		}
		return new LSPSolution(initialStock, production);
	}
	
}
