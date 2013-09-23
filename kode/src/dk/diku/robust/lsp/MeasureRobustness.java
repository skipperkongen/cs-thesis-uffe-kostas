package dk.diku.robust.lsp;

import java.io.FileNotFoundException;

import dk.diku.robust.autogen.sclsp_experiment.Experiment;
import dk.diku.robust.autogen.sclsp_instance.SCLSPInstance;
import dk.diku.robust.autogen.sclsp_solution.CLSPSolution;
import dk.diku.robust.lsp.model.LSPSolutionPOJO;
import dk.diku.robust.lsp.simulation.SimConclusion;
import dk.diku.robust.lsp.simulation.Simulator;
import dk.diku.robust.util.GiantUnmarshaller;

public class MeasureRobustness {
	
	private final static double PRECISION = 0.001;
	
	public static void main(String... args) throws Exception {
		
		String[] variations = {
				"root", 
//				"fat" , 
//				"opt90", 
//				"opt80", 
//				"opt70"
				};
		
		///////////////////////////////////////
		// TEST 
		
		String solType = "cap_setup_s";
		String instType = "cap_setup_s";
		
//		String solType = "cap_s";
//		String instType = "cap_s";

//		String instType = "setup_b";
//		String solType = "setup_b";
		
//		String instType = "cap_setup_b";
//		String solType = "cap_setup_b";
		
		// END
		//////////////////////////////////////
		
//		String solutionPath = "xml/lsp_solutions/fat_opt_solutions/opt90_6_15_cap_s.xml";

		String instancePath = "xml/lsp_instances/6_15_" + instType + ".xml";
		String experimentPath = "xml/lsp_experiments/base_experiment.xml";
		String baseSolutionPath = "xml/lsp_solutions/fat_opt_solutions/base_6_15" + ".xml";
		

		
		SCLSPInstance instance = GiantUnmarshaller.xmlToCLSP(instancePath);
		Experiment exp = GiantUnmarshaller.xmlToExperiment(experimentPath);
		Simulator simulator = new Simulator(exp, instance);		
		

		System.out.println("Instance: " + instancePath);
		
		System.out.println("\n--------------------------------\n");
		
		
		System.out.println("base solution: " + baseSolutionPath);
		CLSPSolution baseSolution = GiantUnmarshaller.xmlToCLSPSolution(baseSolutionPath);
		LSPSolutionPOJO base = new LSPSolutionPOJO(baseSolution);
		System.out.println(base);
		SimConclusion baseConclusion = simulator.simulate(base, PRECISION);
		System.out.println("base conclusion:");
		System.out.println(baseConclusion);
		double baseCost = baseConclusion.averageCost;
		double baseCostMinusInitial = baseCost - baseConclusion.averageInitialCost;

		System.out.println("\n--------------------------------\n");
		
		
		for(String variation : variations) {
			String solutionPath = "xml/lsp_solutions/fat_opt_solutions/" + variation + "_6_15_" + solType + ".xml";	
			try {
				CLSPSolution solution = GiantUnmarshaller.xmlToCLSPSolution(solutionPath);
				LSPSolutionPOJO pojo = new LSPSolutionPOJO(solution);
				SimConclusion conclusion = simulator.simulate(pojo, PRECISION);
				System.out.println(variation + " solution: " + solutionPath);
				System.out.println(pojo);
				System.out.println("");		
				System.out.println(conclusion.toString());
				System.out.println();
				double solCost = conclusion.averageCost;
				double solCostMinusInitial = solCost - conclusion.averageInitialCost;
				System.out.println("Cost increase: " + ((solCost / baseCost) - 1)*100 + "%");
				System.out.println("Cost increase (excl. initial cost): " + ((solCostMinusInitial / baseCostMinusInitial) - 1)*100 + "%");
				
				System.out.println("\n--------------------------------\n");
			}
			catch(FileNotFoundException e) {
				System.out.println("skipping " + solutionPath);
				
				System.out.println("\n--------------------------------\n");
			}
		}
		
		







//		System.out.println("\n"+pojo.toString());
		

		
	}

}
