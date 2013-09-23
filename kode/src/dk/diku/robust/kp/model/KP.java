package dk.diku.robust.kp.model;

public class KP {
	private int items;
	private int capacity;
	private int[] profits;
	private int[] weights;
	
	public KP(int items, int capacity, int maxProfit, int maxWeight) {
		super();
        int N = items; // number of items
        int P = maxProfit; // maximum profit
        int W = maxWeight; // maximum weight
        this.profits = new int[N+1];
        this.weights = new int[N+1];

        // generate random instance, items 1..N
        for (int n = 1; n <= N; n++) {
            this.profits[n] = (int) (Math.random() * P);
            this.weights[n] = (int) (Math.random() * W);
        }
		this.items = items;
		this.capacity = capacity;
	}
	
	public KP() {
		super();
		this.items = 0;
		this.capacity = 0;
		this.profits = null;
		this.weights = null;
	}
	

	/**
	 * @param items
	 * @param capacity
	 * @param profits an array containing all profits. This is to be 1-indexed
	 * @param weights an array containing all weights. This is to be 1-indexed
	 */
	public KP(int items, int capacity, int[] profit, int[] weight) {
		super();
		this.items = items;
		this.capacity = capacity;
		this.profits = new int[profit.length+1];
		this.weights = new int[weight.length+1];
		this.profits[0] = 0;
		this.weights[0] = 0;
		for(int i = 1; i<=profit.length;i++){
			this.profits[i] = profit[i-1];
			this.weights[i] = weight[i-1];
		}
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getItems() {
		return items;
	}

	public void setItems(int items) {
		this.items = items;
	}

	public int[] getProfits() {
		return profits;
	}

	public void setProfits(int[] profits) {
		this.profits = profits;
	}

	public int[] getWeights() {
		return weights;
	}

	public void setWeights(int[] weights) {
		this.weights = weights;
	}
	public int getProfit(int i) {
		return profits[i];
	}

	public void setProfit(int i, int profit) {
		this.profits[i] = profit;
	}

	public int getWeight(int i) {
		return weights[i];
	}

	public void setWeight(int i, int weight) {
		this.weights[i] = weight;
	}
	public int getMaxWeight(){
		int result = 0;
		for(int i:weights){
			result+=i;
		}
		return result;
	}
}
