package de.yadrone.apps.controlcenter.plugins.video;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.yadrone.base.IARDrone;
import de.yadrone.base.video.ImageListener;

public class VideoCanvas extends JPanel
{

	private BufferedImage image = null;
	private Image waitingImage = null;
	private long timestampLastUpdate = 0;
	private boolean showWaiting = true;
	
	public VideoCanvas(final IARDrone drone)
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
		
		waitingImage = new ImageIcon(VideoCanvas.class.getResource("hourglass.png")).getImage();
		
		// if for some time no video is received, a waiting screen shall be displayed
		new Thread(new Runnable() {

			public void run()
			{
				while(true)
				{
					showWaiting = System.currentTimeMillis() - timestampLastUpdate > 1000;
					SwingUtilities.invokeLater(new Runnable() {
						public void run()
						{
							repaint();
						}
					});
					try
					{
						Thread.sleep(500);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
			
		}).start();
	}
	
	private void setImage(final BufferedImage image)
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
		if ((image != null) && !showWaiting)
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		else
		{
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.drawImage(waitingImage, 20, 20, this);
			g.setColor(Color.BLACK);
			g.drawString("Waiting for Video ...", 120, 60);
			g.drawString("This could last up to one minute !", 120, 80);
			g.drawString("If afterwards no video is displayed, try to change codec !", 120, 100);
		}
	}
	
	public void imageUpdated(BufferedImage image)
	{
		timestampLastUpdate = System.currentTimeMillis();
		setImage(image);
	}
}
