package dk.diku.robust.lsp.simulation;

import dk.diku.robust.autogen.sclsp_experiment.Experiment;
import dk.diku.robust.autogen.sclsp_instance.SCLSPInstance;
import dk.diku.robust.autogen.sclsp_scenario.LotSizingScenario;
import dk.diku.robust.autogen.sclsp_scenario.ObjectFactory;
import dk.diku.robust.lsp.CLSPScenarioGenerator;
import dk.diku.robust.lsp.model.LSPSolutionPOJO;
import dk.diku.robust.stat.ConfidenceInterval;

public class Simulator {

	private static final int DECIMALS = 3;
	private static final int INITIAL_STORAGE_COST = 10000;
	private static final int MINIMUM_SIMS = 20;

	private Experiment exp;
	private SCLSPInstance instance;
	private int maxSimulations;
	private int numItems;
	private int numPeriods;
	private ObjectFactory fact;
	private double desiredRobustness;


	public Simulator(Experiment exp, SCLSPInstance instance) {
		super();
		this.exp = exp;
		this.instance = instance;
		this.numItems = instance.getNumItems();
		this.numPeriods = instance.getNumPeriods();
		this.maxSimulations = exp.getSimulation().getNumSimulations().intValue();
		this.fact = new ObjectFactory();
		this.desiredRobustness = exp.getDesiredRobustness();
	}

	/**
	 * @param chrom
	 * @param precision The desired precision of robustness estimate, 
	 * that is the span in percentage between lower bound and upper bound on robustness.
	 * @return
	 * @throws Exception
	 */
	public SimConclusion simulate(LSPSolutionPOJO pojo, double precision) throws Exception {
		return rethunkSimulate(pojo, true, precision);
	}

	/**
	 * @param chrom
	 * @return
	 * @throws Exception
	 */
	public SimConclusion simulate(LSPSolutionPOJO pojo) throws Exception {
		return rethunkSimulate(pojo, false, 0.0);
	}

	private SimConclusion rethunkSimulate(LSPSolutionPOJO pojo, boolean usingPrecision, double precision) throws Exception {

		long startTime = System.currentTimeMillis();
		
		// Simulate until LB => desiredRobustness, UB < desiredRobustness, max sims reached or precision reached.
		double LB = 0.0d;
		double UB = 1.0d;
		int simsPerformed = 0;

		// local variables
		Tally tally = new Tally();

		// Simulate until termination criteria met
		while(runMoreSims(LB, UB, simsPerformed, usingPrecision, precision)) {

			// Sample a scenario
			LotSizingScenario scenario = null;
			// TODO: Test hvor lang tid det tager at sample
			scenario = CLSPScenarioGenerator.makeScenario(instance, fact).getValue();

			// Perform single simulation
			// TODO: Test hvor lang tid det tager at simulere
			SimResult result = simulateScenario(pojo, scenario);

			// Capture results of simulation
			if(result.feasible) {
				tally.sumFeasible++;				
				tally.sumCost += result.cost;
				tally.sumInitialCost += result.initialCost;
				tally.highestCost = (long)Math.max(tally.highestCost, result.cost);
				tally.lowestCost = (long)Math.min(tally.lowestCost, result.cost);
			}
			else {
				tally.sumUnrealizableProduction += result.unrealizableProduction;
				tally.highestDemandMissed = (long)Math.max(tally.highestDemandMissed, result.demandMissed);
				tally.sumDemandMissed += result.demandMissed;
			}
			tally.sumUsedCapacity += result.usedCapacity;
			tally.sumAvailableCapacity += result.availableCapacity;
			tally.sumDemandMet += result.demandMet;
			tally.sumTotalDemand += result.demand;
			simsPerformed++;
			if(simsPerformed >= MINIMUM_SIMS) {
				// Update robustness bounds
				ConfidenceInterval ci = new ConfidenceInterval((int)simsPerformed, (int)tally.sumFeasible, DECIMALS);
				double[] interval = ci.getInterval(); 
				LB = interval[0];
				UB = interval[1];
			}
		}

		// Done simulating

		// Conclude on simulation
		SimConclusion conclusion = new SimConclusion();
		
		double feasibleSims = tally.sumFeasible;
		double infeasibleSims = simsPerformed-tally.sumFeasible;

		// BOTH FEASIBLE AND NON-FEASIBLE
		conclusion.robust = (LB >= exp.getDesiredRobustness());
		conclusion.robustnessLB = LB;
		conclusion.robustnessUB = UB;
		conclusion.averageDemandMet = tally.sumDemandMet / (double) simsPerformed;
		conclusion.averageTotalDemand = tally.sumTotalDemand / (double) simsPerformed;		
		conclusion.averageUsedCapacity = tally.sumUsedCapacity / (double)simsPerformed;
		conclusion.averageAvailableCapacity = tally.sumAvailableCapacity / (double)simsPerformed;
		conclusion.simulationsRun = simsPerformed;
		conclusion.runningTime = System.currentTimeMillis() - startTime;

		// ONLY FEASIBLE
		conclusion.averageCost = tally.sumCost / feasibleSims;
		conclusion.averageInitialCost = tally.sumInitialCost / feasibleSims;
		conclusion.lowestCost = tally.lowestCost;
		conclusion.highestCost = tally.highestCost;

		// ONLY NON-FEASIBLE
		conclusion.averageUnrealizableProduction = tally.sumUnrealizableProduction / infeasibleSims;
		conclusion.averageDemandMissed = tally.sumDemandMissed / infeasibleSims;
		conclusion.highestDemandMissed = tally.highestDemandMissed;

		return conclusion;
	}

