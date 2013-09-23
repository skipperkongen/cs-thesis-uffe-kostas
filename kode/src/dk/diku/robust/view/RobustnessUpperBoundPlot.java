package dk.diku.robust.view;

import java.util.List;
import java.util.Observable;

import dk.diku.robust.core.Solver;
import dk.diku.robust.core.SolverRunner;
import dk.diku.robust.core.model.SolutionData;

public class RobustnessUpperBoundPlot extends AbsMultiDoublePlot {

	public RobustnessUpperBoundPlot() {
		super(false);
		setChartTitle("Upper bounds on robustness across generations");
		setXLabel("Generation");
		setYLabel("Upper bound on robustness");
	}

	@Override
	public void update(Observable observable, Object obj) {
		if(observable instanceof SolverRunner) {
			SolverRunner runner = (SolverRunner) observable;
			Solver solver = runner.getSolver();
			// plot robustness, just for kicks
			int x = solver.getIteration();
			List<SolutionData> solutions = solver.getSolutions();
			int solutionID = 0;
			for(SolutionData sol : solutions) {
				double high = sol.robustnessHigh;
				addPoint(""+solutionID, x, high);
				solutionID++;
			}
		}

	}

}
