package dk.diku.robust.lsp.restacker;

import dk.diku.robust.autogen.sclsp_scenario.Capacity;
import dk.diku.robust.autogen.sclsp_scenario.CostAtTime;
import dk.diku.robust.autogen.sclsp_scenario.CostOnResource;
import dk.diku.robust.autogen.sclsp_scenario.Demand;
import dk.diku.robust.autogen.sclsp_scenario.InventoryCost;
import dk.diku.robust.autogen.sclsp_scenario.Item;
import dk.diku.robust.autogen.sclsp_scenario.LotSizingScenario;
import dk.diku.robust.autogen.sclsp_scenario.Resource;

public class LSPSample {
	
	int[] capacity; // capacity of resource in scenario
	Integer[][] capUsage; // capacity drain per item in scenario
	Integer[][] holdingCosts; // holding cost per item in scenario
	Integer[][] productionCosts; // production cost per item in scenario
	Integer[][] fixedCosts; // fixed cost per item in scenario
	int[][] demands; // demands per item per time period in scenario
	long totalCapacity;
	long totalDemand;


	public LSPSample(LotSizingScenario scenario) {
		totalCapacity = 0;
		totalDemand = 0;
		
		int numPeriods = scenario.getGlobalParameters().getTimePeriods();
		int numItems = scenario.getGlobalParameters().getNumItems();

		capacity = new int[numPeriods]; 
		for(Resource res : scenario.getResources().getResource()) {
			for(Capacity c : res.getCapacities().getCapacity()) {
				int t = c.getTimePeriod()-1;
				int cap = c.getCapacity();
				capacity[t] = cap;
				totalCapacity += cap;
			}
		}

		// Read capacity usage from scenario
		// also...
		// Read demand from scenario
		// Assume only one resource!
		capUsage = new Integer[numItems][numPeriods];
		holdingCosts = new Integer[numItems][numPeriods];
		productionCosts = new Integer[numItems][numPeriods];
		fixedCosts = new Integer[numItems][numPeriods];
		demands = new int[numItems][numPeriods];
		for(Item item : scenario.getItems().getItem()) {
			int itemID = item.getId();
			// capacity usage for item
			for(CostOnResource c : item.getProductionCosts().getCostOnResource()) {
				//int resID = c.getResourceId(); // ignore this, treat all resources as single resource
				for(CostAtTime cat : c.getCostAtTime()) {
					int time = cat.getTimePeriod();
					int usage = cat.getUnitCapacity();
					int prodCost = cat.getUnitProductionCost();
					int fixedCost = cat.getFixedProductionCost();
					// add usage to tally
					capUsage[itemID-1][time-1] = usage;
					productionCosts[itemID-1][time-1] = prodCost;
					fixedCosts[itemID-1][time-1] = fixedCost;
				}
			}
			// demand for item
			for(Demand d : item.getDemands().getDemand()) {
				int demand = d.getAmount();
				int time = d.getTimePeriod();
				demands[itemID-1][time-1] = demand;
				totalDemand += demand;
			}
			// holding cost of item
			for(InventoryCost invCost : item.getInventoryCosts().getInventoryCost()) {
				int holdingCost = invCost.getCost();
				int time = invCost.getTimePeriod();
				// add to normalizing term maxCost
				holdingCosts[itemID-1][time-1] = holdingCost;
			}
		}
		// forward values from t-1 when null present
		for(int t=0; t<numPeriods; t++) {
			for(int i=0; i<numItems; i++) {
				if(capUsage[i][t] == null) {
					capUsage[i][t] = (t==0 ? 0 : capUsage[i][t-1]);
				}
				if(holdingCosts[i][t] == null) {
					holdingCosts[i][t] = (t==0 ? 0 : holdingCosts[i][t-1]);
				}
				if(productionCosts[i][t] == null) {
					productionCosts[i][t] = (t==0 ? 0 : productionCosts[i][t-1]);
				}
				if(fixedCosts[i][t] == null) {
					fixedCosts[i][t] = (t==0 ? 0 : fixedCosts[i][t-1]);
				}
			}
		}
	}
}
