package dk.diku.robust.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import dk.diku.robust.autogen.knapsackexperiment.KnapsackExperiment;
import dk.diku.robust.autogen.sclsp_experiment.Experiment;
import dk.diku.robust.autogen.sclsp_instance.SCLSPInstance;
import dk.diku.robust.autogen.sclsp_solution.CLSPSolution;
import dk.diku.robust.autogen.stochknapsack.StochasticKnapsackProblem;

public class GiantUnmarshaller {
	
	// Knapsack
	private static String SKP_INSTANCE_SCHEMA = "schema/knapsack/knapsack_instance.xsd";
	private static String SKP_INSTANCE_CONTEXT = "dk.diku.robust.autogen.stochknapsack";
	private static String SKP_EXPERIMENT_SCHEMA = "schema/knapsack/knapsack_experiment.xsd";
	private static String SKP_EXPERIMENT_CONTEXT = "dk.diku.robust.autogen.knapsackexperiment";
	// Lotsizing - Instance
	private static String SCLSP_INSTANCE_SCHEMA = "schema/lotsizing/sclsp_instance.xsd";
	private static String SCLSP_INSTANCE_CONTEXT = "dk.diku.robust.autogen.sclsp_instance";
	// Lotsizing - Solution
	private static String CLSP_SOLUTION_SCHEMA = "schema/lotsizing/sclsp_solution.xsd";
	private static String CLSP_SOLUTION_CONTEXT = "dk.diku.robust.autogen.sclsp_solution";
	// General experiment
	private static String EXPERIMENT_SCHEMA = "schema/lotsizing/sclsp_experiment.xsd";
	private static String EXPERIMENT_CONTEXT = "dk.diku.robust.autogen.sclsp_experiment";	
	
	public static StochasticKnapsackProblem xmlToStochasticKnapsackProblem(String path) throws FileNotFoundException, JAXBException, SAXException  {
		return (StochasticKnapsackProblem) xmlToObject(path, SKP_INSTANCE_SCHEMA, SKP_INSTANCE_CONTEXT);
	}
	
	public static KnapsackExperiment xmlToKnapsackExperiment(String path) throws FileNotFoundException, JAXBException, SAXException  {
		return (KnapsackExperiment) xmlToObject(path, SKP_EXPERIMENT_SCHEMA, SKP_EXPERIMENT_CONTEXT);
	}
	
	public static SCLSPInstance xmlToCLSP(String path) throws FileNotFoundException, JAXBException, SAXException  {
		return (SCLSPInstance) xmlToObject(path, SCLSP_INSTANCE_SCHEMA, SCLSP_INSTANCE_CONTEXT);
	}

	public static CLSPSolution xmlToCLSPSolution(String path) throws FileNotFoundException, JAXBException, SAXException  {
		return (CLSPSolution) xmlToObject(path, CLSP_SOLUTION_SCHEMA, CLSP_SOLUTION_CONTEXT);
	}
	
	public static Experiment xmlToExperiment(String path) throws FileNotFoundException, JAXBException, SAXException  {
		return (Experiment) xmlToObject(path, EXPERIMENT_SCHEMA, EXPERIMENT_CONTEXT);
	}
	
	@SuppressWarnings("unchecked")
	private static Object xmlToObject(String path, String type, String context) throws JAXBException, SAXException, FileNotFoundException  {
		JAXBContext jc = JAXBContext.newInstance(context);
		Unmarshaller u = jc.createUnmarshaller();
		Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File(type));	
		u.setSchema(schema);
		FileInputStream is = new FileInputStream(path);
		Object instance =  ((JAXBElement)u.unmarshal(is)).getValue();
		return instance;
	}
	
	public static void main(String... args) throws Exception {

		Experiment exp = xmlToExperiment("xml/lsp_experiments/sclsp_experiment.xml");
		System.out.println(exp.getNumSeeds());
		
		// Test 1
		//CLSPSolution sol = xmlToCLSPSolution("xml/lsp_scenario_solutions/s1_solution.xml");
		//System.out.println("Timeperiods: " + sol.getGlobalParameters().getTimePeriods());
		
		
	}
}
