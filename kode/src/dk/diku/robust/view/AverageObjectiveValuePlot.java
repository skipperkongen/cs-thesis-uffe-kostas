package dk.diku.robust.view;

import java.util.List;
import java.util.Observable;

import dk.diku.robust.core.Solver;
import dk.diku.robust.core.SolverRunner;
import dk.diku.robust.core.model.SolutionData;

public class AverageObjectiveValuePlot extends AbsMultiDoublePlot {
	
	public AverageObjectiveValuePlot() {
		super(true);
		setChartTitle("Average objective value");
		setXLabel("Generation");
		setYLabel("Average objective value");
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
			double objectiveValueSum = 0;
			for(SolutionData sol : solutions) {
				objectiveValueSum += sol.objectiveValue;
			}
			double val = objectiveValueSum / Math.max(1, solutions.size());
			addPoint("Average objective value", x, val);
		}


	}
}
