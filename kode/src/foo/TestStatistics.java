package foo;

import dk.diku.robust.stat.ConfidenceInterval;
import dk.diku.robust.stat.StatMath;

public class TestStatistics {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ConfidenceInterval ci = new ConfidenceInterval(100,36,2);
		for(int i = 0;i<11;i++){
			System.out.println("i = "+i+": "+ci.DetermineHypothesis(i, 10, 0.5));
		}
		System.out.println(ci.getPrintableInterval());
		double[] interval;
		double size;

		for(int i = 1; i <101; i++){
			int j = (int) Math.floor(i*0.8);
			interval = ci.getInterval(i,j,3);
			System.out.println("Interval: ["+interval[0] + "," + interval[1] + "]");
		}
		/*
		interval = ci.getInterval(1000,990,3);
		size = interval[1]-interval[0];
		System.out.println("Interval size: "+size);
		 */		

		//System.out.println(StatMath.getLowerBoundNormal(100, 100, 0.95));

	}

}
