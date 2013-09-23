package dk.diku.robust.lsp.view;

import java.util.List;
import java.util.Observable;

import dk.diku.robust.core.Solver;
import dk.diku.robust.core.SolverRunner;
import dk.diku.robust.core.model.SolutionData;
import dk.diku.robust.lsp.simulation.SimConclusion;
import dk.diku.robust.view.AbsMultiDoublePlot;

public class LSPAverageCostPlot extends AbsMultiDoublePlot {
	
	public LSPAverageCostPlot() {
		super(true);
		setChartTitle("Average cost");
		setXLabel("Generation");
		setYLabel("Average cost");
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
				SimConclusion conclusion = (SimConclusion) sol.getCustom();
				objectiveValueSum += conclusion.averageCost;
			}
			double val = objectiveValueSum / Math.max(1, solutions.size());
			addPoint("Average cost", x, val);
		}
	}
}