package gui_desktop;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import com.shigeodayo.ardrone.ARDrone;
import com.shigeodayo.ardrone.navdata.javadrone.NavData;
import com.shigeodayo.ardrone.navdata.javadrone.NavDataListener;
import com.shigeodayo.ardrone.video.ImageListener;

public class CCFrame extends JFrame implements NavDataListener, ImageListener
{
	private VideoPanel video;
	private BatteryPanel battery;
	private StatePanel state;
	private AttitudePanel attitude;
	private AttitudeChartPanel attitudeChart;
	private StatisticsPanel statistics;
	private SpeedPanel speed;
	private KeyboardLayoutPanel keyboard;
	private ConsolePanel console;
	
	private ARDrone drone;
	
	public CCFrame(ARDrone ardrone)
	{
		super("YADrone Control Center");
		
		this.drone = ardrone;
		
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {}

		int defaultSpeed = 20;
		final KeyboardCommandManager cmdManager = new KeyboardCommandManager(drone, defaultSpeed);
		
		// CommandManager handles (keyboard) input and dispatches events to the drone		
		System.out.println("CCFrame.KeyEventDispatcher: grab the whole keyboard input from now on ...");
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher() {
        	
        	public boolean dispatchKeyEvent(KeyEvent e)
			{
				if (e.getID() == KeyEvent.KEY_PRESSED) 
				{
	                cmdManager.keyPressed(e);
	            } 
				else if (e.getID() == KeyEvent.KEY_RELEASED) 
	            {
	                cmdManager.keyReleased(e);
	            }
	            return false;
			}
		});
		
		video = new VideoPanel(ardrone);
		battery = new BatteryPanel();
		state = new StatePanel();
		attitude = new AttitudePanel();
		attitudeChart = new AttitudeChartPanel();
		statistics = new StatisticsPanel();
		speed = new SpeedPanel(cmdManager, defaultSpeed);
		keyboard = new KeyboardLayoutPanel(cmdManager);
		console = new ConsolePanel();
		
		JDesktopPane desktop = new JDesktopPane() {
			private Image originalImage;
			private Image scaledImage;
			private int width = 0;
		    private int height = 0;
		    
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                if (originalImage == null)
                { // called only once
	                ImageIcon icon = new ImageIcon(KeyboardLayoutPanel.class.getResource("img/desktop.jpg"));
	        		originalImage = icon.getImage();
	        		scaledImage = originalImage;
                }
        		
                if ((width != getWidth()) || (height != getHeight()))
                { // called once the uses changes the frame size
                	width = getWidth();
                	height = getHeight();
                	scaledImage = originalImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_AREA_AVERAGING);
                }
                
