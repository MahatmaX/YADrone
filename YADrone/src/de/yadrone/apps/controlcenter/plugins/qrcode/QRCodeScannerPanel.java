package de.yadrone.apps.controlcenter.plugins.qrcode;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.IARDrone;
import de.yadrone.base.video.ImageListener;

public class QRCodeScannerPanel extends JPanel implements ICCPlugin
{
	private IARDrone drone;
	private String code;
	private String orientation;
	
	private BufferedImage image = null;
	private Result detectionResult;

	public QRCodeScannerPanel()
	{
		setBackground(Color.BLACK);
//		// might also be (1280*720)
//		Dimension dim = new Dimension(640, 360);
//		
//		setMinimumSize(dim);
//		setMaximumSize(dim);
//		setSize(dim);
		
//		addMouseListener(new MouseAdapter() {
//			public void mouseClicked(MouseEvent e)
//			{
//				// toggle camera view from horizontal to vertical
//				drone.toggleCamera();
//			}
//		});
	}
	
	private long imageCount = 0;
	
	private void setImage(final BufferedImage image)
	{
		this.image = image;
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				repaint();
			}
		});
		
		// try to detect QR code
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		// decode the barcode (if only QR codes are used, the QRCodeReader might be a better choice)
		MultiFormatReader reader = new MultiFormatReader();

		try
		{
			detectionResult = reader.decode(bitmap);
			
			code = detectionResult.getText();
			
			// System.out.println("QRCode Text: " + result.getText());

			// for (int i=0; i < points.length; i++)
			// {
			// System.out.println("QRCode Point # " + i + ": " +
			// points[i].getX() + "/" + points[i].getY());
			// }

			ResultPoint[] points = detectionResult.getResultPoints();
			ResultPoint a = points[1]; // top-left
			ResultPoint b = points[2]; // top-right
			ResultPoint c = points[0]; // bottom-left
			ResultPoint d = points[3]; // alignment point (bottom-right)
			
			// Find the degree of the rotation that is needed

			double z = Math.abs(a.getX() - b.getX());
			double x = Math.abs(a.getY() - b.getY());
			double theta = Math.atan(x / z); // degree in rad (+- PI/2)

			theta = theta * (180 / Math.PI); // convert to degree

			if ((b.getX() < a.getX()) && (b.getY() > a.getY()))
			{ // code turned more than 90° clockwise
				theta = 180 - theta;
			}
			else if ((b.getX() < a.getX()) && (b.getY() < a.getY()))
			{ // code turned more than 180° clockwise
				theta = 180 + theta;
			}
			else if ((b.getX() > a.getX()) && (b.getY() < a.getY()))
			{ // code turned more than 270 clockwise
				theta = 360 - theta;
			}
			
			orientation = (int)theta + " °";
		} 
		catch (ReaderException e) 
		{
			// no code found.
			detectionResult = null;
			orientation = "n/a °";
			code = "n/a";
		}
	}

	public void paint(Graphics g)
	{
		if (image != null)
		{
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
			if (detectionResult != null)
			{
				ResultPoint[] points = detectionResult.getResultPoints();
				ResultPoint a = points[1]; // top-left
				ResultPoint b = points[2]; // top-right
				ResultPoint c = points[0]; // bottom-left
				ResultPoint d = points[3]; // alignment point (bottom-right)
				
				g.setColor(Color.GREEN);
				
				g.drawPolygon(new int[] {(int)a.getX(),(int)b.getX(),(int)d.getX(),(int)c.getX()}, 
						      new int[] {(int)a.getY(),(int)b.getY(),(int)d.getY(),(int)c.getY()}, 4);
				
				g.setColor(Color.RED);
				g.setFont(new Font("SansSerif", Font.BOLD, 14));
				g.drawString(code, (int)a.getX(), (int)a.getY());
				g.drawString(orientation, (int)a.getX(), (int)a.getY() + 20);
			}
		}
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
		drone.getVideoManager().removeImageListener(null);
	}

	public String getTitle()
	{
		return "QRCode Scanner";
	}
	
	public String getDescription()
	{
		return "Scan for QR Codes (or other kinds of (bar)codes) in the current video stream";
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
		return new Point(100, 100);
	}

	public JPanel getPanel()
	{
		return this;
	}
}
