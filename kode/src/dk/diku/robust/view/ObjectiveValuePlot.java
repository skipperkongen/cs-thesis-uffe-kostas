package dk.diku.robust.view;

import java.util.List;
import java.util.Observable;

import dk.diku.robust.core.Solver;
import dk.diku.robust.core.SolverRunner;
import dk.diku.robust.core.model.SolutionData;

public class ObjectiveValuePlot extends AbsMultiDoublePlot {
	
	private static final String DET_SOL_KEY = "Deterministic solution";
	private static final String FAT_SOL_KEY = "Fat solution";
	private double detSol;
	private double fatSol;

	public ObjectiveValuePlot() {
		super(false);
		setChartTitle("Objective values across generations");
		setXLabel("Generation");
		setYLabel("Objective value");
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
			int solutionID = 0;
			for(SolutionData sol : solutions) {
				double val = sol.objectiveValue;
				addPoint(""+solutionID, x, val);
				solutionID++;
			}
			addPoint(DET_SOL_KEY, x, detSol);
			addPoint(FAT_SOL_KEY, x, fatSol);
		}

	}
	
	public void detSolution(double d) {
		addNewSeries(DET_SOL_KEY);
		detSol = d;
	}
	
	public void fatSolution(double d) {
		addNewSeries(FAT_SOL_KEY);
		fatSol = d;
	}

}
