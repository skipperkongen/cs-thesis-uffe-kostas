package dk.diku.robust.lsp;

import java.io.File;

import dk.diku.robust.autogen.sclsp_experiment.Experiment;
import dk.diku.robust.autogen.sclsp_instance.SCLSPInstance;
import dk.diku.robust.autogen.sclsp_solution.CLSPSolution;
import dk.diku.robust.lsp.model.LSPSolutionPOJO;
import dk.diku.robust.lsp.simulation.SimConclusion;
import dk.diku.robust.lsp.simulation.Simulator;
import dk.diku.robust.util.GiantUnmarshaller;

public class AllInAllRobustness {

	/**
	 * @param args
	 * @throws Exception 
	 */
	
	private static final double PRECISION = 0.01;
	
	public static void main(String[] args) throws Exception {
		
		String experimentPath = "xml/lsp_experiments/base_experiment.xml";
		String solutionsPath = "xml/lsp_solutions/fat_opt_solutions";
		String instancesPath = "xml/lsp_instances";

		File solFolder = new File(solutionsPath);
		File instFolder = new File(instancesPath);
		
		System.out.println("is folder: " + solFolder.isDirectory());
		System.out.println("is folder: " + instFolder.isDirectory());
		
		

		System.out.println("Begin simulation of all solutions in all instances:");
		
		int instanceID = 1;

		Experiment exp = GiantUnmarshaller.xmlToExperiment(experimentPath);
		
		for(File instFile : instFolder.listFiles()) {
			if(!instFile.getName().endsWith(".xml")) {
				continue;
			}
			
			System.out.println();
			
			SCLSPInstance instance = GiantUnmarshaller.xmlToCLSP(instFile.getAbsolutePath());
			Simulator simulator = new Simulator(exp, instance);
			
			System.out.println("\\begin{table}[!hbp]");
			System.out.println("\\label{tab:instance"+ instanceID +"_results}");
			
			System.out.println("\\begin{tabular}{lllll}");
			System.out.println("\\hline");
			System.out.println("Solution \t& Robustness  \t& Bounds \t& Cost \t& Cost (excl. initial) \\\\");
			System.out.println("\\hline");
			System.out.println("\\hline");

			for(File solFile : solFolder.listFiles()) {
				if(!solFile.getName().endsWith(".xml")) {
					continue;
				}
				CLSPSolution solution = GiantUnmarshaller.xmlToCLSPSolution(solFile.getAbsolutePath());
				LSPSolutionPOJO pojo = new LSPSolutionPOJO(solution);
				SimConclusion conclusion = simulator.simulate(pojo, PRECISION);
				String robust = (conclusion.robust?"OK":"FAIL");
				double LB = Math.floor(conclusion.robustnessLB*100)/100.0;
				double UB = Math.floor(conclusion.robustnessUB*100)/100.0;
				double cost = Math.floor(conclusion.averageCost);
				double costMinus = Math.floor(conclusion.averageCost-conclusion.averageInitialCost);
				System.out.println(solFile.getName() + " \t& " + robust + " \t& " + " [" + LB + ", " + UB + "]" + " \t& " + cost + " \t& " + costMinus + "\\\\");
			}
			System.out.println("\\hline");
			System.out.println("\\end{tabular}");
			System.out.println("\\caption{Simulation results for instance "+ instFile.getName() +"}");
			System.out.println("\\end{table}");
			System.out.println();
			instanceID++;
		}
		


	}

}
