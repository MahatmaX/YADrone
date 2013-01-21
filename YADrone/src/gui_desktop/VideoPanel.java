package gui_desktop;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.shigeodayo.ardrone.ARDrone;

public class VideoPanel extends JPanel
{
	private BufferedImage image = null;

	public VideoPanel(final ARDrone drone)
	{
		setBackground(Color.BLACK);
		// might also be (1280*720)
		Dimension dim = new Dimension(640, 360);
		
		setMinimumSize(dim);
		setMaximumSize(dim);
		setSize(dim);
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e)
			{
				// toggle camera view from horizontal to vertical
				drone.toggleCamera();
			}
		});
	}
	
	public void setImage(final BufferedImage image)
	{
		this.image = image;
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				repaint();
			}
		});
	}

	public void paint(Graphics g)
	{
		if (image != null)
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		else
		{
			g.setColor(Color.WHITE);
			g.drawString("Waiting for Video ...", 10, 20);
		}
	}
}
