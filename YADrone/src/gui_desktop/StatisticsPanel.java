package gui_desktop;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatisticsPanel extends JPanel
{
	private long navDataTimestamp = System.currentTimeMillis();
	private long imageTimestamp = System.currentTimeMillis();
	
	private JLabel avgNavData;
	private JLabel avgImage;
	
	public StatisticsPanel()
	{
		super(new GridBagLayout());
		
		setBackground(Color.white);
		
		avgNavData = new JLabel();
		avgImage = new JLabel();
		
		int y=0;
		add(createLabel(null, "NavData Arrival Rate: "), new GridBagConstraints(0, y, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
		add(createLabel(avgNavData, "n/a"), new GridBagConstraints(1, y++, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
		
		add(createLabel(null, "Image Arrival Rate: "), new GridBagConstraints(0, y, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
		add(createLabel(avgImage, "n/a"), new GridBagConstraints(1, y++, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
		
		add(new JLabel(""), new GridBagConstraints(0, y++, 2, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
	}
	
	private JLabel createLabel(JLabel label, String text)
	{
		if (label == null)
			label = new JLabel();
		
		label.setText(text);
		label.setFont(new Font("Helvetica", Font.PLAIN, 10));
		
		return label;
	}
	
	public void navDataArrived()
	{
		avgNavData.setText((System.currentTimeMillis() - navDataTimestamp) + " ms");
		navDataTimestamp = System.currentTimeMillis();
	}
	
	public void imageArrived()
	{
		avgImage.setText((System.currentTimeMillis() - imageTimestamp) + " ms");
		imageTimestamp = System.currentTimeMillis();
	}
}
