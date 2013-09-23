package dk.diku.robust.stat;

import java.util.ArrayList;
import java.util.Collections;

import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;

public class StatMath {

	private static MersenneTwister twister = new MersenneTwister();
	private static int SIMULATIONS = 100000;

	/**
	 * @param mean of the normal distribution
	 * @param variance of the normal distribution
	 * @param percentile, this is the percentage of outcomes that must be greater than the value returned.
	 * @return The maximum value for which it is true that 'percentile' number of the results
	 *  are greater.
	 *  As an example if gLBN is called with a mean of 100, a variance of 20 
	 *  and a percentile of 97.5, then
	 *  gLBN will return a value around 80 since there will be a 97.5% chance that 
	 *  a number drawn from this distribution will be higher than 80.   
	 */
	public static double getLowerBoundNormal(double mean, double variance, double percentile){
		Normal normal = new Normal(mean, Math.sqrt(variance), twister);
		ArrayList<Double> randomNumbers = new ArrayList<Double>();
		for(int i=0;i<SIMULATIONS;i++){
			randomNumbers.add(normal.nextDouble());
		}
		Collections.sort(randomNumbers);
		int index = (int) ((1-percentile)*SIMULATIONS);
		return randomNumbers.get(index);
	}

}
