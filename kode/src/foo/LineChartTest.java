package foo;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class LineChartTest extends ApplicationFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static double[] data = {6,1,5,2,3,9,5,5,6,-6,3,2,1,5,14,0,1,7};

	public LineChartTest(String title) {
		super(title);
		ChartPanel chartPanel = createDemoPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        chartPanel.setMouseZoomable(true, false);
        setContentPane(chartPanel);
	}

	private static ChartPanel createDemoPanel() {
		JFreeChart chart = createChart(createDataset());
		return new ChartPanel(chart);
	}

	private static JFreeChart createChart(XYDataset dataset) {
		JFreeChart chart = ChartFactory.createXYAreaChart(
				"Random data",  // title
				"x value",             // x-axis label
				"y value",   // y-axis label
				dataset,            // data
				PlotOrientation.VERTICAL,
				true,               // create legend?
				true,               // generate tooltips?
				false               // generate URLs?
		);

		return chart;
	}

	private static XYDataset createDataset() {
		XYSeries s1 = new XYSeries("line");
		XYSeries s2 = new XYSeries("square");
		XYSeries s3 = new XYSeries("cube");
		XYSeries s4 = new XYSeries("next thing");
		for(int i=0; i<data.length; i++) {
			s1.add(i, data[i]);
			s2.add(i, data[i]*data[i]);
			s3.add(i, data[i]*data[i]*data[i]);
			s4.add(i, data[i]*data[i]*data[i]*data[i]);
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(s1);
		dataset.addSeries(s2);
		dataset.addSeries(s3);
		dataset.addSeries(s4);
		return dataset;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LineChartTest demo = new LineChartTest(
		"Random data");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

	}
}
