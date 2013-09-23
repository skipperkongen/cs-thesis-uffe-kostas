package dk.diku.robust.lsp;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import cern.jet.random.Normal;
import cern.jet.random.engine.MersenneTwister;

import dk.diku.robust.autogen.sclsp_instance.Capacity;
import dk.diku.robust.autogen.sclsp_instance.Item;
import dk.diku.robust.autogen.sclsp_instance.SCLSPInstance;
import dk.diku.robust.autogen.sclsp_instance.TimePeriod;
import dk.diku.robust.autogen.sclsp_instance.Value;
import dk.diku.robust.autogen.sclsp_scenario.BOMRelation;
import dk.diku.robust.autogen.sclsp_scenario.BOMRelations;
import dk.diku.robust.autogen.sclsp_scenario.Capacities;
import dk.diku.robust.autogen.sclsp_scenario.CostAtTime;
import dk.diku.robust.autogen.sclsp_scenario.CostOnResource;
import dk.diku.robust.autogen.sclsp_scenario.Demand;
import dk.diku.robust.autogen.sclsp_scenario.Demands;
import dk.diku.robust.autogen.sclsp_scenario.GlobalParameters;
import dk.diku.robust.autogen.sclsp_scenario.InventoryCost;
import dk.diku.robust.autogen.sclsp_scenario.InventoryCosts;
import dk.diku.robust.autogen.sclsp_scenario.Items;
import dk.diku.robust.autogen.sclsp_scenario.LotSizingScenario;
import dk.diku.robust.autogen.sclsp_scenario.ObjectFactory;
import dk.diku.robust.autogen.sclsp_scenario.ProductionCosts;
import dk.diku.robust.autogen.sclsp_scenario.Resource;
import dk.diku.robust.autogen.sclsp_scenario.Resources;
import dk.diku.robust.autogen.sclsp_scenario.StopCriteria;
import dk.diku.robust.autogen.sclsp_scenario.Text;
import dk.diku.robust.util.GiantMarshaller;
import dk.diku.robust.util.GiantUnmarshaller;

public class CLSPScenarioGenerator {

	// parameter keys
	private static final String PATH_KEY = "p";
	private static final String NUMSCENARIOS_KEY = "n";
	private static final String OUTPUT_FOLDER_KEY = "o";
	private static final String FILE_PREFIX_KEY = "f";
	// defaults
	private static final String DEFAULT_FILE_PREFIX = "sp12_cap_xtr6_15_scenario";
	private static final String DEFAULT_OUTPUT_FOLDER = "output";
	private static final int DEFAULT_NUMSCENARIOS = 50;
	private static final String DEFAULT_PATH_TO_SCLSP = "xml/lsp_instances/s12_cap_xtr6_15.xml";
	// various
	private static final int DUMMY_VALUE = 0;
	private static final int NUM_RESOURCES_SCLSP = 1;
	private static final int SINGLE_RESOURCE_ID = 1;
	private static final int DEFAULT_LEADTIME = 0;
	private static int scenarioID;
	private static String folder;
	private static String prefix;
	private static Normal normal = new Normal(0, 1, new MersenneTwister());

