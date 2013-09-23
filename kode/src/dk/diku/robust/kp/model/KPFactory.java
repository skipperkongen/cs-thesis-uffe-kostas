package dk.diku.robust.kp.model;

import java.util.Date;

import cern.jet.random.Gamma;
import cern.jet.random.Normal;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import dk.diku.robust.autogen.stochknapsack.DistributionType;
import dk.diku.robust.autogen.stochknapsack.GammaDistribution;
import dk.diku.robust.autogen.stochknapsack.IncidentType;
import dk.diku.robust.autogen.stochknapsack.ItemType;
import dk.diku.robust.autogen.stochknapsack.NormalDistribution;
import dk.diku.robust.autogen.stochknapsack.PolynomialDistribution;
import dk.diku.robust.autogen.stochknapsack.StochasticKnapsackProblem;
import dk.diku.robust.autogen.stochknapsack.UniformDistribution;
import dk.diku.robust.util.GiantUnmarshaller;

public class KPFactory {

	private static KPFactory instance = null;
	private Normal normal;
	private Gamma gamma;
	private Uniform uniform;

	private KPFactory() {
		Date seed = new Date(System.currentTimeMillis());
		MersenneTwister mt = new MersenneTwister(seed);
		normal = new Normal(0,1,mt);
		gamma = new Gamma(3,3,mt);
		uniform = new Uniform(0,1,mt);
	}

	public static KPFactory getInstance() {
		if(instance == null) {
			instance = new KPFactory();
		}
		return instance;
	}

	public StochasticKnapsackProblem readKnapsackProblem(String pathToXML) throws Exception {
		return GiantUnmarshaller.xmlToStochasticKnapsackProblem(pathToXML);
	}

	public KP sampleScenario(StochasticKnapsackProblem skp) throws Exception {

		// SET CAPACITY
		int capacity = 0;
		if(skp.getCapacity() != null) {
			// round constant (float) capacity;
			capacity = Math.round(skp.getCapacity().floatValue());
		}
		else {
			// sample the distribution and round 
			DistributionType distr = skp.getStochasticCapacity();
			int stocCap = (int) Math.round(sampleDistribution(distr));
			stocCap = Math.max(0, stocCap);
			capacity = stocCap;
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
				int stocProf = (int) Math.round(sampleDistribution(it.getStochasticProfit()));
				stocProf = Math.max(0, stocProf);
				profits[index] = stocProf;
			}
			// set weight
			if(it.getWeight() != null) {
				weights[index] = Math.round(it.getWeight());
			}
			else {
				int stocWeight = (int) Math.round(sampleDistribution(it.getStochasticWeight()));
				stocWeight = Math.max(0, stocWeight);
				weights[index] = stocWeight;
			}
			// increase index into weights[] and profits[]
			index++;
		}

		KP kp = new KP(profits.length, capacity, profits, weights);
/*		kp.setCapacity(capacity);
		kp.setItems(profits.length);
		kp.setProfits(profits);
		kp.setWeights(weights);*/

		// RETURN PROBLEM
		return kp;
	}
	
	public KP getDeterministicScenario(StochasticKnapsackProblem skp) throws Exception {

		// SET CAPACITY
		int capacity = 0;
		if(skp.getCapacity() != null) {
			// round constant (float) capacity;
			capacity = Math.round(skp.getCapacity().floatValue());
		}
		else {
			// get the mean value of the capacity if it is a normal distribution 
			DistributionType distr = skp.getStochasticCapacity();
			if(distr.getNormal()==null){
				System.err.println("Capacity must have a normal distribution.");
			}
			else{
				capacity = (int) distr.getNormal().getMean();
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
				DistributionType distr = it.getStochasticProfit();
				if(distr.getNormal()==null){
					System.err.println("Profit must have a normal distribution.");
				}
				else{
					profits[index] = (int) distr.getNormal().getMean();
					
				}
			}
			// set weight
			if(it.getWeight() != null) {
				weights[index] = Math.round(it.getWeight());
			}
			else {
				DistributionType distr = it.getStochasticWeight();
				if(distr.getNormal()==null){
					System.err.println("Weight must have a normal distribution.");
				}
				else{
					weights[index] = (int) distr.getNormal().getMean();
					
				}
			}
			// increase index into weights[] and profits[]
			index++;
		}

		KP kp = new KP(profits.length, capacity, profits, weights);
		// RETURN PROBLEM
		return kp;
	}

	private double sampleDistribution(DistributionType distr) {
		if(distr.getNormal() != null) {
			NormalDistribution nDistr = distr.getNormal();
			return normal.nextDouble(nDistr.getMean(), Math.sqrt(nDistr.getVariance()));
		}
		else if(distr.getGamma() != null) {
			GammaDistribution gDistr = distr.getGamma();
			return gamma.nextDouble(gDistr.getShape(), gDistr.getShape());
		}
		else if(distr.getUniform() != null) {
			UniformDistribution uDistr = distr.getUniform();
			return uniform.nextDoubleFromTo(uDistr.getLower(), uDistr.getUpper());

		}
		else if(distr.getPolynomial() != null) {
			return samplePolynomial(distr.getPolynomial());
		}
		return 0.0f;
	}

	private double samplePolynomial(PolynomialDistribution pDistr) {
		double result = 0.0;
		float dice = uniform.nextFloatFromTo(0, 1);
		float sum = 0.0f;
		for(IncidentType it: pDistr.getIncident()) {
			sum += it.getLikelyhood();
			if(dice <= sum) {
				result = it.getValue();
			}
		}
		return result;
	}

}
