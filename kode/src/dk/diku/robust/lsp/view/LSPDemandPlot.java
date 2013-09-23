package dk.diku.robust.lsp.view;

import java.util.List;
import java.util.Observable;

import dk.diku.robust.core.Solver;
import dk.diku.robust.core.SolverRunner;
import dk.diku.robust.core.model.SolutionData;
import dk.diku.robust.lsp.simulation.SimConclusion;
import dk.diku.robust.view.AbsMultiDoublePlot;

public class LSPDemandPlot extends AbsMultiDoublePlot {
	
	public LSPDemandPlot() {
		super(true);
		setChartTitle("3 Part Demand");
		setXLabel("Generation");
		setYLabel("3 Part Demand");
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
			double sumDem = 0;
			double sumDemMiss = 0;
			double sumTotalDemand = 0;

			for(SolutionData sol : solutions) {
				SimConclusion conclusion = (SimConclusion) sol.getCustom();
				sumDem += conclusion.averageDemandMet;
				sumDemMiss += conclusion.averageDemandMissed;
				sumTotalDemand += conclusion.averageTotalDemand;
			}
			addPoint("Average demand met", x, sumDem / solutions.size());
			addPoint("Average demand missed", x, sumDemMiss / solutions.size());
			addPoint("Average demand", x, sumTotalDemand / solutions.size());

		}


	}
}