                g.drawImage(scaledImage, 0, 0, this);
            }
        };
        
        
	    desktop.add(createVideoFrame());
	    desktop.add(createStateFrame());
	    desktop.add(createBatteryFrame());
	    desktop.add(createStatisticsFrame());
	    desktop.add(createAttitudeFrame());
	    desktop.add(createAttitudeChartFrame());
	    desktop.add(createSpeedFrame());
	    desktop.add(createKeyboardLayoutFrame());
	    JInternalFrame frame = createConsoleFrame();
	    desktop.add(frame);
	    
	    setContentPane(desktop);
	    
		setSize(1024, 768);
		setVisible(true);
		
		/* 
		 * This is dirty, but JMonkey somehow steals the focus at startup and without the focus,
		 * keyboard input is not processed. Hence one element requests the focus every second.
		 */
		setAndKeepFocus(console);
		
		addWindowListener(new WindowListener() {
			
			public void windowOpened(WindowEvent e) { }
			public void windowIconified(WindowEvent e) { }
			public void windowDeiconified(WindowEvent e) { }
			public void windowActivated(WindowEvent e) { }
			public void windowDeactivated(WindowEvent e) { }
			public void windowClosing(WindowEvent e) {
				drone.disconnect();
				attitude.stop();
				System.exit(0);
			}
			public void windowClosed(WindowEvent e) { }
		});
		
		drone.addImageUpdateListener(this);
		drone.addNavDataListener(this);
	}

	private void setAndKeepFocus(ConsolePanel console2)
	{
		new Thread(new Runnable() {
			
			public void run()
			{
				while (true)
				{
					console.focus();
					try
					{
						Thread.sleep(1000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private JInternalFrame createVideoFrame()
	{
	    JInternalFrame frame = new JInternalFrame("Video", true, false, false, true);
	    frame.setSize(650, 390);
	    frame.setLocation(0, 0);
	    frame.setContentPane(video);
	    frame.setVisible(true);
	    return frame;
	}

	private JInternalFrame createStateFrame()
	{
	    JInternalFrame frame = new JInternalFrame("NavData", true, false, false, true);
	    frame.setSize(240, 650);
	    frame.setLocation(650, 0);
	    frame.setContentPane(new JScrollPane(state));
	    frame.setVisible(true);
	    return frame;
	}
	
	private JInternalFrame createBatteryFrame()
	{
	    JInternalFrame frame = new JInternalFrame("Battery", false, false, false, true);
	    frame.setSize(60, 120);
	    frame.setLocation(890, 0);
	    frame.setContentPane(battery);
	    frame.setVisible(true);
	    return frame;
	}
	
	private JInternalFrame createStatisticsFrame()
	{
	    JInternalFrame frame = new JInternalFrame("Statistics", true, false, false, true);
	    frame.setSize(200, 60);
	    frame.setLocation(0, 650);
	    frame.setContentPane(statistics);
	    frame.setVisible(true);
	    return frame;
	}
	
	private JInternalFrame createAttitudeFrame()
	{
	    JInternalFrame frame = new JInternalFrame("Attitude", true, false, false, true);
	    frame.setSize(330, 260);
	    frame.setLocation(0, 390);
	    JPanel panel = new JPanel();
	    panel.add(attitude.getPane());
	    frame.setContentPane(panel);
	    frame.setVisible(true);
	    return frame;
	}
	
	private JInternalFrame createAttitudeChartFrame()
	{
	    JInternalFrame frame = new JInternalFrame("Attitude", true, false, false, true);
	    frame.setSize(330, 250);
	    frame.setLocation(330, 390);
	    frame.setContentPane(attitudeChart);
	    frame.setVisible(true);
	    return frame;
	}
	
	private JInternalFrame createSpeedFrame()
	{
	    JInternalFrame frame = new JInternalFrame("Speed", true, false, false, true);
	    frame.setSize(60, 200);
	    frame.setLocation(890, 120);
	    frame.setContentPane(speed);
	    frame.setVisible(true);
	    return frame;
	}
	
	private JInternalFrame createKeyboardLayoutFrame()
	{
	    JInternalFrame frame = new JInternalFrame("Keyboard Layout", true, false, false, true);
	    frame.setSize(400, 150);
	    frame.setLocation(330, 260);
	    frame.setContentPane(keyboard);
	    frame.setVisible(true);
	    return frame;
	}
	
	private JInternalFrame createConsoleFrame()
	{
	    JInternalFrame frame = new JInternalFrame("Console", true, false, false, true);
	    frame.setSize(400, 300);
	    frame.setLocation(600, 400);
	    frame.setContentPane(console);
	    frame.setVisible(true);
	    return frame;
	}
	
	public void imageUpdated(BufferedImage image)
	{
		if (statistics != null)
			statistics.imageArrived();
		if (video != null)
			video.setImage(image);
	}
	
	public void navDataUpdated(NavData navData)
	{
		if (statistics != null)
			statistics.navDataArrived();
		if (battery != null)
			battery.setBatteryLevel(navData.getBattery());
		if (attitude != null)
			attitude.setAttitude(navData.getPitch(), navData.getRoll(), navData.getYaw(), (int)navData.getAltitude());
		if (attitudeChart != null)
			attitudeChart.setAttitude(navData.getPitch(), navData.getRoll(), navData.getYaw());
		if (state != null)
			state.setState(navData.toDetailString());
	}
}
