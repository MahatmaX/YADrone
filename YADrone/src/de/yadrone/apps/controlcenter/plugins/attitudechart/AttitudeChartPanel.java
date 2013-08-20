package de.yadrone.apps.controlcenter.plugins.attitudechart;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;


import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.AttitudeListener;

public class AttitudeChartPanel extends JPanel implements ICCPlugin
{
	private IARDrone drone;
	
	private AttitudeChart chart;
	
	public AttitudeChartPanel()
	{
		super(new GridBagLayout());
		
		this.chart = new AttitudeChart();
		JPanel chartPanel = new ChartPanel(chart.getChart(), true, true, true, true, true);
		
		add(chartPanel, new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0,0,5,0), 0, 0));
	}
	
	private AttitudeListener attitudeListener = new AttitudeListener() {
		public void windCompensation(float pitch, float roll)
		{
			
		}
		
		public void attitudeUpdated(float pitch, float roll)
		{
			
		}
		
		public void attitudeUpdated(float pitch, float roll, float yaw)
		{
			chart.setAttitude(pitch / 1000, roll / 1000, yaw / 1000);
		}
	};
	
	public void activate(IARDrone drone)
	{
		this.drone = drone;
		
		drone.getNavDataManager().addAttitudeListener(attitudeListener);
	}

	public void deactivate()
	{
		drone.getNavDataManager().removeAttitudeListener(attitudeListener);
	}

	public String getTitle()
	{
		return "Attitude Chart";
	}
	
	public String getDescription()
	{
		return "Displays a chart with the latest pitch, roll and yaw";
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
