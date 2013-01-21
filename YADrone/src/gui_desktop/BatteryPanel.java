package gui_desktop;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;

public class BatteryPanel extends JPanel
{
	private Font font = new Font("Helvetica", Font.PLAIN, 10);
	private int batteryLevel = 100;
	
	public BatteryPanel()
	{
		setSize(20, 60);
	}
	
	public void setBatteryLevel(int batteryLevel)
	{
		if (batteryLevel != this.batteryLevel)
		{
			this.batteryLevel = batteryLevel;
			repaint();
		}
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
	}
}
