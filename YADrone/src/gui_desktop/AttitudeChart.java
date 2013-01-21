package gui_desktop;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.Date;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

/**
 * @author dbade
 * 
 * Domain = x-axis, range = y-axis
 *
 */
public class AttitudeChart
{
	protected JFreeChart chart;
	protected TimeSeriesCollection dataset;
	
	private TimeSeries yawSeries;
	private TimeSeries pitchSeries;
	private TimeSeries rollSeries;
	
	/**
	 * Create a new instance of this chart.
	 */
	public AttitudeChart()
	{
		this.yawSeries = new TimeSeries("Yaw", "Degree", "Time");
		this.pitchSeries = new TimeSeries("Pitch", "Degree", "Time");
		this.rollSeries = new TimeSeries("Roll", "Degree", "Time");

		yawSeries.setMaximumItemCount(500);
		pitchSeries.setMaximumItemCount(500);
		rollSeries.setMaximumItemCount(500);
		
		dataset = new TimeSeriesCollection();
		dataset.addSeries(this.yawSeries);
		dataset.addSeries(this.pitchSeries);
		dataset.addSeries(this.rollSeries);
		
		createChart("Time", "Degree", "Yaw / Pitch / Roll", true);
	}

	/**
	 * Update the chart.
	 * @param tuple  The tuple containing the current signal level.
	 */
	public void setAttitude(float pitch, float roll, float yaw)
	{
		Millisecond ms = new Millisecond(new Date());
		yawSeries.addOrUpdate(ms, yaw);
		pitchSeries.addOrUpdate(ms, pitch);
		rollSeries.addOrUpdate(ms, roll);
	}
	
	protected void createChart(String domainAxisTitle, String rangeAxisTitle, String chartsTitle, boolean includeLegend) 
	{
		DateAxis domain = new DateAxis(domainAxisTitle);
		NumberAxis range = new NumberAxis(rangeAxisTitle);
		
		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setSeriesPaint(0, Color.red);
		renderer.setSeriesPaint(1, Color.green);
		renderer.setSeriesPaint(2, Color.blue);
		renderer.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		
		XYPlot plot = new XYPlot(dataset, domain, range, renderer);
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		
		domain.setAutoRange(true);
		domain.setLowerMargin(0.0);
		domain.setUpperMargin(0.0);
		domain.setTickLabelsVisible(true);
		
		range.setAutoRange(false);
		range.setUpperBound(90);
		range.setLowerBound(-90);
		range.setAutoRangeIncludesZero(true);
		range.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		
		chart = new JFreeChart(plot);
		chart.setBackgroundPaint(Color.white);
    }
	
	/**
	 * Get the chart object.
	 * @return  The object to be displayed on a panel.
	 */
	public JFreeChart getChart()
	{
		return chart;
	}
}
