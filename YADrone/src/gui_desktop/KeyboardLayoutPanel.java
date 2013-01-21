package gui_desktop;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class KeyboardLayoutPanel extends JPanel
{
	private Image originalImage;
	private Image scaledImage;
	private int width = 0;
    private int height = 0;
    
	public KeyboardLayoutPanel(final KeyboardCommandManager cmdManager)
	{
		ImageIcon icon = new ImageIcon(KeyboardLayoutPanel.class.getResource("img/keyboard_control.png"));
		originalImage = icon.getImage();
		scaledImage = originalImage;
	}
	
	
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        
        if ((width != getWidth()) || (height != getHeight()))
        {
        	width = getWidth();
        	height = getHeight();
        	scaledImage = getScaledImage();
        }
        
        g.drawImage(scaledImage, 0, 0, this);
    }
    
    private Image getScaledImage()
    {
    	return originalImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_AREA_AVERAGING);
    }
	
	
}
