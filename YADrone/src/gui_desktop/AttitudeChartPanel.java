package gui_desktop;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;

public class AttitudeChartPanel extends JPanel
{
	private AttitudeChart chart;
	
	public AttitudeChartPanel()
	{
		super(new GridBagLayout());
		
		this.chart = new AttitudeChart();
		JPanel chartPanel = new ChartPanel(chart.getChart(), true, true, true, true, true);
		
		add(chartPanel, new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0,0,5,0), 0, 0));
	}
	
	public void setAttitude(float pitch, float roll, float yaw)
	{
		chart.setAttitude(pitch, roll, yaw);
	}
}
