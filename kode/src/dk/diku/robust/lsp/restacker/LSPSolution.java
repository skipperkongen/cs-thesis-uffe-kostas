package dk.diku.robust.lsp.restacker;

import dk.diku.robust.lsp.simulation.SimConclusion;

public class LSPSolution {
	
	public int[] initialStock;
	public int[][] production;
	public SimConclusion conclusion;

	public LSPSolution(int[] initialStock, int[][] production) {
		super();
		this.initialStock = initialStock;
		this.production = production;
	}
	

}
