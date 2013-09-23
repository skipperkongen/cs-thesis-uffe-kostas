package dk.diku.robust.lsp.restacker;

import org.jgap.IChromosome;
import org.jgap.impl.IntegerGene;

import dk.diku.robust.autogen.sclsp_experiment.Experiment;
import dk.diku.robust.autogen.sclsp_instance.SCLSPInstance;
import dk.diku.robust.autogen.sclsp_scenario.LotSizingScenario;
import dk.diku.robust.autogen.sclsp_scenario.ObjectFactory;
import dk.diku.robust.lsp.CLSPScenarioGenerator;
import dk.diku.robust.lsp.simulation.SimConclusion;
import dk.diku.robust.lsp.simulation.SimResult;
import dk.diku.robust.stat.ConfidenceInterval;

public class LSPSimulator {

	private static final int DECIMALS = 3;
	private static final int INITIAL_STORAGE_COST = 10000;
	private static final int MINIMUM_SIMS = 20;

	private Experiment exp;
	private SCLSPInstance instance;
	private int maxSimulations;
	private int numItems;
	private int numPeriods;
	private int genesPerItem;
	private ObjectFactory fact;
	private double desiredRobustness;


	public LSPSimulator(Experiment exp, SCLSPInstance instance) {
		super();
		this.exp = exp;
		this.instance = instance;
		this.numItems = instance.getNumItems();
		this.numPeriods = instance.getNumPeriods();
		this.maxSimulations = exp.getSimulation().getNumSimulations().intValue();
		this.genesPerItem = numPeriods + 1;
		this.fact = new ObjectFactory();
		this.desiredRobustness = exp.getDesiredRobustness();
	}

