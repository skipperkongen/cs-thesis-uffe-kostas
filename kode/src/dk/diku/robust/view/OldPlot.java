package dk.diku.robust.view;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class OldPlot {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5910141051003966565L;

	private static final int GEN_SIZE = 50;

	private static final String ROBUST_PREFIX = "R";
	private static final String VALUES_PREFIX = "V";

	private static final int DOT_WIDTH = 5;
	private static final int DOT_HEIGHT = 5;

	private static final String FAT_SOLUTION_KEY = "Fat solution";
	private static final String GA_SOLUTION_KEY = "GA solution";

	private int genSize;
	private XYSeriesCollection dataSetRobustness;
	private XYSeriesCollection dataSetValues;
	private XYSeriesCollection bestValues;
	private int generation;
	private int generationBest;
	private ApplicationFrame frameR;
	private ApplicationFrame frameV;
	private ApplicationFrame frameB;

	public OldPlot(int genSize) {
		super();
		this.genSize = genSize;
		// initialize data sets
		initializeDataSets();
		// initialize chart frames
		initializeFrames();
		//
		generation = 0;
		generationBest = 0;

	}

	public void initializeFrames() {
		// create the frames
		frameR = new ApplicationFrame("Robustness");
		frameV = new ApplicationFrame("Solution values");
		frameB = new ApplicationFrame("GA versus fat solution");

		// create charts, robustness
		JFreeChart chartR = ChartFactory.createXYLineChart(
				"Robustness per individual", 
				"Generation", 
				"Robustness", 
				dataSetRobustness, 
				PlotOrientation.VERTICAL, 
				false, 
				false, 
				false);

		JFreeChart chartV = ChartFactory.createXYLineChart(
				"Solution value per individual", 
				"Generation", 
				"Value", 
				dataSetValues, 
				PlotOrientation.VERTICAL, 
				false, 
				false, 
				false);
		
		JFreeChart chartB = ChartFactory.createXYLineChart(
				"GA versus fat solution", 
				"Generation", 
				"Value",
				bestValues, 
				PlotOrientation.VERTICAL, 
				true,
				false, 
				false);
		// set renderer

		XYPlot plotR = (XYPlot)chartR.getPlot();
		XYPlot plotV = (XYPlot)chartV.getPlot();
		XYPlot plotB = (XYPlot)chartB.getPlot();
		//XYItemRenderer renderer = getDefaultRenderer();
		//plotR.setRenderer(renderer);
		//plotV.setRenderer(renderer);
		
		// set background color
		plotR.setBackgroundPaint(new Color(0x00EEEEEE));
		plotV.setBackgroundPaint(new Color(0x00EEEEEE));
		plotB.setBackgroundPaint(new Color(0x00EEEEEE));

		// create panels
		ChartPanel panelR = new ChartPanel(chartR);
		panelR.setPreferredSize(new java.awt.Dimension(500, 270));
		panelR.setMouseZoomable(true, false);

		ChartPanel panelV = new ChartPanel(chartV);
		panelV.setPreferredSize(new java.awt.Dimension(500, 270));
		panelV.setMouseZoomable(true, false);
		
		ChartPanel panelB = new ChartPanel(chartB);
		panelB.setPreferredSize(new java.awt.Dimension(500, 270));
		panelB.setMouseZoomable(true, false);

		// set charts in frames
		frameR.setContentPane(panelR);
		frameV.setContentPane(panelV);
		frameB.setContentPane(panelB);
	}

	private XYItemRenderer getDefaultRenderer() {
		XYItemRenderer renderer = null;
		//renderer = new XYDotRenderer();
		//renderer.setDotWidth(DOT_WIDTH);
		//renderer.setDotHeight(DOT_HEIGHT);
		
		// renderer = new HighLowRenderer();
		// renderer = new XYBlockRenderer();
		//renderer = new VectorRenderer();
		renderer = new XYLine3DRenderer();
		return renderer;
	}

	private void initializeDataSets() {
		// create data sets
		dataSetRobustness = new XYSeriesCollection();
		dataSetValues = new XYSeriesCollection();
		for(int i = 0; i<genSize; i++) {
			XYSeries sR = new XYSeries(ROBUST_PREFIX+i);
			dataSetRobustness.addSeries(sR);
			XYSeries sV = new XYSeries(VALUES_PREFIX+i);
			dataSetValues.addSeries(sV);
		}
		// fat solution versus our best
		bestValues = new XYSeriesCollection();
		bestValues.addSeries(new XYSeries(FAT_SOLUTION_KEY));
		bestValues.addSeries(new XYSeries(GA_SOLUTION_KEY));
	}

	public void show() {
		frameR.pack();
		RefineryUtilities.centerFrameOnScreen(frameR);
		frameR.setVisible(true);

		frameV.pack();
		RefineryUtilities.centerFrameOnScreen(frameV);
		frameV.setVisible(true);	
		
		frameB.pack();
		RefineryUtilities.centerFrameOnScreen(frameB);
		frameB.setVisible(true);	
	}

	public void hide() {
		frameR.setVisible(false);
		frameV.setVisible(false);
		frameB.setVisible(false);
	}

	public synchronized void plotGeneration(double[] robustness, double[] values) {
		for(int i=0; i<genSize; i++) {
			XYSeries sR = dataSetRobustness.getSeries(ROBUST_PREFIX+i);
			sR.add(generation, robustness[i]);
			XYSeries sV = dataSetValues.getSeries(VALUES_PREFIX+i);
			sV.add(generation, values[i]);
		}
		generation++;
	}
	
	public synchronized void plotValuesFatVersusGA(double fatSolution, double gaSolution) {
		bestValues.getSeries(FAT_SOLUTION_KEY).add(generationBest, fatSolution);
		bestValues.getSeries(GA_SOLUTION_KEY).add(generationBest, gaSolution);
		generationBest++;
	}
}
