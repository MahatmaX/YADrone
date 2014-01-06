package de.yadrone.apps.controlcenter.plugins.altitude;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;

import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.IARDrone;
import de.yadrone.base.configuration.ConfigurationListener;
import de.yadrone.base.navdata.Altitude;
import de.yadrone.base.navdata.AltitudeListener;

public class AltitudeChartPanel extends JPanel implements ICCPlugin
{
	private IARDrone drone;
	
	private AltitudeChart chart;
	
	public AltitudeChartPanel()
	{
		super(new GridBagLayout());
		
		this.chart = new AltitudeChart();
		JPanel chartPanel = new ChartPanel(chart.getChart(), true, true, true, true, true);
		
		add(chartPanel, new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0,0,5,0), 0, 0));
	}
	
	private AltitudeListener altitudeListener = new AltitudeListener() {

		public void receivedAltitude(int altitude)
		{
			chart.setAltitude(altitude);
		}

		public void receivedExtendedAltitude(Altitude altitude) { }
		
	};
	
	public void activate(IARDrone drone)
	{
		this.drone = drone;
		
		drone.getNavDataManager().addAltitudeListener(altitudeListener);
	}

	public void deactivate()
	{
		drone.getNavDataManager().removeAltitudeListener(altitudeListener);
	}

	public String getTitle()
	{
		return "Altitude Chart";
	}
	
	public String getDescription()
	{
		return "Displays a chart with the latest altitude";
	}

	public boolean isVisual()
	{
		return true;
	}

	public Dimension getScreenSize()
	{
		return new Dimension(330, 250);
	}
	
	public Point getScreenLocation()
	{
		return new Point(330, 390);
	}

	public JPanel getPanel()
	{
		return this;
	}
}