	public SimConclusion simulateLSP(LSPSolution solution) {

		int[] initialStockCopy = solution.initialStock;
		int[][] productionCopy = solution.production;
		//LSPSolverGenetic.printAll(chrom);
		// mash also the chromosome!
		//System.out.println();
		//LSPSolverGenetic.printAll(chrom);
		//System.out.println("-------");

		// Simulate until LB => desiredRobustness, UB < desiredRobustness, max sims reached or precision reached.
		double LB = 0.0d;
		double UB = 1.0d;
		int simsPerformed = 0;

		// local variables

		long sumFeasible = 0;
		long sumUsedCapacity= 0;
		long sumAvailableCapacity = 0;
		long sumCost = 0;
		long highestCost = 0;
		long lowestCost = Long.MAX_VALUE;
		long sumDemandNotMet = 0;
		long sumDemandMet = 0;
		long sumTotalDemand = 0;
		long sumUnrealizableProduction = 0;

		// Simulate until termination criteria met
		while(runMoreSims(LB, UB, simsPerformed)) {

			// Sample a scenario
			LotSizingScenario scenario = null;
			// TODO: Test hvor lang tid det tager at sample
			try {
				scenario = CLSPScenarioGenerator.makeScenario(instance, fact).getValue();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

			// Perform single simulation
			// TODO: Test hvor lang tid det tager at simulere
			SimResult result = simulateScenario(productionCopy, initialStockCopy, scenario);

			// Capture results of simulation
			sumFeasible += result.feasible? 1 : 0;
			sumUsedCapacity += result.usedCapacity;
			sumUnrealizableProduction += result.unrealizableProduction;
			sumAvailableCapacity += result.availableCapacity;
			sumCost += result.cost;
			highestCost = (long)Math.max(highestCost, result.cost);
			lowestCost = (long)Math.min(lowestCost, result.cost);
			sumDemandNotMet += result.demandMissed;
			sumDemandMet += result.demandMet;
			sumTotalDemand += result.demand;
			if(simsPerformed > MINIMUM_SIMS) {
				// calculate robustness
				ConfidenceInterval ci = new ConfidenceInterval((int)simsPerformed, (int)sumFeasible, DECIMALS);
				double[] interval = ci.getInterval(); 
				LB = interval[0];
				UB = interval[1];
			}
			simsPerformed++;
		}

		// Done simulating

		// Conclude on simulation
		SimConclusion conclusion = new SimConclusion();
		// robustness
		conclusion.robust = (LB >= exp.getDesiredRobustness());
		conclusion.robustnessLB = LB;
		conclusion.robustnessUB = UB;
		// unrealizable production
		conclusion.averageUnrealizableProduction = sumUnrealizableProduction / (double) simsPerformed;
		// demands met
		conclusion.averageDemandMet = sumDemandMet / (double) simsPerformed;
		conclusion.averageDemandMissed = sumDemandNotMet / (double) simsPerformed;
		conclusion.averageTotalDemand = sumTotalDemand / (double) simsPerformed;
		// cost
		conclusion.averageCost = ((double)sumCost) / (double)simsPerformed;
		conclusion.lowestCost = lowestCost;
		conclusion.highestCost = highestCost;
		// sims run
		conclusion.averageUsedCapacity = sumUsedCapacity / (double)simsPerformed;
		conclusion.averageAvailableCapacity = sumAvailableCapacity / (double)simsPerformed;
		conclusion.simulationsRun = simsPerformed;
		return conclusion;
	}

	private SimResult simulateScenario(int[][] productionCopy, int[] initialStockCopy, LotSizingScenario scenario) {

		LSPSample scen = new LSPSample(scenario);

		// Thing to count while simulating
		long totalCapUse = 0;
		long cost = 0; // productioncost, holdingcost, fixedcost
		long totalMissedDemand = 0; // if stock after production < demand, count it.
		long totalDemandMet = 0; // if stock after production < demand, count it.
		long totalUnrealizableProduction = 0;
		boolean feasible = true; // all demands met, all capacities respected.

		// Initialize the storage
		int[][] storage = new int[numItems][numPeriods]; // actual available in scenario
		// Begin producing
		for(int t=0; t<numPeriods; t++) {
			long capUseTimePeriod = 0; // capacity used in this time period, used later to indicate overall slack in capacity constraint.
			for(int i=0; i<numItems;i++) {

				// Simulate production of single item, current time period

				// Planned production
				int plannedProduction = productionCopy[i][t]; // Amount specified by the production plan.

				// Actual production
				int maxPossibleProduction = scen.capacity[t]/scen.capUsage[i][t];
				int actualProduction = Math.min(plannedProduction, maxPossibleProduction);
				totalUnrealizableProduction += Math.max(0, plannedProduction - actualProduction);

				// Storage after production
				if(t == 0) {
					// Produced + initial storage
					storage[i][t] = actualProduction + initialStockCopy[i];
					// Update cost: initial storage cost
					cost += initialStockCopy[i] * INITIAL_STORAGE_COST;
				}
				if(t > 0) {
					// Produced + last storage
					storage[i][t] = actualProduction + storage[i][t-1];
				}

				// Storage minus demand
				storage[i][t] -= scen.demands[i][t];

				if(storage[i][t] < 0 ) {
					// Negative storage? Demand not met
					long missedDemand = Math.abs(storage[i][t]);
					totalMissedDemand += missedDemand;
					totalDemandMet += scen.demands[i][t] - missedDemand;
				}
				else {
					// Non-negative storage? Demand met
					totalDemandMet += scen.demands[i][t];
				}
				// New storage 
				storage[i][t] = Math.max(0, storage[i][t]);

				// Production cost 
				cost += scen.productionCosts[i][t]*plannedProduction;
				// Fixed cost 
				cost += plannedProduction>0? scen.fixedCosts[i][t] : 0;
				// Holding cost
				cost += scen.holdingCosts[i][t]*storage[i][t];

				// Feasible? Planned production possible
				feasible = feasible && (plannedProduction <= maxPossibleProduction);
				capUseTimePeriod += actualProduction * scen.capUsage[i][t];
			}
			totalCapUse += capUseTimePeriod;
		}

		// Feasible? no missed demands
		feasible = feasible && (totalMissedDemand == 0);

		// Create result object
		SimResult result = new SimResult();
		result.cost = cost;
		result.demand = scen.totalDemand;
		result.demandMissed = totalMissedDemand;
		result.unrealizableProduction = totalUnrealizableProduction;
		result.demandMet = totalDemandMet;
		result.feasible = feasible;
		result.usedCapacity = totalCapUse;
		result.availableCapacity = scen.totalCapacity;
		return result;
	}

	public void uffesMashing(int[][] productionCopy, int[] initialStockCopy, IChromosome chrom) {
		// kopier kromosomet, fortolk generne, højeste produktion per tidsperiode fortolkes som eneste item der produceres i den periode
		for(int t=0; t<genesPerItem; t++) {
			int highestProd = -1;
			int mostProducedItem = -1;
			for(int i = 0; i<numItems; i++) {
				if(t == 0) {
					// copy orig stock
					initialStockCopy[i] = ((IntegerGene)chrom.getGene(i*genesPerItem)).intValue();
				}
				else {
					int production = ((IntegerGene)chrom.getGene(i*genesPerItem + t)).intValue();
					if(production > highestProd) {
						// reset formerly assumed most produced item
						if(mostProducedItem != -1) {
							productionCopy[mostProducedItem][t-1] = 0;
						}
						// save new values for highest production, and most produced item
						mostProducedItem = i;
						highestProd = production;
						productionCopy[i][t-1] = production;
					}
				}
			}
		}
	}

	public void permaMashing(int[][] productionCopy, int[] initialStockCopy, IChromosome chrom) {
		// kopier kromosomet, fortolk generne, højeste produktion per tidsperiode fortolkes som eneste item der produceres i den periode
		uffesMashing(productionCopy, initialStockCopy, chrom);
		// mash chromosomet
		for(int t=0; t<genesPerItem; t++) {
			for(int i = 0; i<numItems; i++) {
				if(t == 0) {
					// mash initial stock
					chrom.getGene(i*genesPerItem).setAllele(initialStockCopy[i]);
				}
				else {
					// mash production
					chrom.getGene(i*genesPerItem + t).setAllele(productionCopy[i][t-1]);
				}
			}
		}
	}

	private boolean runMoreSims(double LB, double UB, int simsPerformed) {
		// Run more simulations?
		boolean term1 = LB < desiredRobustness;
		boolean term2 = UB >= desiredRobustness;
		boolean term3 = simsPerformed < maxSimulations;
		return  term1 && term2 && term3;
	}
}
