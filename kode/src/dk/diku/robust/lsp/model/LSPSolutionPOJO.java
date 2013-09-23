package dk.diku.robust.lsp.model;

import dk.diku.robust.autogen.sclsp_solution.BeingSetupTimePeriod;
import dk.diku.robust.autogen.sclsp_solution.CLSPSolution;
import dk.diku.robust.autogen.sclsp_solution.IsSetupTimePeriod;
import dk.diku.robust.autogen.sclsp_solution.ItemOnStock;
import dk.diku.robust.autogen.sclsp_solution.ItemProduction;
import dk.diku.robust.autogen.sclsp_solution.ProductionTimePeriod;
import dk.diku.robust.autogen.sclsp_solution.ResourceBeingSetup;
import dk.diku.robust.autogen.sclsp_solution.ResourceIsSetup;
import dk.diku.robust.autogen.sclsp_solution.StockTimePeriod;

public class LSPSolutionPOJO {
	
	public int[] initialStorage;
	public int[][] production;
	public boolean[][] beingSetup;
	public boolean[][] isSetup;
	public int numItems;
	public int numPeriods;
	
	public LSPSolutionPOJO(CLSPSolution xml) {
		numItems = xml.getGlobalParameters().getItems().intValue();
		numPeriods = xml.getGlobalParameters().getTimePeriods().intValue();
		initialStorage = new int[numItems];
		production = new int[numItems][numPeriods];
		beingSetup = new boolean[numItems][numPeriods];
		isSetup = new boolean[numItems][numPeriods];
		
		// initial storage?
		for(StockTimePeriod tp : xml.getOnStock().getStockTimePeriod()) {
			int t = tp.getTimePeriod().intValue();
			if(t == 0) {
				for(ItemOnStock item : tp.getItemOnStock()) {
					int id = item.getItemId().intValue();
					int amount = item.getAmount().intValue();
					initialStorage[id-1] = amount;
				}
			}
		}
		
		// productions
		for(ProductionTimePeriod ptp : xml.getBeingProduced().getProductionTimePeriod()) {
			int t = ptp.getTimePeriod().intValue();
			for(ItemProduction ip : ptp.getItemProduction()) {
				int id = ip.getItemId().intValue();
				int amount = ip.getAmount().intValue();
				production[id-1][t-1] = amount;
			}
		}
		
		// being setup
		for(BeingSetupTimePeriod tp: xml.getBeingSetup().getBeingSetupTimePeriod()) {
			int t = tp.getTimePeriod().intValue();
			for(ResourceBeingSetup rbs : tp.getResourceBeingSetup()) {
				// single resource, setup on any resource means setup on resource 1
				int id = rbs.getItemId().intValue();
				beingSetup[id-1][t-1] = rbs.isBeingSetup();
			}
		}
		
		// is setup
		for(IsSetupTimePeriod tp : xml.getIsSetup().getIsSetupTimePeriod()) {
			int t = tp.getTimePeriod().intValue();
			for(ResourceIsSetup ris : tp.getResourceIsSetup()) {
				int id = ris.getItemId().intValue();
				isSetup[id-1][t-1] = ris.isIsSetup();
			}
		}
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("Initial storage: \n");
		for(int i=0; i<numItems; i++) {
			sb.append(initialStorage[i] + "\t");
		}
		sb.append("\n");
		
		sb.append("Production: \n");
		for(int i=0; i<numItems; i++) {
			for(int t=0; t<numPeriods; t++) {
				sb.append(production[i][t]  + "\t");
			}
			sb.append("\n");
		}
		sb.append("\n");
		
		sb.append("BeingSetup: \n");
		for(int i=0; i<numItems; i++) {
			for(int t=0; t<numPeriods; t++) {
				sb.append((beingSetup[i][t]?1:0)  + "\t");
			}
			sb.append("\n");
		}
		sb.append("\n");
		
		sb.append("IsSetup: \n");
		for(int i=0; i<numItems; i++) {
			for(int t=0; t<numPeriods; t++) {
				sb.append((isSetup[i][t]?1:0)  + "\t");
			}
			sb.append("\n");
		}
		sb.append("\n");
		return sb.toString();
	}
}
