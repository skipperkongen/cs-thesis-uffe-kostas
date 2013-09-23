package dk.diku.robust.kp.model;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.impl.BooleanGene;


public class KPSolution {
	private boolean[] itemSelected;
	private KP kp;
	
	public KPSolution(){
		
	}
	
	public KPSolution(KP kp, boolean[] selected){
		itemSelected = selected;
		this.kp = kp;
	}

	public int getUsedCapacity() {
		int sum = 0;
		for(int i = 0; i<kp.getProfits().length;i++){
			if(itemSelected[i]){
				sum+=kp.getWeights()[i];
			}
		}
		return sum;
	}
	
	public int getTotalProfit() {
		int sum = 0;
		for(int i = 0; i<kp.getProfits().length;i++){
			if(itemSelected[i]){
				sum+=kp.getProfits()[i];
			}
		}
		return sum;
	}

	public boolean[] getItemSelected() {
		return itemSelected;
	}

	public void setItemSelected(boolean[] itemSelected) {
		this.itemSelected = itemSelected;
	}

	public KP getKp() {
		return kp;
	}

	public void setKp(KP kp) {
		this.kp = kp;
	}
	
	public IChromosome getChromosome(Configuration conf) throws Exception {
		Gene[] genes = new Gene[kp.getItems()];
		for(int i=0; i<kp.getItems(); i++) {
			genes[i] = new BooleanGene(conf);
			genes[i].setAllele(new Boolean(itemSelected[i]));
		}
		Chromosome chrom = new Chromosome(conf, genes);
		return chrom;
	}
}
