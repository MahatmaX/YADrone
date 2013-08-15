package de.yadrone.apps.controlcenter;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JPanel;

import de.yadrone.base.IARDrone;

public interface ICCPlugin
{

	public void activate(IARDrone drone);
	public void deactivate();
	
	public String getTitle();
	public String getDescription();
	
	public boolean isVisual();
	public Dimension getScreenSize();
	public Point getScreenLocation();
	public JPanel getPanel();
}
