package dk.diku.robust.lsp.restacker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dk.diku.robust.autogen.sclsp_experiment.Experiment;
import dk.diku.robust.autogen.sclsp_instance.SCLSPInstance;

public class RestackerOperator {
	
	Experiment exp; 
	SCLSPInstance instance;
	List<Integer> itemIDs;
	Integer[] assignments;
	
	public RestackerOperator(Experiment exp, SCLSPInstance instance) {
		super();
		this.exp = exp;
		this.instance = instance;
		itemIDs = new ArrayList<Integer>();
		for(int i=1; i<=instance.getNumItems();i++) {
			itemIDs.add(i);
		}
	}

	public void restackItem(int[] initialStock, int[][]production, int item) {
		
	}
	
	public void restack(LSPSolution solution) {
		Collections.shuffle(itemIDs);
	}

}
