package dk.diku.robust.view;

import java.util.Observable;
import java.util.Observer;

import dk.diku.robust.core.Solver;
import dk.diku.robust.core.SolverRunner;

public class SolutionPrinter implements Observer {

	//@Override
	public void update(Observable o, Object arg) {
		if(o instanceof SolverRunner) {
			SolverRunner runner = (SolverRunner) o;
			Solver solver = runner.getSolver();
			System.out.print("Best solution: " + solver.getGlobalBest().objectiveValue + "(obj),");
			System.out.print(solver.getGlobalBest().robustnessHigh+ "(robustness - high),");
			System.out.print(solver.getGlobalBest().robustnessLow+ "(robustness - low),");
			System.out.println(solver.getGlobalBest().fitnessValue+"(fitness value)");
		}
	}

}
