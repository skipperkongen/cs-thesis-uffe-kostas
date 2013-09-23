package dk.diku.robust.view;

import java.util.Observable;

import dk.diku.robust.core.Solver;
import dk.diku.robust.core.SolverRunner;
import dk.diku.robust.core.model.SolutionData;

public class BestSolutionTrackerPlot extends AbsMultiDoublePlot {

	public BestSolutionTrackerPlot() {
		super(true);
		setChartTitle("Stats for best solution");
		setXLabel("Generation");
		setYLabel("Value");
	}

	@Override
	public void update(Observable observable, Object obj) {
		if(observable instanceof SolverRunner) {
			SolverRunner runner = (SolverRunner) observable;
			Solver solver = runner.getSolver();
			// plot robustness, just for kicks
			int x = solver.getIteration();
			SolutionData solution = solver.getGlobalBest();
			double objVal = solution.objectiveValue;
			addPoint("Objective value", x, objVal);
			double robustnessLow = solution.robustnessLow;
			addPoint("Robustness LB", x, robustnessLow);
			double robustnessHigh = solution.robustnessHigh;
			addPoint("Robustness UB", x, robustnessHigh);
			
		}

	}

}
