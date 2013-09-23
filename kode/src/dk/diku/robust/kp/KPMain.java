package dk.diku.robust.kp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import dk.diku.robust.autogen.knapsackexperiment.KnapsackExperiment;
import dk.diku.robust.kp.control.KPExperimentController;
import dk.diku.robust.util.GiantUnmarshaller;

public class KPMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		HashMap<String, String> params = extractParameterMap(args);
		KPExperimentController ctrl = new KPExperimentController(params);
		if(!params.containsKey("experiments")) {
			System.err.println("Usage: java KPSolverController experiments=<comma separated list of paths> [optional parameters]");
			System.exit(1);
		}
		else {
			ArrayList<KnapsackExperiment> experiments = new ArrayList<KnapsackExperiment>();
			System.out.println("Reading experiments...");
			ArrayList<String> pathsToExperiments = extractPaths(params.get("experiments"));
			for(String pathToExperiment : pathsToExperiments) {
				System.out.println("- " + pathToExperiment);
				try {
					KnapsackExperiment exp = GiantUnmarshaller.xmlToKnapsackExperiment(pathToExperiment);
					experiments.add(exp);
				} catch (FileNotFoundException e) {
					System.err.println("Experiment file does not exist: " + pathToExperiment);
					System.exit(1);
				} catch (JAXBException e) {
					System.err.println("Experiment file might not have correct type: " + pathToExperiment);
					System.exit(1);
				} catch (SAXException e) {
					System.err.println("Error parsing experiment file: " + pathToExperiment);
					System.exit(1);
				}
			}
			System.out.println("Done reading experiments");
			System.out.println();
			System.out.println("Conducting experiments");
			for(KnapsackExperiment exp: experiments) {
				ctrl.conductExperiment(exp);
			}
		}
	}

	private static ArrayList<String> extractPaths(String pathString) {
		ArrayList<String> paths = new ArrayList<String>();
		String[] split = pathString.split(File.pathSeparator);
		for(String path : split) {
			paths.add(path);
		}
		return paths;
	}

	private static HashMap<String, String> extractParameterMap(String[] args) {
		HashMap<String, String> map = new HashMap<String, String>();
		for(String arg: args) {
			String[] split = arg.split("=");
			if(split.length == 2) {
				map.put(split[0], split[1]);
			}
		}
		return map;
	}

}
