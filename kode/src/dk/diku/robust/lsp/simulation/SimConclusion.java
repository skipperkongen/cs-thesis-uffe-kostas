package dk.diku.robust.lsp.simulation;

public class SimConclusion {
	
	public boolean robust;
	public double robustnessLB;
	public double robustnessUB;
	
	public double averageDemandMet;
	public double averageDemandMissed;
	public double averageTotalDemand;
	
	public double averageUnrealizableProduction;

	public double averageInitialCost;
	public double averageCost;
	public double lowestCost;
	public double highestCost;

	public double averageUsedCapacity;
	public double averageAvailableCapacity;
	
	public int simulationsRun;
	public long runningTime;
	public long highestDemandMissed;


		
	public double getRobustnessSpan() {
		return Math.abs(robustnessUB - robustnessLB);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Conclusion\n" );
		sb.append("- Robust: " + robust + "\n");
		sb.append("- Robustness bounds: [" + robustnessLB + "," + robustnessUB + "]\n");
		sb.append("- Highest demand missed: " + highestDemandMissed + "\n");
		sb.append("- Average demand met: " + averageDemandMet + "\n");
		sb.append("- Average demand missed: " + averageDemandMissed + "\n");
		sb.append("- Average total demand: " + averageTotalDemand + "\n");
		sb.append("- Average unrealizable production: " + averageUnrealizableProduction + "\n");

		sb.append("- Average cost: " + averageCost + "\n");
		sb.append("- Average initial cost: " + averageInitialCost + "\n");
		sb.append("- Average cost excl. initial cost: " + (averageCost-averageInitialCost) + "\n");
		sb.append("- Highest cost: " + highestCost + "\n");
		
		sb.append("- Average used capacity: " + averageUsedCapacity + "\n");
		sb.append("- Average available capacity: " + averageAvailableCapacity + "\n");
		
		sb.append("- Simulations performed: " + simulationsRun + "\n");
		sb.append("- Running time: " + runningTime + " ms\n");
		
		return sb.toString();
	}

}
