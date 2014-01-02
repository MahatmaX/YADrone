package de.yadrone.apps.controlcenter;


import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;

import de.yadrone.apps.controlcenter.plugins.pluginmanager.PluginManager;
import de.yadrone.base.ARDrone;

public class CCFrame extends JFrame
{
	private ARDrone drone;
	
	public CCFrame(ARDrone ardrone)
	{
		super("YADrone Control Center");
		
		setSize(1024, 768);
		
		this.drone = ardrone;
		
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {}

		JDesktopPane desktop = new JDesktopPane() {
			private Image originalImage;
			private Image scaledImage;
			private int width = 0;
		    private int height = 0;
		    
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                if (originalImage == null)
                { // called only once
	                ImageIcon icon = new ImageIcon(CCFrame.class.getResource("img/desktop.jpg"));
	        		originalImage = icon.getImage();
	        		scaledImage = originalImage;
                }
        		
                if ((width != getWidth()) || (height != getHeight()))
                { // called once the user changes the frame size
                	width = getWidth();
                	height = getHeight();
                	scaledImage = originalImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_AREA_AVERAGING);
                }
                
                g.drawImage(scaledImage, 0, 0, this);
            }
        };
        
        // the plugin manager is a mandatory component, because it is used to start all other plugins
        
        final PluginManager pluginManager = new PluginManager();
        pluginManager.setDesktop(desktop);
        pluginManager.activate(ardrone);
        
        JInternalFrame frame = new JInternalFrame(pluginManager.getTitle(), true, false, true, true);
	    frame.setSize(500, 300);
	    frame.setLocation(0, getSize().height - frame.getSize().height - 40);
	    frame.setContentPane(pluginManager);
	    frame.setVisible(true);
        
	    desktop.add(frame);
	    
	    setContentPane(desktop);
	    
		setVisible(true);
		
		addWindowListener(new WindowListener() {
			
			public void windowOpened(WindowEvent e) { }
			public void windowIconified(WindowEvent e) { }
			public void windowDeiconified(WindowEvent e) { }
			public void windowActivated(WindowEvent e) { }
			public void windowDeactivated(WindowEvent e) { }
			public void windowClosing(WindowEvent e) {
				pluginManager.deactivate();
				drone.stop();
				System.exit(0);
			}
			public void windowClosed(WindowEvent e) { }
		});
		
	}
}
