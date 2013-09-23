package dk.diku.robust.view;

import java.util.List;
import java.util.Observable;

import dk.diku.robust.core.Solver;
import dk.diku.robust.core.SolverRunner;
import dk.diku.robust.core.model.SolutionData;

public class AverageFitnessPlot extends AbsMultiDoublePlot {
	
	public AverageFitnessPlot() {
		super(true);
		setChartTitle("Average fitness");
		setXLabel("Generation");
		setYLabel("Average fitness");
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
			double fitnessSum = 0;
			for(SolutionData sol : solutions) {
				fitnessSum += sol.fitnessValue;
			}
			double val = fitnessSum / Math.max(1, solutions.size());
			addPoint("Average fitness", x, val);
		}


	}
}