	/**
	 * Sorry for monolith code
	 * @param args
	 * @throws SAXException 
	 * @throws JAXBException 
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {

		String xmlPath = DEFAULT_PATH_TO_SCLSP;
		prefix = DEFAULT_FILE_PREFIX;
		folder = DEFAULT_OUTPUT_FOLDER;
		int numScenarios = DEFAULT_NUMSCENARIOS;
		scenarioID = 1;

		// Read parameter map from args
		HashMap<String, String> params = extractParameterMap(args);
		if(params.containsKey(PATH_KEY)) {
			xmlPath = params.get(PATH_KEY);
		}
		if(params.containsKey(NUMSCENARIOS_KEY)) {
			numScenarios = Integer.parseInt(params.get(NUMSCENARIOS_KEY));
		}
		if(params.containsKey(FILE_PREFIX_KEY)) {
			prefix = params.get(FILE_PREFIX_KEY);
		}
		if(params.containsKey(OUTPUT_FOLDER_KEY)) {
			folder = params.get(OUTPUT_FOLDER_KEY);
		}

		System.out.println("Reading stochastic instance: " + xmlPath);
		System.out.println("Generating scenarios...");

		// Read the instance
		SCLSPInstance stoch_inst = GiantUnmarshaller.xmlToCLSP(xmlPath);

		// make array for holding scenarios
		JAXBElement[] scenarios = new JAXBElement[numScenarios];

		// Create scenario object factory
		ObjectFactory fact = new ObjectFactory();

		// create all scenarios
		for(int i=0; i<numScenarios; i++) {
			scenarios[i] = makeScenario(stoch_inst, fact);
			System.out.println("Generated a scenario");
		}



		// output all generated scenarios to xml
		for(int i=0; i<numScenarios;i++) {
			JAXBElement<LotSizingScenario> elem = scenarios[i];
			String path = "" + folder + "/" + elem.getValue().getText().getName() + ".xml";
			GiantMarshaller.marshallCLSPScenario(scenarios[i], path);
		}
	}

	/**
	 * So sorry
	 * @param sclsp
	 * @param fact
	 * @return
	 * @throws Exception
	 */
	public static JAXBElement<LotSizingScenario> makeScenario(SCLSPInstance sclsp, ObjectFactory fact) throws Exception {
		LotSizingScenario scen = fact.createLotSizingScenario();
		// create text part
		Text text = fact.createText();
		text.setName(prefix + scenarioID++);
		text.setComment("Autogenerated scenario");
		scen.setText(text);
		// create stop criteria
		StopCriteria crit = fact.createStopCriteria();
		crit.setBranchAndBoundNodes(DUMMY_VALUE);
		crit.setCpuTimeMillis(DUMMY_VALUE);
		crit.setOptimalityGap(DUMMY_VALUE + 0.0f);
		scen.setStopCriteria(crit);
		// create global parameters
		GlobalParameters gp = fact.createGlobalParameters();
		gp.setNumItems(sclsp.getNumItems());
		gp.setNumResources(NUM_RESOURCES_SCLSP);
		gp.setTimePeriods(sclsp.getNumPeriods());
		gp.setNumOrders(countOrders(sclsp));
		scen.setGlobalParameters(gp);

		// create resource(s), always single resource
		Resources resources = fact.createResources();
		Resource singleRes = fact.createResource();
		singleRes.setId(SINGLE_RESOURCE_ID);
		Capacities scenCaps = fact.createCapacities();
		List<dk.diku.robust.autogen.sclsp_scenario.Capacity> capList = scenCaps.getCapacity();
		for(Capacity cap : sclsp.getCapacity()) {
			dk.diku.robust.autogen.sclsp_scenario.Capacity scenCap = fact.createCapacity();
			scenCap.setCapacity(realizeValue(cap.getValue()));
			scenCap.setTimePeriod(cap.getTimePeriod());
			capList.add(scenCap);
		}
		singleRes.setCapacities(scenCaps);
		resources.getResource().add(singleRes);
		scen.setResources(resources);

		// create items
		Items items = fact.createItems();
		List<dk.diku.robust.autogen.sclsp_scenario.Item> itemList = items.getItem();
		List<Item> its = sclsp.getItem();
		for(Item it : its) {
			dk.diku.robust.autogen.sclsp_scenario.Item item = makeItem(it, fact);
			itemList.add(item);
		}
		scen.setItems(items);
		return fact.createInstance(scen);

	}

	private static dk.diku.robust.autogen.sclsp_scenario.Item makeItem(Item instanceItem, ObjectFactory fact) {
		dk.diku.robust.autogen.sclsp_scenario.Item item = fact.createItem();

		// set id
		item.setId(instanceItem.getItemId());

		// postpone setting initial storage until all stochastic variables have been realized
		// ...

		// create production costs
		ProductionCosts pc = fact.createProductionCosts();
		List<CostOnResource> corList = pc.getCostOnResource();
		CostOnResource cor = fact.createCostOnResource();
		cor.setResourceId(SINGLE_RESOURCE_ID);
		corList.add(cor);
		populateCostOnResource(cor, instanceItem, fact);
		item.setProductionCosts(pc);

		// create inventory costs
		InventoryCosts ics = fact.createInventoryCosts();
		List<InventoryCost> icList = ics.getInventoryCost();
		populateInventoryCostList(icList, instanceItem, fact);
		item.setInventoryCosts(ics);

		// create BOM relations, single level, so easy
		BOMRelations brs = fact.createBOMRelations();
		List<BOMRelation> br = brs.getBomRelation();
		br.add(null);
		item.setBomRelations(brs);

		// create demands
		Demands ds = fact.createDemands();
		List<Demand> dList = ds.getDemand();
		populateDemandList(dList, instanceItem, fact);
		item.setDemands(ds);

		// calculate initial storage
		item.setInitialInventory(countDemand(item));

		return item;

	}

