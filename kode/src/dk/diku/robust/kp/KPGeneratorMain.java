package dk.diku.robust.kp;

import java.io.FileOutputStream;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import dk.diku.robust.autogen.stochknapsack.DistributionType;
import dk.diku.robust.autogen.stochknapsack.ItemList;
import dk.diku.robust.autogen.stochknapsack.ItemType;
import dk.diku.robust.autogen.stochknapsack.NormalDistribution;
import dk.diku.robust.autogen.stochknapsack.ObjectFactory;
import dk.diku.robust.autogen.stochknapsack.StochasticKnapsackProblem;

/**
 * @author Uffe Christensen
 * Generates an XML-file conforming to the XML-Schema of a stochastic knapsack problem
 */
public class KPGeneratorMain {

	// Values for deciding the items
	private static int NUMBER_OF_ITEMS = 0;
	private static int MINIMUM_ITEM_WEIGHT = 1;
	private static int MAXIMUM_ITEM_WEIGHT = 2;
	private static int MINIMUM_ITEM_PROFIT = 3;
	private static int MAXIMUM_ITEM_PROFIT = 4;
	// This value is to decide how many percent 
	// to each side 95% of the probability mass can spread.
	private static int WEIGHT_SPREAD = 5;

	// Values for deciding the capacity
	private static int MEAN_CAPACITY = 6;

	// This value is to decide how many percent 
	// to each side 95% of the probability mass can spread.
	private static int DEVIATION_CAPACITY = 7;

	// Location of knapsack xsd auto-generated files
	private final static String PATH_KP_EXPERIMENT = "dk.diku.robust.autogen.stochknapsack";

	public static void main(String... args) throws Exception {
		int[] scenario = new int[8];
		// Values that are the same for all scenarios
		scenario[MINIMUM_ITEM_WEIGHT] = 20;
		scenario[MAXIMUM_ITEM_WEIGHT] = 29;
		scenario[MINIMUM_ITEM_PROFIT] = 16;
		scenario[MAXIMUM_ITEM_PROFIT] = 77;
		
		int spreadHigh = 50;
		int spreadLow = 10;
		String name;

		for(int i = 0; i<2;i++){
			scenario[NUMBER_OF_ITEMS] = 50+i*150;
			scenario[MEAN_CAPACITY] = 20*scenario[NUMBER_OF_ITEMS];
			for(int j = 0; j < 8;j++){
				switch (j){
					case 0: // Weight: high spread, Capacity: high spread
						scenario[WEIGHT_SPREAD] = spreadHigh;
						scenario[DEVIATION_CAPACITY] = spreadHigh;
						break;
					case 1: // Weight: high spread, Capacity: low spread
						scenario[WEIGHT_SPREAD] = spreadHigh;
						scenario[DEVIATION_CAPACITY] = spreadLow;
						break;
					case 2: // Weight: high spread, Capacity: non-stochastic
						scenario[WEIGHT_SPREAD] = spreadHigh;
						scenario[DEVIATION_CAPACITY] = 0;
						break;
					case 3: // Weight: low spread, Capacity: high spread
						scenario[WEIGHT_SPREAD] = spreadLow;
						scenario[DEVIATION_CAPACITY] = spreadHigh;
						break;
					case 4: // Weight: low spread, Capacity: low spread
						scenario[WEIGHT_SPREAD] = spreadLow;
						scenario[DEVIATION_CAPACITY] = spreadLow;
						break;
					case 5: // Weight: low spread, Capacity: non-stochastic
						scenario[WEIGHT_SPREAD] = spreadLow;
						scenario[DEVIATION_CAPACITY] = 0;
						break;
					case 6: // Weight: non-stochastic, Capacity: high spread
						scenario[WEIGHT_SPREAD] = 0;
						scenario[DEVIATION_CAPACITY] = spreadHigh;
						break;
					case 7: // Weight: non-stochastic, Capacity: low spread
						scenario[WEIGHT_SPREAD] = 0;
						scenario[DEVIATION_CAPACITY] = spreadLow;
						break;
				}
				name = "xml/kp_instances/kp_"+scenario[NUMBER_OF_ITEMS]+"_items_"+scenario[WEIGHT_SPREAD]+"_weightSpread_"+scenario[DEVIATION_CAPACITY]+"_capSpread.xml";
				System.out.println("\""+name+"\"");
				createScenario(scenario, name);
			}
		}



	}

	public static void createScenario(int[] scenario, String destOfXML) throws Exception{
		ObjectFactory fact = new ObjectFactory();
		StochasticKnapsackProblem inst = fact.createStochasticKnapsackProblem();

		// Setting the distribution for the capacity
		DistributionType stochCapDist = fact.createDistributionType();
		NormalDistribution normal = fact.createNormalDistribution();
		normal.setMean(scenario[MEAN_CAPACITY]);
		float devCap = (float)(((double)scenario[DEVIATION_CAPACITY]*scenario[MEAN_CAPACITY])/200);
		normal.setVariance((float)Math.pow(devCap,2.0));
		stochCapDist.setNormal(normal);

		// Adding the capacity distribution to the problem
		inst.setStochasticCapacity(stochCapDist);

		// Creating the items
		ItemList items = fact.createItemList();
		Random rand = new Random();
		for(int i=0;i<scenario[NUMBER_OF_ITEMS];i++){
			ItemType item = fact.createItemType();

			// Setting the mean weight of the item
			int meanWeight = scenario[MINIMUM_ITEM_WEIGHT] + rand.nextInt(scenario[MAXIMUM_ITEM_WEIGHT]-scenario[MINIMUM_ITEM_WEIGHT]+1);

			// Setting the deviation according to WEIGHT_SPREAD
			float deviationWeight = (float)(((double)scenario[WEIGHT_SPREAD]*meanWeight)/200);

			// Creating the distribution of the weight
			DistributionType weightDist = fact.createDistributionType();
			NormalDistribution weightNormal = fact.createNormalDistribution();
			weightNormal.setMean(meanWeight);
			weightNormal.setVariance((float)Math.pow(deviationWeight,2.0));
			weightDist.setNormal(weightNormal);
			//item.setWeight((float)meanWeight);
			item.setStochasticWeight(weightDist);

			// Setting the profit of the item. 
			// PROFIT IS NOT STOCHASTIC IN THIS SETUP
			int meanProfit = scenario[MINIMUM_ITEM_PROFIT] + rand.nextInt(scenario[MAXIMUM_ITEM_PROFIT]-scenario[MINIMUM_ITEM_PROFIT]+1);

			item.setProfit((float)meanProfit);

			items.getItem().add(item);
		}

		// Adding the list of items to the problem
		inst.setItems(items);

		ItemList temp = inst.getItems();

		System.out.println("Profit \t Weight");
		for(ItemType item: temp.getItem()){
			System.out.println(item.getProfit()+ "\t " + item.getStochasticWeight().getNormal().getMean());

		}
		System.out.println(" Capacity" + inst.getStochasticCapacity().getNormal().getMean());
		// Converting the problem to xml

		marshallKPScenario(fact.createKnapsack(inst), destOfXML);
	}


	public static void marshallKPScenario(JAXBElement<StochasticKnapsackProblem> scn, String path) throws Exception {
		JAXBContext jc = JAXBContext.newInstance( PATH_KP_EXPERIMENT );
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		FileOutputStream fos = new FileOutputStream(path);
		m.marshal( scn, fos );
		System.out.println("scenario-xml writen to " + path);
		fos.close();
	}

}
