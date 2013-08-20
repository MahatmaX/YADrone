package de.yadrone.apps.controlcenter.plugins.battery;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;


import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.BatteryListener;

public class BatteryPanel extends JPanel implements ICCPlugin
{
	private IARDrone drone;
	
	private Font font = new Font("Helvetica", Font.PLAIN, 10);
	private int batteryLevel = 100;
	private int voltageLevel;
	
	public BatteryPanel()
	{
		setSize(20, 60);
	}
	
	public void paint(Graphics g)
	{
		g.setColor(Color.black);
		g.drawRect(0, 0, this.getWidth(), this.getHeight());

		Color c;
		if (batteryLevel >= 50)
		{
			c = new Color((Math.abs(batteryLevel-100f)/100f) * 2f, 1f, 0f);
		}
		else
		{
			c = new Color(1f, (batteryLevel/100f) * 2f, 0f);
		}
		
		g.setColor(c);
		g.fillRect(0, getHeight() * (batteryLevel / 100), this.getWidth(), this.getHeight());
		
		FontMetrics metrics = g.getFontMetrics(font);
		int hgt = metrics.getHeight();
		g.setFont(font);
		
		g.setColor(Color.black);
		g.drawString("Battery", (this.getWidth() / 2) - (metrics.stringWidth("Battery") / 2), (this.getHeight() / 2) - (hgt / 2));
		g.drawString(batteryLevel + " %", (this.getWidth() / 2) - (metrics.stringWidth(batteryLevel + " %") / 2), (this.getHeight() / 2) + (hgt / 2));
		g.drawString(voltageLevel + " V", (this.getWidth() / 2) - (metrics.stringWidth(voltageLevel + " V") / 2), (int)((this.getHeight() / 2) + ((hgt / 2) * 2.5)));
	}

	private BatteryListener batteryListener = new BatteryListener() {
		
		public void voltageChanged(int vbat_raw)
		{
			
		}
		
		public void batteryLevelChanged(int batteryLevel)
		{
			if (batteryLevel != BatteryPanel.this.batteryLevel)
			{
				BatteryPanel.this.batteryLevel = batteryLevel;
				repaint();
			}
		}
	};
	
	public void activate(IARDrone drone)
	{
		this.drone = drone;
		drone.getNavDataManager().addBatteryListener(batteryListener);
	}

	public void deactivate()
	{
		drone.getNavDataManager().removeBatteryListener(batteryListener);
	}

	public String getTitle()
	{
		return "Battery";
	}
	
	public String getDescription()
	{
		return "Displays current battery and voltage levels";
	}

	public boolean isVisual()
	{
		return true;
	}

	public Dimension getScreenSize()
	{
		return new Dimension(60, 120);
	}
	
	public Point getScreenLocation()
	{
		return new Point(890, 0);
	}

	public JPanel getPanel()
	{
		return this;
	}
}
