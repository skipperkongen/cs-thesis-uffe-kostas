package dk.diku.robust.view;

import java.util.List;
import java.util.Observable;

import dk.diku.robust.core.Solver;
import dk.diku.robust.core.SolverRunner;
import dk.diku.robust.core.model.SolutionData;

public class AverageRobustnessPlot extends AbsMultiDoublePlot {
	
	public AverageRobustnessPlot() {
		super(true);
		setChartTitle("Average robustness");
		setXLabel("Generation");
		setYLabel("Average robustness");
	}

	@Override
	public void update(Observable observable, Object obj) {
		if(observable instanceof SolverRunner) {
			SolverRunner runner = (SolverRunner) observable;
			if(runner.isDone()) {
				// do something
			}
			Solver solver = runner.getSolver();
			int x = solver.getIteration();
			List<SolutionData> solutions = solver.getSolutions();
			double robustnessSum = 0;
			for(SolutionData sol : solutions) {
				robustnessSum += sol.robustnessLow;
			}
			double val = robustnessSum / Math.max(1, solutions.size());
			addPoint("Average robustness", x, val);
		}


	}
}
