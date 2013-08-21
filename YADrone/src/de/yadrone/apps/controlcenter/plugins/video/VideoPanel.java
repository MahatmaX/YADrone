package de.yadrone.apps.controlcenter.plugins.video;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.IARDrone;
import de.yadrone.base.video.ImageListener;

public class VideoPanel extends JPanel implements ICCPlugin
{
	private IARDrone drone;
	
	private BufferedImage image = null;

	public VideoPanel()
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
		if (image != null)
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		else
		{
			g.setColor(Color.WHITE);
			g.drawString("Waiting for Video ...", 10, 20);
		}
	}
	
	private ImageListener imageListener = new ImageListener() {
		public void imageUpdated(BufferedImage image)
		{
			setImage(image);
		}
	};
	
	public void activate(IARDrone drone)
	{
		this.drone = drone;
		drone.getVideoManager().addImageListener(imageListener);
	}

	public void deactivate()
	{
		drone.getVideoManager().removeImageListener(imageListener);
	}

	public String getTitle()
	{
		return "Video";
	}
	
	public String getDescription()
	{
		return "Displays the current video stream";
	}

	public boolean isVisual()
	{
		return true;
	}

	public Dimension getScreenSize()
	{
		return new Dimension(650, 390);
	}
	
	public Point getScreenLocation()
	{
		return new Point(0, 0);
	}

	public JPanel getPanel()
	{
		return this;
	}
}
