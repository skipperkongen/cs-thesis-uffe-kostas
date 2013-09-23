package dk.diku.robust.view;

import java.awt.Color;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public abstract class AbsMultiDoublePlot implements Observer {


	private static final String FRAME_TITLE = "Plot";

	private static final int BACKGROUND_COLOR = 0x00EEEEEE;

	private static final int PREF_DIM_X = 500;

	private static final int PREF_DIM_Y = 270;

	private HashSet<String> registeredKeys;
	private XYSeriesCollection dataSet;
	protected ApplicationFrame frame;
	protected XYPlot plot;
	protected JFreeChart chart;

	public AbsMultiDoublePlot(boolean showLegend) {
		super();
		createDataSet();
		createPlot(showLegend);
	}
	
	public void addBulk(String[] keys, int[] xs, double[] ys) {
		if(keys.length == xs.length && xs.length == ys.length) {
			for(int i=0; i<keys.length; i++) {
				addPoint(keys[i], xs[i], ys[i]);
			}
		}
	}
	
	public void addPoint(String key, int x, double y) {
		if(!registeredKeys.contains(key)) {
			addNewSeries(key);
			registeredKeys.add(key);
		}
		XYSeries s = dataSet.getSeries(key);
		s.add(x, y);
	}
	
	protected void addNewSeries(String key) {
		// add new series
		XYSeries s = new XYSeries(key);
		dataSet.addSeries(s);
	}

	public void show() {
		frame.pack();
		RefineryUtilities.centerFrameOnScreen(frame);
		frame.setVisible(true);
	}
	
	public void hide() {
		frame.setVisible(false);
	}
	
	private void createPlot(boolean showLegend) {
		frame = new ApplicationFrame(FRAME_TITLE);
		
		// create charts, robustness
		chart = ChartFactory.createXYLineChart(
				"Untitled", 
				"x", 
				"y", 
				dataSet, 
				PlotOrientation.VERTICAL, 
				showLegend, 
				false, 
				false);
		
		// modify plot
		plot = (XYPlot)chart.getPlot();
		plot.setBackgroundPaint(new Color(BACKGROUND_COLOR));
		
		// create panel, h8 swing...
		ChartPanel panel = new ChartPanel(chart);
		panel.setPreferredSize(new java.awt.Dimension(PREF_DIM_X, PREF_DIM_Y));
		panel.setMouseZoomable(true, false);
		
		// set charts in frames
		frame.setContentPane(panel);
	}
	
	public String getChartTitle() {
		return chart.getTitle().getText();
	}

	public void setChartTitle(String chartTitle) {
		chart.setTitle(chartTitle);
	}

	public String getXLabel() {
		return plot.getDomainAxis().getLabel();
	}

	public void setXLabel(String label) {
		plot.getDomainAxis().setLabel(label);
	}

	public String getYLabel() {
		return plot.getRangeAxis().getLabel();
	}

	public void setYLabel(String label) {
		plot.getRangeAxis().setLabel(label);
	}
	
	

	private void createDataSet() {
		registeredKeys = new HashSet<String>();
		// create data sets
		dataSet = new XYSeriesCollection();
	}
	

	//@Override
	public abstract void update(Observable o, Object arg);


}
