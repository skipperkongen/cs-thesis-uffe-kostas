package foo;

import cern.jet.random.Normal;
import dk.diku.robust.autogen.stochknapsack.*;
import dk.diku.robust.kp.exactsolver.KPSolverExact;
import dk.diku.robust.kp.model.*;

/*************************************************************************
 *  Compilation:  javac Knapsack.java
 *  Execution:    java Knapsack N P W
 *
 *  Generates an instance of the 0/1 knapsack problem with N items,
 *  profits between 0 and P-1, weights between 0 and W-1,
 *  and solves it in O(NW) using dynamic programming.
 *
 *  %  java Knapsack 6 1000 2000
 *  item    profit  weight  take
 *  1       874     580     true
 *  2       620     1616    false
 *  3       345     1906    false
 *  4       369     1942    false
 *  5       360     50      true
 *  6       470     294     true
 *
 *************************************************************************/

public class TestKnapsackSolver {
    private static Normal normal;
	
	private static String xml = "xml/non_stochastic_knapsack.xml";

    public static void main(String[] args) throws Exception{
    	KPFactory factory = KPFactory.getInstance();
    	StochasticKnapsackProblem skp = factory.readKnapsackProblem(xml);
    	KP tmp = factory.sampleScenario(skp);

        System.out.println("Original problem");        
        System.out.println("profit" + "\t" + "weight" + "\t"+ "p/w");
        int[] origProfit = tmp.getProfits();
        int[] origWeight = tmp.getWeights();
        for (int n = 1; n < origProfit.length; n++){
        	double p_w = ((double) origProfit[n])/origWeight[n];
            System.out.println(origProfit[n] + "\t" + origWeight[n] + "\t" + p_w);
        }
        KPSolverExact solver = new KPSolverExact();
        KPSolution solution = solver.solve(tmp);
        
        System.out.println("\nSolution");
        System.out.println("profit" + "\t" + "weight" + "\t"+ "p/w");
        for (int n = 0; n < origProfit.length; n++){
        	if(solution.getItemSelected()[n]){
        		double p_w = ((double) origProfit[n])/origWeight[n];
        		System.out.println(origProfit[n] + "\t" + origWeight[n] + "\t" + p_w);
        	}
        }
        System.out.println("Total profit: " + solution.getTotalProfit());
        System.out.println("Total weight used: " + solution.getUsedCapacity());
        
    /*    int[] numbers = countFrequency();
        for(int i = 0; i<numbers.length;i++){
        	System.out.println(i+"\t: " + numbers[i]);
        }*/
 

    }
/*    
    public static double getNumber(){
    	double result = normal.nextDouble(20.0, 5.0); 
        return result; 
    }
    
    public static int[] countFrequency(){
        int[] testArray = new int[40];
        for(int i = 0;i<1000;i++){
        	testArray[(int) Math.round(getNumber())]++;
        }
        return testArray;
    }
    */
}
