package de.yadrone.apps.controlcenter.plugins.pluginmanager;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.reflections.Reflections;

import de.yadrone.apps.controlcenter.CCPropertyManager;
import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.IARDrone;

public class PluginManager extends JPanel implements ICCPlugin
{
	private IARDrone drone;
	private JDesktopPane desktop;
	private CCPropertyManager pluginProperties;
	
	/** This frame is used to get screen size and location upon initialization and finalization */
	private Map<ICCPlugin, JInternalFrame> activePluginFrames;
	
	public PluginManager()
	{
		super(new GridBagLayout());
		
		pluginProperties = CCPropertyManager.getInstance();
		
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
						pluginProperties.setPluginAutoStart(plugin.getTitle(), false);
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
		if (pluginProperties.isPluginAutoStart(plugin.getTitle()))
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
		frame.setSize(pluginProperties.isPluginAutoStart(plugin.getTitle()) ? pluginProperties.getPluginScreenSize(plugin.getTitle()) : plugin.getScreenSize());
	    frame.setLocation(pluginProperties.isPluginAutoStart(plugin.getTitle()) ? pluginProperties.getPluginScreenLocation(plugin.getTitle()) : plugin.getScreenLocation());
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
			pluginProperties.setPluginAutoStart(plugin.getTitle(), true);
			pluginProperties.setPluginScreenLocation(plugin.getTitle(), activePluginFrames.get(plugin).getLocation());
			pluginProperties.setPluginScreenSize(plugin.getTitle(), activePluginFrames.get(plugin).getSize());
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
}
