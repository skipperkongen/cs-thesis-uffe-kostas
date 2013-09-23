package dk.diku.robust.view;

import java.util.Observable;
import java.util.Observer;

import dk.diku.robust.core.Solver;
import dk.diku.robust.core.SolverRunner;

public class IterationObserver implements Observer {
	
	private long lastObservation = System.currentTimeMillis();

	//@Override
	public void update(Observable o, Object arg) {
		if(o instanceof SolverRunner) {
			SolverRunner runner = (SolverRunner) o;
			Solver solver = runner.getSolver();
			long now = System.currentTimeMillis();
			System.out.println("Iteration: " + solver.getIteration() + " took " + (now - lastObservation) + " ms");
			lastObservation = now;
		}
	}

}
