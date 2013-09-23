package dk.diku.robust.lsp.fatsolution;

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

public class LSPFatScenarioGenerator {
	
	public LSPFatScenarioGenerator() {
		super();
	}

	public JAXBElement<LotSizingScenario> generate(double robustness, SCLSPInstance instance) throws Exception {
		
		FatSettings fatSettings = new FatSettings(robustness, instance);
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

		String experimentPath = "xml/lsp_experiments/base_experiment.xml";
		Experiment exp = GiantUnmarshaller.xmlToExperiment(experimentPath);
		
		String[] baseNames = {/* "xtr12-15", "xtr12-30", "xtr24-15", "xtr24-30", */ "6_15" /*, "xtr6-30" */};
		String[] variations = {/*"_base", */ "_cap_b", /* "_cap_s" , "_setup_b", "_setup_s", "_cap_setup_b", "_cap_setup_s" */};
		String preFix = "scenroot_";
		String folder = "xml/lsp_instances/";
		String ext = ".xml";
		
		for(String baseName : baseNames) {
			for(String variation : variations) {
				String fileName = baseName + variation + ext;
				String instancePath = folder + fileName;
				
				SCLSPInstance instance = GiantUnmarshaller.xmlToCLSP(instancePath);

				LSPFatScenarioGenerator generator = new LSPFatScenarioGenerator();
				JAXBElement<LotSizingScenario> fatScenario = generator.generate(0.99658628705340965770155691795615, instance);
//				JAXBElement<LotSizingScenario> fatScenario = generator.generate(exp.getDesiredRobustness(), instance);

				String output = "output/" + preFix + fileName;
				GiantMarshaller.marshallCLSPScenario(fatScenario, output);
			}
		}
		


		

		

	}

}
