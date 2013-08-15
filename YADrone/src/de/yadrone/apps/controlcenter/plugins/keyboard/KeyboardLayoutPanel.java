package de.yadrone.apps.controlcenter.plugins.keyboard;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.IARDrone;

public class KeyboardLayoutPanel extends JPanel implements ICCPlugin
{
	private KeyboardCommandManager cmdManager;
	
	private String currentKey = "";
	
	private Image originalImage;
	private Image scaledImage;
	private int width = 0;
    private int height = 0;
    
	public KeyboardLayoutPanel()
	{
		ImageIcon icon = new ImageIcon(KeyboardLayoutPanel.class.getResource("keyboard_control.png"));
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
        g.drawString(currentKey, 10, 20);
    }
    
    private Image getScaledImage()
    {
    	return originalImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_AREA_AVERAGING);
    }
	
    private KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher() {
    	
    	public boolean dispatchKeyEvent(KeyEvent e)
		{
			if (e.getID() == KeyEvent.KEY_PRESSED) 
			{
                cmdManager.keyPressed(e);
                currentKey = "KEY " + e.getKeyChar();
                repaint();
            } 
			else if (e.getID() == KeyEvent.KEY_RELEASED) 
            {
                cmdManager.keyReleased(e);
                currentKey = "";
                repaint();
            }
            return false;
		}
	};
	
    public void activate(IARDrone drone)
	{
		cmdManager = new KeyboardCommandManager(drone);
		
		// CommandManager handles (keyboard) input and dispatches events to the drone		
		System.out.println("KeyboardLayoutPanel: grab the whole keyboard input from now on ...");
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(keyEventDispatcher);
	}

	public void deactivate()
	{
		System.out.println("KeyboardLayoutPanel: release key event dispatcher ...");
		
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.removeKeyEventDispatcher(keyEventDispatcher);
	}

	public String getTitle()
	{
		return "Keyboard Control";
	}
	
	public String getDescription()
	{
		return "Allow to control the drone via keyboard";
	}

	public boolean isVisual()
	{
		return true;
	}

	public Dimension getScreenSize()
	{
		return new Dimension(400, 150);
	}
	
	public Point getScreenLocation()
	{
		return new Point(330, 260);
	}

	public JPanel getPanel()
	{
		return this;
	}
}
