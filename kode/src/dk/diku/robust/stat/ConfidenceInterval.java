package dk.diku.robust.stat;

import java.text.NumberFormat;

/**
 * @author Uffe Christensen
 * Class to determine the least significant difference of a dataset.
 *
 */
public class ConfidenceInterval {
	private int numberOfExperiments;
	private int numberOfPositives;
	private int desiredPrecision;
	
	public ConfidenceInterval(int experiments, int positives, int precision){
		this.numberOfExperiments = experiments;
		this.numberOfPositives = positives;
		this.desiredPrecision = precision;
		
	}
	
	/**
	 * @param experiments Number of experiments
	 * @param positives Number of positives
	 * @param precision Number of decimals in the endpoints
	 * @return acceptance area
	 */
	public double[] getInterval(int experiments, int positives, int precision){
		double[] interval = {0.0, 0.0};
		double step = Math.pow(10.0,-precision*1.0);
		interval[0] = DetermineEndPoint((double) experiments, (double) positives, step, step);
		interval[1] = DetermineEndPoint((double) experiments, (double) positives, step, 1.0);
		return interval;
	}
	/**
	 * @return the array containing the maximum and minimum probabilities
	 * that are likely given the observations and the desired precision.
	 * interval[0] is the lowest value
	 * interval[1] is the highest value
	 */
	public double[] getInterval(){
		return getInterval(numberOfExperiments, numberOfPositives, desiredPrecision);
	}
		
	public String getPrintableInterval(){  
		NumberFormat myFormat = NumberFormat.getInstance();
		myFormat.setMaximumFractionDigits(desiredPrecision);
		double interval[] = getInterval();
		String MinimumProbability = myFormat.format(interval[0]);
		String MaximumProbability = myFormat.format(interval[1]);
		String test  = "Sandsynligheder i intervallet [ "
						+MinimumProbability +";"
						+MaximumProbability
						+"] kan ligge til grund for de angivne resultater";
		return test;
	}

	/**
	 * @param N Number of experiments
	 * @param X Number of positive outcomes
	 * @param PrecisionDecimal Precision of the percentages
	 * @param StartingEndPoint An endpoint of the interval, typically 0 or 1.
	 * @return
	 */
	public double DetermineEndPoint(double N, double X, double PrecisionDecimal, double StartingEndPoint)
	{
		double ApproximatedProbability = X/N;
		double LeftEndPoint = Math.min(ApproximatedProbability, StartingEndPoint);
		double RightEndPoint = Math.max(ApproximatedProbability, StartingEndPoint);
		double Center;
		boolean BinarySearchDone = false;
		boolean TempHypothesis;
		boolean Low = (StartingEndPoint < ApproximatedProbability);
		// If the StartingEndPoint is already a valid hypothesis, it is returned. 
		if(DetermineHypothesis(X, N, StartingEndPoint))
			return(StartingEndPoint);
		else
		{
			// When the binary search starts, the starting endpoint is a rejected hypothesis.
			while(!BinarySearchDone)
			{
				// The hypothesis to be tested is the middle point between a rejected
				// hypothesis and a non-rejected hypothesis.
				Center = (LeftEndPoint + RightEndPoint)/2.0;
				TempHypothesis = DetermineHypothesis(X,N,Center);
				// If the new hypothesis is not rejected and we are trying to
				// find a left endpoint, the new hypothesis is also the new riqht endpoint.
				// This will also happen if the new hypothesis is rejected and we are trying to
				// find a right endpoint.
				if((TempHypothesis && Low) || (!TempHypothesis && !Low))
                    RightEndPoint = Center;
				else
					LeftEndPoint = Center;
				BinarySearchDone = (((RightEndPoint-LeftEndPoint))<PrecisionDecimal);
			}
			if(Low)
                return(RightEndPoint);
			return(LeftEndPoint);
		}
	}
	

	/**
	 *  Determines whether a hypothesis about a binomialdistribution
	 *  can be confirmed.
	 * @param x The number of positive test results.
	 * @param n The number of experiments carried out.
	 * @param p0 The probability that is being tested.
	 * @return False if the hypothesis cannot be confirmed and otherwise true.
	 */
	public boolean DetermineHypothesis(double x, double n, double p0)
	{
		// The calculation of K is taken from Inge Heningsen: Statistik
		// p. 107.
		double K = Math.pow((x-n*p0),2.0)/(n*p0*(1.0-p0)); 
		// The MagicNumber is derived from Tue Tjur: Sandsynlighedsregning
		// p. 151 about chi^2 distribution with 1 degree of freedom.
		double MagicNumber = 3.841;
		return (K<MagicNumber);
	}
	

}
