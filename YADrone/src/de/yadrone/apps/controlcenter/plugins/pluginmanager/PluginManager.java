package de.yadrone.apps.controlcenter.plugins.pluginmanager;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
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
	
	public PluginManager()
	{
		super(new GridBagLayout());
		
		JPanel contentPane = new JPanel(new GridBagLayout());
		
		// look for all classes implementing the Plugin interface
		Reflections reflections = new Reflections();
		Set subTypes = reflections.getSubTypesOf(ICCPlugin.class);
		Iterator iter = subTypes.iterator();
		while (iter.hasNext())
		{
			Class pluginClass = (Class)iter.next();
			if (pluginClass == PluginManager.class)
				continue; // do not instantiate the PluginManager once more
			
			// for each plugin class a new panel (with a button to (de)activate the plugin) is created
			try
			{
				ICCPlugin plugin = (ICCPlugin)pluginClass.newInstance();
				
				JPanel panel = createPluginPanel(plugin);
				contentPane.add(panel, new GridBagConstraints(0,contentPane.getComponentCount(),1,1,1,0,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				JPanel panel = createErrorPanel(pluginClass, e);
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
					    frame.setSize(plugin.getScreenSize());
					    frame.setLocation(plugin.getScreenLocation());
					    frame.setContentPane(plugin.getPanel());
					    frame.setVisible(true);
					    
						desktop.add(frame);
						
						// move frame to front and give focus 
						frame.moveToFront();
						frame.requestFocus();
						desktop.setSelectedFrame(frame);
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
					}
				}
				isStarted = !isStarted;
			}
		});
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder(plugin.getTitle()));
		panel.add(button, new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
		panel.add(new JLabel("<html><i>" + plugin.getDescription() + "</i></html>"), new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,10,0,0), 0, 0));
		
		return panel;
	}
	
	private JPanel createErrorPanel(Class pluginClass, Exception exc)
	{
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder(pluginClass.getName()));
		panel.add(new JLabel("<html><font color=#ff0000>Failed to load plugin: " + exc.getMessage() + " (" + exc.getClass().getSimpleName() + ")</font></html>"), new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,10,0,0), 0, 0));
		
		return panel;
	}
	
	public void setDesktop(JDesktopPane desktop)
	{
		this.desktop = desktop;
	}
	
	public void activate(IARDrone drone)
	{
		this.drone = drone;
	}
	
	public void deactivate()
	{
		
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
