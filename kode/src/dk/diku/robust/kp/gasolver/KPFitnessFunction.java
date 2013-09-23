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

import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.impl.BooleanGene;

import dk.diku.robust.autogen.knapsackexperiment.KnapsackExperiment;
import dk.diku.robust.autogen.stochknapsack.StochasticKnapsackProblem;
import dk.diku.robust.core.model.SolutionData;
import dk.diku.robust.kp.model.KP;
import dk.diku.robust.kp.model.KPFactory;
import dk.diku.robust.stat.ConfidenceInterval;

/**
 * Fitness function for the knapsack example.
 *
 * @author Klaus Meffert
 * @since 2.3
 */
public class KPFitnessFunction extends FitnessFunction {

	private static final long serialVersionUID = 1L;
	private static final int DECIMALS = 2;
	
	private static final int ROBUSTNESS_LOW = 0;
	private static final int ROBUSTNESS_HIGH = 1;
	private static final int AVG_PROFIT = 2;
	private KPFactory factory;
	private StochasticKnapsackProblem skp;
	private int numSimulations;
	private double desiredRobustness;

	public KPFitnessFunction() {
		super();
	}

	public KPFitnessFunction(StochasticKnapsackProblem autogenSKP, KnapsackExperiment experiment) {
		factory = KPFactory.getInstance();
		this.skp = autogenSKP;
		this.numSimulations = experiment.getSimulation().getNumSimulations().intValue();
		this.desiredRobustness = experiment.getDesiredRobustness();
	}

	/**
	 * Determine the fitness of the given Chromosome instance. The higher the
	 * return value, the more fit the instance. This method should always
	 * return the same fitness value for two equivalent Chromosome instances.
	 *
	 * @param a_subject the Chromosome instance to evaluate
	 * @return a positive double reflecting the fitness rating of the given
	 * Chromosome
	 *
	 * @author Klaus Meffert
	 * @since 2.3
	 */
	public double evaluate(IChromosome a_subject){
		double result = 0;

		try{
			// calculate avg profit and robustness
			double[] vars = calcAverageProfitAndRobustnessAlternative(a_subject);
			double robustnessLow = vars[ROBUSTNESS_LOW];
			double robustnessHigh = vars[ROBUSTNESS_HIGH];
			double avgProfit = robustnessLow < desiredRobustness ? 0.0 : vars[AVG_PROFIT];
			
			
			//result = getFitnessSum(robustnessLow, avgProfit);
			result = getFitnessSumSquared(robustnessLow, avgProfit);
			//result = getFitnessProduct(robustnessLow, avgProfit);

			// set data
			SolutionData data = new SolutionData(robustnessLow, robustnessHigh, avgProfit);
			data.fitnessValue = result; 
			a_subject.setApplicationData(data);

			
		}
		catch(Exception e){
			e.printStackTrace();
			return -1.0;
		}
		return result;

	}
	
	public double[] calcAverageProfitAndRobustness(IChromosome a_subject) throws Exception {
		int feasible = 0;
		Gene[] genes = a_subject.getGenes();

		int profit;
		int weight;
		int accumulatedProfit = 0;
		KP problem;

		for(int i = 0; i< numSimulations;i++){
			profit = 0;
			weight = 0;
			problem = factory.sampleScenario(skp);
			for(int j=0; j<genes.length;j++) {
				BooleanGene b = (BooleanGene) genes[j];
				if(b.booleanValue()){
					profit += problem.getProfits()[j+1];
					weight += problem.getWeights()[j+1];
				}
			}
			if(problem.getCapacity() >= weight){
				feasible++;
				accumulatedProfit += profit;
			}
		}
		double avgProfit;
		if(feasible == 0){
			avgProfit = 0.0;
		}
		else{
			avgProfit = accumulatedProfit/feasible;
		}
		ConfidenceInterval ci = new ConfidenceInterval(numSimulations, feasible, DECIMALS);
		double[] result = new double[3];
		result[ROBUSTNESS_LOW] = ci.getInterval()[0];
		result[ROBUSTNESS_HIGH] = ci.getInterval()[1];
		result[AVG_PROFIT] = avgProfit;
		return result;
	}
	
	public double[] calcAverageProfitAndRobustnessAlternative(IChromosome a_subject) throws Exception {
		int feasible = 0;
		Gene[] genes = a_subject.getGenes();

		int profit;
		int weight;
		int accumulatedProfit = 0;
		KP problem;
		int i = 0;
		ConfidenceInterval ci = new ConfidenceInterval(numSimulations, feasible, DECIMALS);
		double[] acceptanceArea = {0.0,0.0};
		boolean canTerminate = false;
		while(i< numSimulations && !canTerminate){
			profit = 0;
			weight = 0;
			problem = factory.sampleScenario(skp);
			for(int j=0; j<genes.length;j++) {
				BooleanGene b = (BooleanGene) genes[j];
				if(b.booleanValue()){
					profit += problem.getProfits()[j+1];
					weight += problem.getWeights()[j+1];
				}
			}
			if(problem.getCapacity() >= weight){
				feasible++;
				accumulatedProfit += profit;
			}
			i++;
			acceptanceArea = ci.getInterval(i, feasible, DECIMALS);
			// If we already have an acceptanceArea that is above desired robustness
			// or if we have an acceptanceArea that is below desired robustness
			if((acceptanceArea[0]>desiredRobustness || acceptanceArea[1]<desiredRobustness) && i >15){
				canTerminate = true;
			}
		}
		System.out.println("NumSim = "+i);
		double avgProfit;
		if(feasible == 0){
			avgProfit = 0.0;
		}
		else{
			avgProfit = accumulatedProfit/feasible;
		}
		double[] result = new double[3];
		result[ROBUSTNESS_LOW] = acceptanceArea[0];
		result[ROBUSTNESS_HIGH] = acceptanceArea[1];
		result[AVG_PROFIT] = avgProfit;
		return result;
	}
	
	private double getFitnessProduct(double robustness, double avgProfit) throws Exception{
		double c1 = avgProfit;
		double c2 = (1.0 - Math.abs(desiredRobustness - robustness)) * avgProfit;
		double c3 = c2 * (robustness>=desiredRobustness?1:0);
		return c1 * c2 * c3;
	}
	
	private double getFitnessSum(double robustness, double avgProfit) throws Exception{
		double c1 = avgProfit;
		double c2 = c1 * (robustness>=desiredRobustness?1:0);
		return c1+c2;
	}
	
	private double getFitnessSumSquared(double robustness, double avgProfit) throws Exception{
		double c1 = avgProfit;
		double c2 = c1 * (robustness>=desiredRobustness?1:0);
		return Math.pow(c1+c2,2.0);
	}
	
}

