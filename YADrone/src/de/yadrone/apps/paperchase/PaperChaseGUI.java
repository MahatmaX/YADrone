package de.yadrone.apps.paperchase;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import de.yadrone.base.IARDrone;
import de.yadrone.base.video.ImageListener;

public class PaperChaseGUI extends JFrame implements ImageListener, TagListener
{
	private Font font = new Font("SansSerif", Font.BOLD, 14);
	
	private BufferedImage image = null;
	private Result result;
	private String orientation;
	
	private JPanel contentPane;
	
	
	public PaperChaseGUI(final IARDrone drone)
	{
		super("YADrone Paper Chase");
        
        setSize(PaperChase.IMAGE_WIDTH, PaperChase.IMAGE_HEIGHT);
        setVisible(true);
        
        addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				drone.stop();
				System.exit(0);
			}
		});
        
        contentPane = new JPanel() {
        	public void paint(Graphics g)
        	{
        		if (image != null)
        		{
        			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        			
        			// draw tolerance field
        			g.setColor(Color.RED);
    				
    				int imgCenterX = PaperChase.IMAGE_WIDTH / 2;
    				int imgCenterY = PaperChase.IMAGE_HEIGHT / 2;
    				int tolerance = PaperChase.TOLERANCE;
    				
    				g.drawPolygon(new int[] {imgCenterX-tolerance, imgCenterX+tolerance, imgCenterX+tolerance, imgCenterX-tolerance}, 
						      		  new int[] {imgCenterY-tolerance, imgCenterY-tolerance, imgCenterY+tolerance, imgCenterY+tolerance}, 4);
    				
    				
        			if (result != null)
        			{
        				ResultPoint[] points = result.getResultPoints();
        				ResultPoint a = points[1]; // top-left
        				ResultPoint b = points[2]; // top-right
        				ResultPoint c = points[0]; // bottom-left
        				ResultPoint d = points.length == 4 ? points[3] : points[0]; // alignment point (bottom-right)
        				
        				g.setColor(Color.GREEN);
        				
        				g.drawPolygon(new int[] {(int)a.getX(),(int)b.getX(),(int)d.getX(),(int)c.getX()}, 
  						      new int[] {(int)a.getY(),(int)b.getY(),(int)d.getY(),(int)c.getY()}, 4);
        				
        				g.setColor(Color.RED);
        				g.setFont(font);
        				g.drawString(result.getText(), (int)a.getX(), (int)a.getY());
        				g.drawString(orientation, (int)a.getX(), (int)a.getY() + 20);
        				
        				if ((System.currentTimeMillis() - result.getTimestamp()) > 1000)
        				{
        					result = null;
        				}
        			}
        		}
        		else
        		{
        			g.drawString("Waiting for Video ...", 10, 20);
        		}
        	}

        };
        setContentPane(contentPane);
	}
	
	public void imageUpdated(BufferedImage newImage)
    {
    	image = newImage;
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				contentPane.repaint();
			}
		});
    }
	
	public void onTag(Result result, float orientation)
	{
		if (result != null)
		{
			this.result = result;
			this.orientation = orientation + "°";
		}
//		else
//		{
//			this.result = null;
//			this.orientation = "n/a °";
//		}
	}
}
