package dk.diku.robust.core.model;

public class SolutionData {
	
	public double robustnessLow;
	public double robustnessHigh;
	public double objectiveValue;
	public double fitnessValue;
	private Object solution;

	public SolutionData(double low, double high, double objectiveValue) {
		super();
		this.robustnessLow = low;
		this.robustnessHigh = high;
		this.objectiveValue = objectiveValue;
	}

	public SolutionData() {

	}

	public Object getCustom() {
		return solution;
	}

	public void setCustom(Object solution) {
		this.solution = solution;
	}
	
	

}
