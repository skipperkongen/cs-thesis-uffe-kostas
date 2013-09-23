package dk.diku.robust.core;

import java.util.Observable;


public class SolverRunner extends Observable implements Runnable {
	
	private Solver solver;
	private boolean done;

	public SolverRunner(Solver solver) {
		super();
		this.solver = solver;
	}

	//@Override
	public void run() {
		done = false;
		long start, end;
		while(solver.getIteration() < solver.getMaxIteration()) {
			/*
			System.out.println("Stepping solver");
			start = System.currentTimeMillis();
			*/
			solver.step();
			/*
			end = System.currentTimeMillis();
			System.out.println("- Completed in: " + (end-start) + " ms");
			*/
			setChanged();
			notifyObservers();
		}
		solver.finalTask();
		done = true;
		setChanged();
		notifyObservers();
	}
	
	public Solver getSolver() {
		return solver;
	}

	public boolean isDone() {
		return done;
	}
}
