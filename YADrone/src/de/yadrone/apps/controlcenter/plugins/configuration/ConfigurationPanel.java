package de.yadrone.apps.controlcenter.plugins.configuration;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.IARDrone;
import de.yadrone.base.configuration.ConfigurationListener;

public class ConfigurationPanel extends JPanel implements ICCPlugin
{
	private IARDrone drone;
	
	private JRadioButton customConfigIdsButton;
	private JRadioButton previousRunLogsButton;
	private JRadioButton configurationButton;
	
	private JTextArea text;
	
	public ConfigurationPanel()
	{
		super(new GridBagLayout());
		
		setBackground(Color.white);
		
		customConfigIdsButton = new JRadioButton("Custom Config Ids");
		customConfigIdsButton.setEnabled(false);
		customConfigIdsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				drone.getConfigurationManager().getConfiguration(configListener);
			}
		});
		
		previousRunLogsButton = new JRadioButton("Previous Run Logs");
		previousRunLogsButton.setEnabled(false);
		previousRunLogsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				drone.getConfigurationManager().getConfiguration(configListener);
			}
		});
		
		configurationButton = new JRadioButton("Current Configuration");
		configurationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				drone.getConfigurationManager().getConfiguration(configListener);
			}
		});
		
		ButtonGroup g = new ButtonGroup();
		g.add(configurationButton);
		g.add(customConfigIdsButton);
		g.add(previousRunLogsButton);

		configurationButton.setSelected(true);
		
		JPanel buttonGroupPanel = new JPanel();
		buttonGroupPanel.setLayout(new BoxLayout(buttonGroupPanel, BoxLayout.LINE_AXIS));
		buttonGroupPanel.add(configurationButton);
		buttonGroupPanel.add(customConfigIdsButton);
		buttonGroupPanel.add(previousRunLogsButton);
		
		
		text = new JTextArea("Waiting for Configuration ...");
		text.setEditable(false);
		text.setFont(new Font("Helvetica", Font.PLAIN, 10));
		
		add(buttonGroupPanel, new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0));
		add(new JScrollPane(text), new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
	}
	
	private ConfigurationListener configListener = new ConfigurationListener() {
		public void result(final String s)
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
//					System.out.println("Set new Text : " + s);
					text.setText(s);
				}
			});
		}
	};
	
	public void activate(IARDrone drone)
	{
		this.drone = drone;
		
		drone.getConfigurationManager().getConfiguration(configListener);
	}

	public void deactivate()
	{
	}

	public String getTitle()
	{
		return "Configuration";
	}
	
	public String getDescription()
	{
		return "Displays information about the drone's current configuration";
	}

	public boolean isVisual()
	{
		return true;
	}

	public Dimension getScreenSize()
	{
		return new Dimension(360, 650);
	}
	
	public Point getScreenLocation()
	{
		return new Point(410, 0);
	}

	public JPanel getPanel()
	{
		return this;
	}
}
