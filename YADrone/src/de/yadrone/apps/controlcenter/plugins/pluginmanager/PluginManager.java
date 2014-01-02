package de.yadrone.apps.controlcenter.plugins.pluginmanager;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.reflections.Reflections;

import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.IARDrone;

public class PluginManager extends JPanel implements ICCPlugin
{
	private IARDrone drone;
	private JDesktopPane desktop;
	private PluginProperties pluginProperties;
	
	/** This frame is used to get screen size and location upon initialization and finalization */
	private Map<ICCPlugin, JInternalFrame> activePluginFrames;
	
	public PluginManager()
	{
		super(new GridBagLayout());
		
		pluginProperties = new PluginProperties();
		activePluginFrames = new HashMap<ICCPlugin, JInternalFrame>();
	}

	private void init()
	{
		JPanel contentPane = new JPanel(new GridBagLayout());
		
		// look for all classes implementing the Plugin interface
		Reflections reflections = new Reflections();
		Set subTypes = reflections.getSubTypesOf(ICCPlugin.class); // this set contains Class objects
		
		// we need a String list, because we want to sort the list alphabetically
		List<String> sortedList = new ArrayList<String>();
		Iterator iter = subTypes.iterator();
		while (iter.hasNext())
		{
			sortedList.add(((Class)iter.next()).getName());
		}
		Collections.sort(sortedList);
		
		// now we have a sorted list of class names and can go for creating panels
		for (int i=0; i < sortedList.size(); i++)
		{
			// for each plugin class a new panel (with a button to (de)activate the plugin) is created
			try
			{
				Class pluginClass = Class.forName(sortedList.get(i));
				if (pluginClass == PluginManager.class)
					continue; // do not instantiate the PluginManager once more
				
				ICCPlugin plugin = (ICCPlugin)pluginClass.newInstance();
				
				JPanel panel = createPluginPanel(plugin);
				contentPane.add(panel, new GridBagConstraints(0,contentPane.getComponentCount(),1,1,1,0,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				JPanel panel = createErrorPanel(sortedList.get(i), e);
				contentPane.add(panel, new GridBagConstraints(0,contentPane.getComponentCount(),1,1,1,0,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
			}
		}
		// add filler to top-align contents
		contentPane.add(new JLabel(""), new GridBagConstraints(0,contentPane.getComponentCount(),1,1,1,1,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		
		add(new JScrollPane(contentPane), new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));

	}
	
	private JPanel createPluginPanel(final ICCPlugin plugin)
	{
		final JButton button = new JButton("Activate");
		button.setMinimumSize(new Dimension(100,20));
		button.setMaximumSize(new Dimension(100,20));
		button.setPreferredSize(new Dimension(100,20));
		button.addActionListener(new ActionListener() {
			
			private boolean isStarted = false;
			private JInternalFrame frame;
			
			public void actionPerformed(ActionEvent e)
			{
				if (!isStarted)
				{
					plugin.activate(drone);
					button.setText("Deactivate");
					
					if (plugin.isVisual())
					{
						// visual plugins are added to the desktop pane
						
						frame = new JInternalFrame(plugin.getTitle(), true, false, true, true);
					    initPluginInternalFrame(plugin, frame);
					    activePluginFrames.put(plugin, frame);
					}
				}
				else
				{
					plugin.deactivate();
					button.setText("Activate");
					
					if (plugin.isVisual())
					{
						frame.setVisible(false);
						desktop.remove(frame);
						activePluginFrames.remove(plugin);
						pluginProperties.setAutoStart(plugin.getTitle(), false);
					}
				}
				isStarted = !isStarted;
			}
		});
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder(plugin.getTitle()));
		panel.add(button, new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
		panel.add(new JLabel("<html><i>" + plugin.getDescription() + "</i></html>"), new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,10,0,0), 0, 0));
		
		// start plugin if corresponding properties are set
		if (pluginProperties.isAutoStart(plugin.getTitle()))
			button.doClick();
		
		return panel;
	}
	
	private JPanel createErrorPanel(String pluginClass, Exception exc)
	{
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder(pluginClass));
		panel.add(new JLabel("<html><font color=#ff0000>Failed to load plugin: " + exc.getMessage() + " (" + exc.getClass().getSimpleName() + ")</font></html>"), new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,10,0,0), 0, 0));
		
		return panel;
	}
	
