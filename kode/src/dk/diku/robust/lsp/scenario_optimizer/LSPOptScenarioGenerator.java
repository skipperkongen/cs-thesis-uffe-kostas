package dk.diku.robust.lsp.scenario_optimizer;

import javax.xml.bind.JAXBElement;

import dk.diku.robust.autogen.sclsp_experiment.Experiment;
import dk.diku.robust.autogen.sclsp_instance.SCLSPInstance;
import dk.diku.robust.autogen.sclsp_scenario.Capacity;
import dk.diku.robust.autogen.sclsp_scenario.CostAtTime;
import dk.diku.robust.autogen.sclsp_scenario.CostOnResource;
import dk.diku.robust.autogen.sclsp_scenario.Demand;
import dk.diku.robust.autogen.sclsp_scenario.Item;
import dk.diku.robust.autogen.sclsp_scenario.LotSizingScenario;
import dk.diku.robust.autogen.sclsp_scenario.ObjectFactory;
import dk.diku.robust.autogen.sclsp_scenario.Resource;
import dk.diku.robust.lsp.CLSPScenarioGenerator;
import dk.diku.robust.util.GiantMarshaller;
import dk.diku.robust.util.GiantUnmarshaller;

public class LSPOptScenarioGenerator {

	public LSPOptScenarioGenerator() {
		super();
	}

	public JAXBElement<LotSizingScenario> generate(double robustness, SCLSPInstance instance) throws Exception {

		OptSettings fatSettings = new OptSettings(robustness, instance);
		ObjectFactory fact = new ObjectFactory();
		JAXBElement<LotSizingScenario> jaxb = CLSPScenarioGenerator.makeScenario(instance, fact);
		LotSizingScenario scenario = jaxb.getValue();
		// I'm in your scenario, fixing your values

		// First, the capacities... assume 1 resource.
		for(Resource r : scenario.getResources().getResource()) {
			for(Capacity c : r.getCapacities().getCapacity()) {
				int time = c.getTimePeriod();
				c.setCapacity((int)Math.floor(fatSettings.fatCapacityArr[time-1]));
			}
		}

		// Then, the demands and capSetups
		for(Item item : scenario.getItems().getItem()) {
			int i = item.getId()-1;
			for(Demand demand : item.getDemands().getDemand()) {
				int t = demand.getTimePeriod() - 1;
				demand.setAmount((int)Math.ceil(fatSettings.fatDemandArr[i][t]));
			}
			for(CostOnResource cor : item.getProductionCosts().getCostOnResource()) {
				// again, assume single resource
				for(CostAtTime cat : cor.getCostAtTime()) {
					int t = cat.getTimePeriod()-1;
					cat.setSetupCapacity(((int)Math.ceil(fatSettings.fatCapSetupArr[i][t])));
				}
			}

		}

		jaxb.setValue(scenario);

		return jaxb;
	}

	public static void main(String... args) throws Exception {

		String experimentPath = "xml/lsp_experiments/exp_12cap_xtr6_15.xml";
		Experiment exp = GiantUnmarshaller.xmlToExperiment(experimentPath);
		int[] decrease = {90, 80, 70}; // percent

		String[] baseNames = {"xtr12-15", "xtr24-15", "xtr6-15"};
		String[] variations = {"_cap_B", "_cap_S", "_capSetup_B", "_capSetup_S", "_cap_capSetup_B", "_cap_capSetup_S"};
		String preFix = "opt";
		String folder = "xml/lsp_instances/";
		String ext = ".xml";
		String outputFolder = "output/";

		for(String baseName : baseNames) {
			for(String variation : variations) {
				for(int decreasedRobustness : decrease) {

					String fileName = baseName + variation + ext;
					String instancePath = folder + fileName;

					SCLSPInstance instance = GiantUnmarshaller.xmlToCLSP(instancePath);

					LSPOptScenarioGenerator generator = new LSPOptScenarioGenerator();
					JAXBElement<LotSizingScenario> fatScenario = generator.generate(exp.getDesiredRobustness() * (decreasedRobustness/100.0), instance);

					String output = outputFolder + preFix + decreasedRobustness + "_" + fileName;
					GiantMarshaller.marshallCLSPScenario(fatScenario, output);
				}
			}
		}







	}

}