	private static void populateDemandList(List<Demand> dList, Item instanceItem, ObjectFactory fact) {
		for(TimePeriod tp : instanceItem.getTimePeriod()) {
			if(tp.getDemand() != null) {
				Demand d = fact.createDemand();
				d.setTimePeriod(tp.getTimePeriod());
				d.setAmount(realizeValue(tp.getDemand()));
				dList.add(d);
			}
		}

	}

	private static void populateInventoryCostList(List<InventoryCost> icList, Item instanceItem, ObjectFactory fact) {
		for(TimePeriod tp : instanceItem.getTimePeriod()) {
			if(tp.getHoldingCost() != null) {
				InventoryCost ic = fact.createInventoryCost();
				ic.setTimePeriod(tp.getTimePeriod());
				ic.setCost(realizeValue(tp.getHoldingCost()));
				icList.add(ic);
			}
		}

	}

	private static void populateCostOnResource(CostOnResource cor, Item instanceItem, ObjectFactory fact) {
		List<CostAtTime> catList = cor.getCostAtTime();
		for(TimePeriod tp : instanceItem.getTimePeriod()) {
			CostAtTime cat = fact.createCostAtTime();
			boolean addedSomething = false;
			if(tp.getProductionCost() != null) {
				cat.setUnitProductionCost(realizeValue(tp.getProductionCost()));
				addedSomething = true;
			}
			if(tp.getFixedCost() != null) {
				cat.setFixedProductionCost(realizeValue(tp.getFixedCost()));
				addedSomething = true;
			}
			if(tp.getFixedCost() != null) {
				cat.setStartupCost(realizeValue(tp.getFixedCost()));
				addedSomething = true;
			}
			if(tp.getCapUsage() != null) {
				cat.setUnitCapacity(realizeValue(tp.getCapUsage()));
				addedSomething = true;
			}
			if(tp.getCapSetup() != null) {
				cat.setSetupCapacity(realizeValue(tp.getCapSetup()));
				addedSomething = true;
			}
			if(tp.getTimePeriod() <= 1) {
				cat.setLeadTime(DEFAULT_LEADTIME);
				addedSomething = true;
			}
			cat.setTimePeriod(tp.getTimePeriod());
			if(addedSomething) {
				catList.add(cat);
			}
		}
	}

	private static int realizeValue(Value value) {
		if(value.getConstantValue() != null) {
			return value.getConstantValue();
		}
		else {
			return sampleNorm(value.getNormal());
		}
	}

	private static int countDemand(dk.diku.robust.autogen.sclsp_scenario.Item item) {
		int sum = 0;
		for(Demand d : item.getDemands().getDemand()) {
			sum += d.getAmount();
		}
		return sum;
	}

	private static int sampleNorm(dk.diku.robust.autogen.sclsp_instance.Normal normalDistr) {
		double d = normal.nextDouble(normalDistr.getMean(), normalDistr.getStandardDeviation());
		return (int)Math.max(0, Math.round(d));
	}

	/**
	 * Count an order in stochastic instance of CLSP for each constant demand greater than zero, and each stochastic demand with 
	 * a mean greater than zero (only normal distribution allowed).
	 * @param sclsp
	 * @return
	 */
	private static int countOrders(SCLSPInstance sclsp) {
		int count = 0;
		for(Item item : sclsp.getItem()) {
			for(TimePeriod p: item.getTimePeriod()) {
				Value val = p.getDemand();
				if(val != null) {
					count++;
					break;
				}
			}
		}
		return count;
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
