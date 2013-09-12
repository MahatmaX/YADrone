package de.yadrone.apps.controlcenter.plugins.altitude;

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
public class AltitudeChart
{
	protected JFreeChart chart;
	protected TimeSeriesCollection dataset;
	
	private TimeSeries altitudeSeries;
	
	/**
	 * Create a new instance of this chart.
	 */
	public AltitudeChart()
	{
		this.altitudeSeries = new TimeSeries("Altitude", "Millimeter", "Time");

		altitudeSeries.setMaximumItemCount(1000);
		
		dataset = new TimeSeriesCollection();
		dataset.addSeries(this.altitudeSeries);
		
		createChart("Time", "Millimeter", "Altitude", true);
	}

	/**
	 * Update the chart.
	 * @param tuple  The tuple containing the current signal level.
	 */
	public void setAltitude(int altitude)
	{
		Millisecond ms = new Millisecond(new Date());
		altitudeSeries.addOrUpdate(ms, altitude);
	}
	
	protected void createChart(String domainAxisTitle, String rangeAxisTitle, String chartsTitle, boolean includeLegend) 
	{
		DateAxis domain = new DateAxis(domainAxisTitle);
		NumberAxis range = new NumberAxis(rangeAxisTitle);
		
		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setSeriesPaint(0, Color.blue);
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
		
		range.setAutoRange(true);
//		range.setUpperBound(180);
//		range.setLowerBound(0);
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
