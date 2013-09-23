package dk.diku.robust.lsp.fatsolution;

import dk.diku.robust.autogen.sclsp_instance.Capacity;
import dk.diku.robust.autogen.sclsp_instance.Item;
import dk.diku.robust.autogen.sclsp_instance.Normal;
import dk.diku.robust.autogen.sclsp_instance.SCLSPInstance;
import dk.diku.robust.autogen.sclsp_instance.TimePeriod;
import dk.diku.robust.autogen.sclsp_instance.Value;
import dk.diku.robust.stat.StatMath;

public class FatSettings {

	double[][] fatDemandArr;
	Double[] fatCapacityArr;
	Double[][] fatCapSetupArr;
	SCLSPInstance instance;
	int numItems;
	int numPeriods;
	double robustness;

	public FatSettings(double robustness, SCLSPInstance instance) {
		super();
		this.instance = instance;
		this.robustness = robustness;
		numItems = instance.getNumItems();
		numPeriods = instance.getNumPeriods();
		this.fatDemandArr = new double[numItems][numPeriods];
		this.fatCapacityArr = new Double[numPeriods];
		this.fatCapSetupArr = new Double[numItems][numPeriods];
		setValues();
	}

	private void setValues() {

		// Item values
		for(Item item : instance.getItem()) {
			int itemID = item.getItemId();
			for(TimePeriod tp : item.getTimePeriod()) {
				int time = tp.getTimePeriod();

				// Find fat demand
				Value demand = tp.getDemand();
				if(demand != null) {
					double fatDemand  = -1;
					if(demand.getNormal() != null) {
						Normal norm = demand.getNormal();
						fatDemand = StatMath.getLowerBoundNormal(norm.getMean(), Math.pow(norm.getStandardDeviation(),2), 1-robustness);
					}
					else {
						fatDemand = demand.getConstantValue().doubleValue();
					}
					fatDemandArr[itemID-1][time-1] = fatDemand;
				}

				// Find fat capSetup
				Value capSetup = tp.getCapSetup();
				if(capSetup != null) {
					double fatCapSetup = -1;
					if(capSetup.getNormal() != null) {
						Normal norm = capSetup.getNormal();
						fatCapSetup = StatMath.getLowerBoundNormal(norm.getMean(), Math.pow(norm.getStandardDeviation(),2), 1-robustness);
					}
					else {
						fatCapSetup = capSetup.getConstantValue().doubleValue();
					}
					fatCapSetupArr[itemID-1][time-1] = fatCapSetup;
				}
			}
		}

		// Fat Capacity
		for(Capacity capacity : instance.getCapacity()) {
			Value value = capacity.getValue();
			if(value != null) {
				double fatValue = -1;
				if(value.getNormal() != null) {
					Normal norm = value.getNormal();
					fatValue = StatMath.getLowerBoundNormal(norm.getMean(), Math.pow(norm.getStandardDeviation(),2), robustness);
				}
				else {
					fatValue = capacity.getValue().getConstantValue();
				}
				fatCapacityArr[capacity.getTimePeriod()-1] = fatValue;
			}
		}

		// Forward values to missing values, capacities and capusage
		for(int i=0;i<numItems;i++) {
			for(int t=0;t<numPeriods;t++) {
				if(t==0) {
					// Initialize to zero?
					if(fatCapacityArr[t] == null) {
						fatCapacityArr[t] = 0.0;
					}
					if(fatCapSetupArr[i][t] == null) {
						fatCapSetupArr[i][t] = 0.0;
					}
				}
				else {
					// Forward previous value?
					if(fatCapacityArr[t] == null) {
						fatCapacityArr[t] = fatCapacityArr[t-1];
					}
					if(fatCapSetupArr[i][t] == null) {
						fatCapSetupArr[i][t] = fatCapSetupArr[i][t-1];
					}
				}
			}	
		}
	}


}