	private void initPluginInternalFrame(final ICCPlugin plugin, JInternalFrame frame)
	{
		// load properties and init frame
		frame.setSize(pluginProperties.isAutoStart(plugin.getTitle()) ? pluginProperties.getScreenSize(plugin.getTitle()) : plugin.getScreenSize());
	    frame.setLocation(pluginProperties.isAutoStart(plugin.getTitle()) ? pluginProperties.getScreenLocation(plugin.getTitle()) : plugin.getScreenLocation());
	    frame.setContentPane(plugin.getPanel());
	    frame.setVisible(true);
	    
		desktop.add(frame);
		
		// move frame to front and give focus 
		frame.moveToFront();
		frame.requestFocus();
		desktop.setSelectedFrame(frame);
	}
	
	public void setDesktop(JDesktopPane desktop)
	{
		this.desktop = desktop;
	}
	
	public void activate(IARDrone drone)
	{
		this.drone = drone;
		init();
	}
	
	public void deactivate()
	{
		// store all plugin properties
		Iterator<ICCPlugin> plugins = activePluginFrames.keySet().iterator();
		while (plugins.hasNext())
		{
			ICCPlugin plugin = plugins.next();
			pluginProperties.setAutoStart(plugin.getTitle(), true);
			pluginProperties.setScreenLocation(plugin.getTitle(), activePluginFrames.get(plugin).getLocation());
			pluginProperties.setScreenSize(plugin.getTitle(), activePluginFrames.get(plugin).getSize());
		}
	}

	public String getTitle()
	{
		return "Plugin Manager";
	}
	
	public String getDescription()
	{
		return "Used to load/unload ControlCenter plugins.";
	}

	public boolean isVisual()
	{
		return true;
	}

	public Dimension getScreenSize()
	{
		return new Dimension(400, 300);
	}
	
	public Point getScreenLocation()
	{
		return new Point(600, 400);
	}
	
	public JPanel getPanel()
	{
		return this;
	}
	
	private class PluginProperties extends Properties
	{
		private String FILENAME = "controlcenter.properties";
		
		public PluginProperties()
		{
			super();
			load();
		}
		
		public boolean isAutoStart(String title)
		{
			
			return Boolean.parseBoolean(getProperty(title + "_autostart", "false"));
		}
		
		public void setAutoStart(String title, boolean autoStart)
		{
			setProperty(title + "_autostart", autoStart+"");
			store();
		}

		public Dimension getScreenSize(String title)
		{
			String size = getProperty(title + "_size");
			return new Dimension(Integer.parseInt(size.substring(0, size.indexOf("x"))), Integer.parseInt(size.substring(size.indexOf("x") + 1)));
		}
		
		public void setScreenSize(String title, Dimension size)
		{
			setProperty(title + "_size", (int)size.getWidth() + "x" + (int)size.getHeight());
			store();
		}
		
		public Point getScreenLocation(String title)
		{
			String location = getProperty(title + "_location");
			return new Point(Integer.parseInt(location.substring(0, location.indexOf(","))), Integer.parseInt(location.substring(location.indexOf(",") + 1)));
		}
		
		public void setScreenLocation(String title, Point location)
		{
			setProperty(title + "_location", (int)location.getX() + "," + (int)location.getY());
			store();
		}
		
		private void load()
		{
			try
			{
				FileReader reader = new FileReader(FILENAME);
				load(reader);
			}
			catch (Exception e)
			{
				// probably not found (started for the first time)
				// e.printStackTrace();
			}
		}
		
		private void store()
		{
			try
			{
				FileWriter writer = new FileWriter(FILENAME);
				store(writer, "YADrone Control Center Properties" );
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
