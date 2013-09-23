package dk.diku.robust.lsp;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBElement;

import dk.diku.robust.autogen.sclsp_instance.Capacity;
import dk.diku.robust.autogen.sclsp_instance.Item;
import dk.diku.robust.autogen.sclsp_instance.Normal;
import dk.diku.robust.autogen.sclsp_instance.ObjectFactory;
import dk.diku.robust.autogen.sclsp_instance.SCLSPInstance;
import dk.diku.robust.autogen.sclsp_instance.TimePeriod;
import dk.diku.robust.autogen.sclsp_instance.Value;
import dk.diku.robust.util.GiantMarshaller;

public class SCLSPInstanceGenerator {

	public static final float BIG_SPREAD_PERCENT = 0.25f;
	public static final float SMALL_SPREAD_PERCENT = 0.05f;



	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		String[] instances = {/*"xtr12-15", "xtr12-30", "xtr24-15", "xtr24-30",*/ "6_15" /*, "xtr6-30" */ };
		
		for(String instance : instances) {
			SCLSPInstanceGenerator generator = new SCLSPInstanceGenerator();
			generator.createInstancesFromTxt(instance);			
		}
	}

	public void createInstancesFromTxt(String instance) throws Exception {
		String tablesAndMillerFolder ="data/tablesandmiller/miller/";
		String ext = ".lsp";
		String file = instance+ext;
		String baseOutputPath = "output/" + instance + "_base.xml";
		String capBigInstancePath = "output/" + instance + "_cap_b.xml";
		String capSmallInstancePath = "output/" + instance + "_cap_s.xml";
		String capSetupBigInstancePath = "output/" + instance + "_setup_b.xml";
		String capSetupSmallInstancePath = "output/" + instance + "_setup_s.xml";
		String capCapSetupBigInstancePath = "output/" + instance + "_cap_setup_b.xml";
		String capCapSetupSmallInstancePath = "output/" + instance + "_cap_setup_s.xml";

		JAXBElement<SCLSPInstance> baseInstance = getBaseInstance(new File(tablesAndMillerFolder + file));
		GiantMarshaller.marshallSCLSPInstance(baseInstance, baseOutputPath);

		JAXBElement<SCLSPInstance> capBig = getInstance(baseInstance.getValue(), true, false, BIG_SPREAD_PERCENT);
		GiantMarshaller.marshallSCLSPInstance(capBig, capBigInstancePath);

		JAXBElement<SCLSPInstance> capSmall = getInstance(baseInstance.getValue(), true, false, SMALL_SPREAD_PERCENT);
		GiantMarshaller.marshallSCLSPInstance(capSmall, capSmallInstancePath);

		JAXBElement<SCLSPInstance> setupBig = getInstance(baseInstance.getValue(), false, true, BIG_SPREAD_PERCENT);
		GiantMarshaller.marshallSCLSPInstance(setupBig, capSetupBigInstancePath);

		JAXBElement<SCLSPInstance> setupSmall = getInstance(baseInstance.getValue(), false, true, SMALL_SPREAD_PERCENT);
		GiantMarshaller.marshallSCLSPInstance(setupSmall, capSetupSmallInstancePath);

		JAXBElement<SCLSPInstance> capSetupBig = getInstance(baseInstance.getValue(), true, true, BIG_SPREAD_PERCENT);
		GiantMarshaller.marshallSCLSPInstance(capSetupBig, capCapSetupBigInstancePath);

		JAXBElement<SCLSPInstance> capSetupSmall = getInstance(baseInstance.getValue(), true, true, SMALL_SPREAD_PERCENT);
		GiantMarshaller.marshallSCLSPInstance(capSetupSmall, capCapSetupSmallInstancePath);
	}

	private JAXBElement<SCLSPInstance> getInstance(SCLSPInstance baseInstance, boolean stochCapacity, boolean stochSetup, float spreadPercent) {

		ObjectFactory fact = new ObjectFactory();

		// modify capacities
		for(Capacity capacity : baseInstance.getCapacity()) {
			Value value = capacity.getValue(); 
			float mean = -1;
			if(stochCapacity) {
				// make stochastic

				float std_deviation = -1;
				if(value.getNormal() != null) {
					mean = value.getNormal().getMean();

				}
				else {
					mean = value.getConstantValue();
				}
				std_deviation = mean * spreadPercent;
				Normal norm = fact.createNormal();
				norm.setMean(mean);
				norm.setStandardDeviation(std_deviation);
				value.setNormal(norm);
				value.setConstantValue(null);
			}
			else {
				// make constant
				if(value.getNormal() != null) {
					mean = value.getNormal().getMean();
				}
				else {
					mean = value.getConstantValue();
				}
				value.setConstantValue((int)mean);
				value.setNormal(null);
			}
		}
		
		// modify setup
		for(Item item : baseInstance.getItem()) {
			for(TimePeriod timePeriod : item.getTimePeriod()) {
				Value value = timePeriod.getCapSetup(); 
				float mean = -1;
				if(stochSetup) {
					// make stochastic
					float std_deviation = -1;
					if(value.getNormal() != null) {
						mean = value.getNormal().getMean();

					}
					else {
						mean = value.getConstantValue();
					}
					std_deviation = mean * spreadPercent;
					Normal norm = fact.createNormal();
					norm.setMean(mean);
					norm.setStandardDeviation(std_deviation);
					value.setNormal(norm);
					value.setConstantValue(null);
				}
				else {
					// make constant
					if(value.getNormal() != null) {
						mean = value.getNormal().getMean();
					}
					else {
						mean = value.getConstantValue();
					}
					value.setConstantValue((int)mean);
					value.setNormal(null);
				}
			}
		}

		JAXBElement<SCLSPInstance> newInstance = fact.createSclspInstance(baseInstance);
		return newInstance;
	}

	public static JAXBElement<SCLSPInstance> getBaseInstance(File file) throws Exception {

		final int NUM_ITEMS = 0;
		final int NUM_PERIODS = 1;
		final int CAPACITIES = 2;
		final int EMPTY = 3;
		final int INIT_COST = 4;
		final int PROD_COST = 5;
		final int HOLD_COST = 6;
		final int FIXD_COST = 7;
		final int DEMAND = 8;
		final int CAP_USAGE = 9;
		final int CAP_SETUP = 10;

		int mode = NUM_ITEMS;

		ObjectFactory fact = new ObjectFactory();
		SCLSPInstance instance = fact.createSCLSPInstance();

		Scanner lineScanner = new Scanner(file);

		Item currentItem = null;
		int currentItemID = 0;
		int numItems = -1;
		int numPeriods = -1;
		int initialCost = -1;
		int[] prodCost = null;
		int[] fixedCost = null;
		int[] holdCost = null;
		int[] demand = null;
		int[] capUsage = null;
		int[] capSetup = null;

		while(lineScanner.hasNextLine()) {
			String line = lineScanner.nextLine();
			Scanner wordScanner = new Scanner(line);
			String word;
			switch(mode) {
			case NUM_ITEMS:
				word = wordScanner.next(); 
				assert(word.equals("Items"));
				numItems = wordScanner.nextInt();
				instance.setNumItems(numItems);
				mode++;
				break;
			case NUM_PERIODS:
				word = wordScanner.next(); 
				assert(word.equals("Periods"));
				numPeriods = wordScanner.nextInt();
				instance.setNumPeriods(numPeriods);
				mode++;
				break;
			case CAPACITIES:
				word = wordScanner.next();
				assert(word.equals("Capacity"));
				readCapacities(wordScanner, instance, fact);
				mode = EMPTY;
				break;
			case EMPTY:
				if(wordScanner.hasNext()) {
					// not empty any more, must be item
					word = wordScanner.next();
					assert(word.equals("Item"));
					currentItemID++;
					assert(currentItemID <= numItems);

					// initialize item arrays
					prodCost = new int[numPeriods];
					fixedCost = new int[numPeriods];
					holdCost = new int[numPeriods];
					demand = new int[numPeriods];
					capUsage = new int[numPeriods];
					capSetup = new int[numPeriods];

					mode = INIT_COST;
				}
				break;
			case INIT_COST:
				word = wordScanner.next();
				assert(word.equals("InitialCost"));
				initialCost = wordScanner.nextInt();
				mode++;
				break;
			case PROD_COST:
				word = wordScanner.next();
				assert(word.equals("ProductionCost"));
				// read production costs
				for(int i = 0; i<numPeriods; i++) {
					assert(wordScanner.hasNextInt());
					prodCost[i] = wordScanner.nextInt();
				}
				assert(!wordScanner.hasNext());
				mode++;
				break;
			case HOLD_COST:
				word = wordScanner.next();
				assert(word.equals("HoldingCost"));
				// read holding costs
				for(int i = 0; i<numPeriods; i++) {
					assert(wordScanner.hasNextInt());
					holdCost[i] = wordScanner.nextInt();
				}
				assert(!wordScanner.hasNext());
				mode++;
				break;
			case FIXD_COST:
				word = wordScanner.next();
				assert(word.equals("FixedCost"));
				// read fixed costs
				for(int i = 0; i<numPeriods; i++) {
					assert(wordScanner.hasNextInt());
					fixedCost[i] = wordScanner.nextInt();
				}
				assert(!wordScanner.hasNext());
				mode++;
				break;
			case DEMAND:
				word = wordScanner.next();
				assert(word.equals("Demand"));
				// read demand
				for(int i = 0; i<numPeriods; i++) {
					assert(wordScanner.hasNextInt());
					demand[i] = wordScanner.nextInt();
				}
				assert(!wordScanner.hasNext());
				mode++;
				break;
			case CAP_USAGE:
				word = wordScanner.next();
				assert(word.equals("CapUsage"));
				// read cap usage
				for(int i = 0; i<numPeriods; i++) {
					assert(wordScanner.hasNextInt());
					capUsage[i] = wordScanner.nextInt();
				}
				assert(!wordScanner.hasNext());
				mode++;
				break;
			case CAP_SETUP:
				word = wordScanner.next();
				assert(word.equals("CapSetup"));
				// read cap setup
				for(int i = 0; i<numPeriods; i++) {
					assert(wordScanner.hasNextInt());
					capSetup[i] = wordScanner.nextInt();
				}
				assert(!wordScanner.hasNext());
				mode = EMPTY;

				// create the currentItem


//				int initialCost = -1;
//				int[] prodCost = null;
//				int[] fixedCost = null;
//				int[] holdCost = null;
//				int[] demand = null;
//				int[] capUsage = null;
//				int[] capSetup = null;

				currentItem = createItem(currentItemID, initialCost, prodCost, fixedCost, holdCost, demand, capUsage, capSetup, fact);

				instance.getItem().add(currentItem);
				break;
			default:
				throw new Exception("Fucked up scanning tables and miller file");
			}
			wordScanner.close();
		}

		// close lineScanners
		lineScanner.close();

		return fact.createSclspInstance(instance);

	}

	private static Item createItem(int currentItemID, int initialCost, int[] prodCost, int[] fixedCost, int[] holdCost, int[] demand, int[] capUsage, int[] capSetup, ObjectFactory fact) {
		Item item = fact.createItem();
		item.setItemId(currentItemID);
		item.setInitialCost(initialCost);
		List<TimePeriod> timePeriods = item.getTimePeriod();
		for(int t=0; t<prodCost.length; t++) {
			TimePeriod timePeriod = fact.createTimePeriod();
			timePeriod.setTimePeriod(t+1);

			// set prod
			Value prodVal = fact.createValue();
			prodVal.setConstantValue(prodCost[t]);
			timePeriod.setProductionCost(prodVal);

			// set fixed
			Value fixedVal = fact.createValue();
			fixedVal.setConstantValue(fixedCost[t]);
			timePeriod.setFixedCost(fixedVal);

			// set hold
			Value holdingVal = fact.createValue();
			holdingVal.setConstantValue(holdCost[t]);
			timePeriod.setHoldingCost(holdingVal);

			// set demand
			Value demandVal = fact.createValue();
			demandVal.setConstantValue(demand[t]);
			timePeriod.setDemand(demandVal);

			// set cap usage
			Value capUsageVal = fact.createValue();
			capUsageVal.setConstantValue(capUsage[t]);
			timePeriod.setCapUsage(capUsageVal);

			// set cap setup
			Value capSetupVal = fact.createValue();
			capSetupVal.setConstantValue(capSetup[t]);
			timePeriod.setCapSetup(capSetupVal);
			timePeriods.add(timePeriod);
		}
		return item;
	}

	private static void readCapacities(Scanner wordScanner, SCLSPInstance instance, ObjectFactory fact) {
		List<Capacity> caps = instance.getCapacity();
		int time = 1;
		while(wordScanner.hasNext()) {
			assert(wordScanner.hasNextInt());
			int cap = wordScanner.nextInt();
			Capacity capacity = fact.createCapacity();
			// set time period
			capacity.setTimePeriod(time);
			// set capacity value
			Value value = fact.createValue();
			value.setConstantValue(cap);
			capacity.setValue(value);
			time++;
			caps.add(capacity);
		}
	}
}
