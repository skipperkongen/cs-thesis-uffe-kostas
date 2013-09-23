package dk.diku.robust.kp.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import dk.diku.robust.autogen.knapsackexperiment.KnapsackExperiment;
import dk.diku.robust.autogen.stochknapsack.StochasticKnapsackProblem;
import dk.diku.robust.core.SolverRunner;
import dk.diku.robust.kp.SKPCalculator;
import dk.diku.robust.kp.exactsolver.KPSolverExact;
import dk.diku.robust.kp.gasolver.KPSolverGenetic;
import dk.diku.robust.kp.model.KP;
import dk.diku.robust.kp.model.KPFactory;
import dk.diku.robust.kp.model.KPSolution;
import dk.diku.robust.util.GiantUnmarshaller;
import dk.diku.robust.view.BestSolutionTrackerPlot;
import dk.diku.robust.view.FitnessPlot;
import dk.diku.robust.view.IterationObserver;
import dk.diku.robust.view.ObjectiveValuePlot;
import dk.diku.robust.view.RobustnessLowerBoundPlot;
import dk.diku.robust.view.RobustnessUpperBoundPlot;
import dk.diku.robust.view.SolutionPrinter;

public class KPExperimentController {

	private HashMap<String, String> experimentParams;

	public KPExperimentController(HashMap<String, String> experimentParams) {
		super();
		this.experimentParams = experimentParams; 
	}

	public void conductExperiment(KnapsackExperiment exp) throws Exception {
		System.out.println("Reading instance");
		System.out.println("- " + exp.getPathToInstance());
		String pathToSKP = exp.getPathToInstance();
		StochasticKnapsackProblem autogenSKP = null;
		try {
			autogenSKP = GiantUnmarshaller.xmlToStochasticKnapsackProblem(pathToSKP);
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + pathToSKP);
		} catch (JAXBException e) {
			System.err.println("JAXB error reading file: " + pathToSKP);
			// TODO Auto-generated catch block
		} catch (SAXException e) {
			System.err.println("SAX error parsing file: " + pathToSKP);
		}
		// finished reading experiments AND instance
		if(autogenSKP != null) {
			if(exp.getGeneticAlgorithm() != null) {
				runGenetic(exp, autogenSKP);
			}
			if(exp.getLocalSearch() != null) {
				// run local search algorithm
			}
		}
		
		
	}

	private void runGenetic(KnapsackExperiment exp, StochasticKnapsackProblem autogenSKP) throws Exception {
		// run genetic algorithm
		PrintStream origStream = System.out;
		File file  = new File(exp.getPathToLog());	
		PrintStream printStream = new PrintStream(new FileOutputStream(file));
		System.setOut(printStream);

		
		KPSolverGenetic ga = new KPSolverGenetic(exp, autogenSKP);
		ga.setMaxIteration(exp.getNumIterations().intValue());
		ga.setPopulationSize(exp.getGeneticAlgorithm().getPopulationSize().intValue());
		ga.setSimulationsPerTest(exp.getSimulation().getNumSimulations().intValue());
		
		// create runner object
		SolverRunner runner = new SolverRunner(ga);
		
		// calculating deterministic solution
		KP deterministicKP = KPFactory.getInstance().getDeterministicScenario(autogenSKP);
		KPSolverExact kpSolver = new KPSolverExact();
		KPSolution detSolution = kpSolver.solve(deterministicKP);
		System.out.println("Deterministic capacity = "+detSolution.getKp().getCapacity());
		System.out.println("Deterministic solution = "+detSolution.getTotalProfit());
		double[] detRobustness = SKPCalculator.calculateRobustness(autogenSKP, detSolution, 100000);
		System.out.println("Deterministic solution (robustness): ["+detRobustness[0]+","+detRobustness[1]+"]");

		
		// calculating fat solution
		KPSolution fatSolution = SKPCalculator.findFatSolution(autogenSKP, exp.getDesiredRobustness());
		System.out.println("Fat capacity = "+fatSolution.getKp().getCapacity());
		System.out.println("Fat solution = "+fatSolution.getTotalProfit());
		double[] fatRobustness = SKPCalculator.calculateRobustness(autogenSKP, fatSolution, 100000);
		System.out.println("Fat solution (robustness): ["+fatRobustness[0]+","+fatRobustness[1]+"]");

		// attach observers
		System.out.println("Attaching observers");
		IterationObserver obs = new IterationObserver();
		SolutionPrinter solPrint = new SolutionPrinter();

		
		if(exp.isVisualization()){
			RobustnessLowerBoundPlot plotLB = new RobustnessLowerBoundPlot();	
			RobustnessUpperBoundPlot plotUB = new RobustnessUpperBoundPlot();
			plotLB.show();
			plotUB.show();
			
			ObjectiveValuePlot plotVal = new ObjectiveValuePlot();
			plotVal.detSolution(detSolution.getTotalProfit());
			plotVal.fatSolution(fatSolution.getTotalProfit());
			plotVal.show();
			
			BestSolutionTrackerPlot plotBest = new BestSolutionTrackerPlot();
			plotBest.show();
			
			FitnessPlot plotFit = new FitnessPlot();
			plotFit.show();
			
			runner.addObserver(plotLB);
			runner.addObserver(plotUB);
			runner.addObserver(plotVal);
			runner.addObserver(plotBest);
			runner.addObserver(plotFit);

		}
		
		runner.addObserver(obs);
		runner.addObserver(solPrint);

		// start execution thread
		System.out.println("Starting solver");
		Thread t = new Thread(runner);
		t.start();
		try {
			t.join();
		}
		catch (InterruptedException e) {
			System.out.print("Join interrupted\n");
		}
		System.setOut(origStream);
		System.out.println(exp.getPathToInstance()+" Done");

	}

}
