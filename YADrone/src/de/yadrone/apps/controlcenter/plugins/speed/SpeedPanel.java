package de.yadrone.apps.controlcenter.plugins.speed;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;

public class SpeedPanel extends JPanel implements ICCPlugin
{
	private IARDrone drone;
	
	private JSlider slider;
	
	private int slowest = 0;
	private int fastest = 100;
 
	public SpeedPanel()
	{
		super(new GridBagLayout());
		setBackground(Color.WHITE);
	}
	
	private ARDrone.ISpeedListener speedListener = new ARDrone.ISpeedListener() {
		
		public void speedUpdated(int speed)
		{
			slider.setValue(speed);
		}
	};
	
	public void activate(final IARDrone drone)
	{
		this.drone = drone;
		
		slider = new JSlider(JSlider.VERTICAL, slowest, fastest, drone.getSpeed());
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setMinorTickSpacing(10);
		slider.setMajorTickSpacing(30);
		slider.setBackground(Color.WHITE);
		
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				drone.setSpeed(slider.getValue());
			}
		});
		
		add(slider, new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0,0,5,0), 0, 0));
		add(new JLabel("Speed"), new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(0,0,5,0), 0, 0));
		
		drone.addSpeedListener(speedListener);
	}

	public void deactivate()
	{
		drone.removeSpeedListener(speedListener);
	}

	public String getTitle()
	{
		return "Speed Control";
	}
	
	public String getDescription()
	{
		return "Small panel to control the drone's speed.";
	}

	public boolean isVisual()
	{
		return true;
	}

	public Dimension getScreenSize()
	{
		return new Dimension(60, 200);
	}
	
	public Point getScreenLocation()
	{
		return new Point(890, 120);
	}

	public JPanel getPanel()
	{
		return this;
	}
}
