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
	
	private JSlider speedSlider;
	private JSlider eulerSlider;
	
	private int speedSlowest = 0;
	private int speedFastest = 100;
 
	private int eulerSlowest = 0;
	private int eulerFastest = 52;
	private int eulerDefaultSpeed = 25;
	
	public SpeedPanel()
	{
		super(new GridBagLayout());
		setBackground(Color.WHITE);
	}
	
	private ARDrone.ISpeedListener speedListener = new ARDrone.ISpeedListener() {
		
		public void speedUpdated(int speed)
		{
			speedSlider.setValue(speed);
		}
	};
	
	public void activate(final IARDrone drone)
	{
		this.drone = drone;
		
		speedSlider = new JSlider(JSlider.VERTICAL, speedSlowest, speedFastest, drone.getSpeed());
		speedSlider.setPaintTicks(true);
		speedSlider.setPaintLabels(true);
		speedSlider.setMinorTickSpacing(10);
		speedSlider.setMajorTickSpacing(25);
		speedSlider.setBackground(Color.WHITE);
		
		speedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				drone.setSpeed(speedSlider.getValue());
			}
		});
		
		eulerSlider = new JSlider(JSlider.VERTICAL, eulerSlowest, eulerFastest, eulerDefaultSpeed);
		eulerSlider.setPaintTicks(true);
		eulerSlider.setPaintLabels(true);
		eulerSlider.setMinorTickSpacing(5);
		eulerSlider.setMajorTickSpacing(10);
		eulerSlider.setBackground(Color.WHITE);
		
		eulerSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				drone.getCommandManager().setMaxEulerAngle(eulerSlider.getValue() / 100f);
			}
		});
		
		
		add(speedSlider, new GridBagConstraints(0,0,1,1,0.5,1,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.VERTICAL, new Insets(0,0,5,0), 0, 0));
		add(new JLabel("Speed in %"), new GridBagConstraints(0,1,1,1,0.5,0,GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(0,0,5,0), 0, 0));
		
		add(eulerSlider, new GridBagConstraints(1,0,1,1,0.5,1,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.VERTICAL, new Insets(0,0,5,0), 0, 0));
		add(new JLabel("Euler Angle"), new GridBagConstraints(1,1,1,1,0.5,0,GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(0,0,5,0), 0, 0));
		
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
		return new Dimension(150, 200);
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
