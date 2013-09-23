package dk.diku.robust.core;

import java.util.List;

import dk.diku.robust.core.model.SolutionData;

public abstract class Solver {

	private int iteration;
	private int maxIteration;
	private int simulationsPerTest;

	public Solver() {
		super();
		iteration = 0;
	}

	public synchronized void step() {
		doIteration();
		iteration++;
	}

	public synchronized int getIteration() {
		return iteration;
	}

	public synchronized int getMaxIteration() {
		return maxIteration;
	}

	public synchronized void setMaxIteration(int max){
		maxIteration = max;
	}

	public int getSimulationsPerTest() {
		return simulationsPerTest;
	}

	public void setSimulationsPerTest(int simulationsPerTest) {
		this.simulationsPerTest = simulationsPerTest;
	}

	public abstract void doIteration();

	public abstract List<SolutionData> getSolutions();

	public abstract SolutionData getCurrentBest();

	public abstract SolutionData getGlobalBest();
	
	public abstract void finalTask();

}
