package dk.diku.robust.kp;


import dk.diku.robust.autogen.stochknapsack.DistributionType;
import dk.diku.robust.autogen.stochknapsack.ItemType;
import dk.diku.robust.autogen.stochknapsack.NormalDistribution;
import dk.diku.robust.autogen.stochknapsack.StochasticKnapsackProblem;
import dk.diku.robust.kp.exactsolver.KPSolverExact;
import dk.diku.robust.kp.model.KP;
import dk.diku.robust.kp.model.KPFactory;
import dk.diku.robust.kp.model.KPSolution;
import dk.diku.robust.stat.ConfidenceInterval;
import dk.diku.robust.stat.StatMath;

public class SKPCalculator {


	private static final int DECIMALS = 3;

	private SKPCalculator(){

	}

	public static double[] calculateRobustness(StochasticKnapsackProblem skp, KPSolution sol, int numSimulations) throws Exception{
		int feasible = 0;
		int weight;
		KP problem;

		for(int i = 0; i< numSimulations;i++){
			weight = 0;
			problem = KPFactory.getInstance().sampleScenario(skp);
			boolean[] b = sol.getItemSelected();
			for(int j=0; j<b.length;j++) {
				if(b[j]){
					weight += problem.getWeights()[j];
				}
			}
			if(problem.getCapacity() >= weight){
				feasible++;
			}
		}
		ConfidenceInterval ci = new ConfidenceInterval(numSimulations, feasible, DECIMALS);
		double[] result = new double[3];
		result[0] = ci.getInterval()[0];
		result[1] = ci.getInterval()[1];
		return result;
	}

	public static KPSolution findFatSolution(StochasticKnapsackProblem skp, double robustness){
		// Create KP to be solved.

		// SET CAPACITY
		int capacity = 0;
		if(skp.getCapacity() != null) {
			// round constant (float) capacity;
			capacity = Math.round(skp.getCapacity().floatValue());
		}
		else {
			// get the fat capacity. 
			DistributionType distr = skp.getStochasticCapacity();
			if(distr.getNormal()==null){
				System.err.println("Capacity must have a normal distribution.");
			}
			else{
				NormalDistribution normal = distr.getNormal();
				double fatCap = StatMath.getLowerBoundNormal(normal.getMean(), normal.getVariance(), robustness);
				capacity = (int) fatCap;
			}
		}


		// SET ITEMS
		int numItems = skp.getItems().getItem().size();
		int[] profits = new int[numItems];
		int[] weights = new int[numItems];
		// create all items
		int index = 0;
		for(ItemType it : skp.getItems().getItem()) {
			// set profit
			if(it.getProfit() != null) {
				profits[index] = (int)Math.round(it.getProfit());
			}
			else {
				DistributionType profitDistr = it.getStochasticProfit();
				if(profitDistr.getNormal()==null){
					System.err.println("Profit must have a normal distribution.");
				}
				else{
					NormalDistribution normal = profitDistr.getNormal();
					double fatProfit = StatMath.getLowerBoundNormal(normal.getMean(), normal.getVariance(), robustness);
					profits[index] = (int) Math.floor(fatProfit);
					
				}
			}
			// set weight
			if(it.getWeight() != null) {
				weights[index] = Math.round(it.getWeight());
			}
			else {
				DistributionType weightDistr = it.getStochasticWeight();
				if(weightDistr.getNormal()==null){
					System.err.println("Weight must have a normal distribution.");
				}
				else{
					NormalDistribution weightNormal = weightDistr.getNormal();
					double fatWeight = StatMath.getLowerBoundNormal(weightNormal.getMean(), weightNormal.getVariance(), 1-robustness);
					weights[index] = (int) Math.floor(fatWeight);
					
				}
			}
			// increase index into weights[] and profits[]
			index++;
		}

		KP kp = new KP(profits.length, capacity, profits, weights);
		// Solving the problem and returning the solution
		KPSolverExact kpSolver = new KPSolverExact();
		return kpSolver.solve(kp);
	}

}