	private SimResult simulateScenario(LSPSolutionPOJO pojo, LotSizingScenario scenario) {

		LSPScenarioPOJO scen = new LSPScenarioPOJO(scenario);

		// Thing to count while simulating
		long capUsedScenario = 0;
		long totalInitialCost = 0;
		long costInScenario = 0; // productioncost, holdingcost, fixedcost
		long demandMissedInScenario = 0; // if stock after production < demand, count it.
		long demandMetInScenario = 0; // if stock after production < demand, count it.
		long unrealizedProductionInScenario = 0;
		boolean feasibleInScenario = true; // all demands met, all capacities respected.

		// Initialize the storage
		int[][] storage = new int[numItems][numPeriods]; // actual available in scenario
		// Begin producing
		for(int t=0; t<numPeriods; t++) {
			long capUseTimePeriod = 0; // capacity used in this time period, used later to indicate overall slack in capacity constraint.
			for(int i=0; i<numItems;i++) {

				// Simulate production of single item, current time period

				//////////////////////////////////
				// PRODUCTION
				//////////////////////////////////

				// Planned production
				int plannedProduction = pojo.production[i][t]; // Amount specified by the production plan.

				// Actual production
				int capUseSetup = (pojo.beingSetup[i][t]?scen.capSetup[i][t]:0);
				int startCapacity = scen.capacity[t];
				int availableCapacity = startCapacity - capUseSetup;
				int maxPossibleProduction = availableCapacity / scen.capUsage[i][t];
				int actualProduction = Math.min(plannedProduction, maxPossibleProduction);
				int unrealizable = Math.abs(Math.min(0, maxPossibleProduction - plannedProduction));
				unrealizedProductionInScenario += unrealizable;

				//////////////////////////////////
				// UPDATE STORAGE
				//////////////////////////////////

				if(t == 0) {
					// Produced + initial storage
					storage[i][t] = actualProduction + pojo.initialStorage[i];
				}
				else {
					// Produced + last storage
					storage[i][t] = actualProduction + storage[i][t-1];
				}

				//////////////////////////////////
				// SUBTRACT DEMAND
				//////////////////////////////////
				storage[i][t] -= scen.demand[i][t];

				//////////////////////////////////
				// ADD COST
				//////////////////////////////////

				// Production cost
				costInScenario += plannedProduction*scen.productionCosts[i][t];
				
				// Storage cost
				if(t == 0) {
					int initialCost = pojo.initialStorage[i] * INITIAL_STORAGE_COST;
					costInScenario += initialCost;
					totalInitialCost += initialCost;
				}
				costInScenario += storage[i][t] * scen.holdingCosts[i][t];
				
				// Fixed cost 
				costInScenario += plannedProduction>0? scen.fixedCosts[i][t] : 0;

				//////////////////////////////////
				// FEASIBLE?
				//////////////////////////////////

				// Feasible? Planned production possible?
				boolean productionAchieved = (plannedProduction <= maxPossibleProduction);
				boolean storageNonNeg = (storage[i][t] >= 0);
				feasibleInScenario = feasibleInScenario && productionAchieved;
				feasibleInScenario = feasibleInScenario && storageNonNeg;
				if(!productionAchieved || !storageNonNeg) {
					@SuppressWarnings("unused")
					int hulubulu;
					hulubulu = 0;
				}
				
				
				long missedDemand = Math.abs(Math.min(0, storage[i][t]));
				demandMissedInScenario += missedDemand;
				demandMetInScenario += scen.demand[i][t] - missedDemand;

				// New storage 
				storage[i][t] = Math.max(0, storage[i][t]);
				
				// Update capacity usage
				capUseTimePeriod += capUseSetup;
				capUseTimePeriod += actualProduction * scen.capUsage[i][t];
			}
			capUsedScenario += capUseTimePeriod;
		}

		// Create result object
		SimResult result = new SimResult();
		result.cost = costInScenario;
		result.initialCost = totalInitialCost;
		result.demand = scen.totalDemand;
		result.demandMissed = demandMissedInScenario;
		result.unrealizableProduction = unrealizedProductionInScenario;
		result.demandMet = demandMetInScenario;
		result.feasible = feasibleInScenario;
		result.usedCapacity = capUsedScenario;
		result.availableCapacity = scen.totalCapacity;
		return result;
	}

	private boolean runMoreSims(double LB, double UB, int simsPerformed, boolean usingPrecision, double precision) {
		// Run more simulations?
		if(usingPrecision) {
			// LB -> UB gap still too big?
			double diff = Math.abs(UB-LB);
			boolean term1 = diff > precision;
			boolean term2 = simsPerformed < maxSimulations;
			return term1 && term2;
		}
		else {
			// LB still below robustness, and UB still above robustness, and simsPerformed less than max
			boolean term1 = LB < desiredRobustness;
			boolean term2 = UB >= desiredRobustness;
			boolean term3 = simsPerformed < maxSimulations;
			return  term1 && term2 && term3;
		}
	}

	class Tally {

		long sumFeasible = 0;
		long sumUsedCapacity= 0;
		long sumAvailableCapacity = 0;
		long sumCost = 0;
		long sumInitialCost = 0;
		long highestCost = 0;
		long lowestCost = Long.MAX_VALUE;
		long sumDemandMissed = 0;
		long highestDemandMissed = 0;
		long sumDemandMet = 0;
		long sumTotalDemand = 0;
		long sumUnrealizableProduction = 0;
	}
}
