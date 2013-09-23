package dk.diku.robust.kp.exactsolver;

import dk.diku.robust.kp.model.KP;
import dk.diku.robust.kp.model.KPSolution;

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

public class KPSolverExact {
	
	public KPSolverExact(){
	}
	
	public KPSolution solve(KP problem){
        int[] profit = problem.getProfits();
        int[] weight = problem.getWeights();
        int N = problem.getItems();
        int W = problem.getCapacity();
        int[][] opt = new int[N+1][W+1];
        boolean[][] sol = new boolean[N+1][W+1];

        for (int n = 1; n <= N; n++) {
            for (int w = 1; w <= W; w++) {
                // don't take item n
                int option1 = opt[n-1][w];

                // take item n
                int option2 = Integer.MIN_VALUE;
                if (weight[n] <= w) option2 = profit[n] + opt[n-1][w-weight[n]];

                // select better of two options
                opt[n][w] = Math.max(option1, option2);
                sol[n][w] = (option2 > option1);
            }
        }

        // determine which items to take
        boolean[] take = new boolean[N+1];
        for (int n = N, w = W; n > 0; n--) {
            if (sol[n][w]) { take[n] = true;  w = w - weight[n]; }
            else           { take[n] = false;                    }
        }
        int selectedItems = 0;
        for(boolean i: take){
        	if(i) selectedItems++;
        }
        
        // Creating two arrays with the profits and weights of the 
        // items in the knapsack.
        /*
        int[] selectedProfits = new int[selectedItems];
        int[] selectedWeights = new int[selectedItems];
        int counter = 0;
        for(int i = 0; i<take.length; i++){
        	if(take[i]){
        		selectedProfits[counter] = profit[i];
        		selectedWeights[counter] = weight[i];
        		counter++;
        	}
        } 
        */       
        return new KPSolution(problem, take);
	}
}
