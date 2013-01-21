package gui_desktop;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SpeedPanel extends JPanel
{

	private int slowest = 0;
	private int fastest = 90;
 
	public SpeedPanel(final KeyboardCommandManager cmdManager, int defaultSpeed)
	{
		super(new GridBagLayout());
		setBackground(Color.WHITE);
		
		final JSlider slider = new JSlider(JSlider.VERTICAL, slowest, fastest, defaultSpeed);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setMinorTickSpacing(10);
		slider.setMajorTickSpacing(30);
		slider.setBackground(Color.WHITE);
		
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				System.out.println("Setting game speed: " + slider.getValue());
				cmdManager.speedUpdated(slider.getValue());
			}
		});
		
		add(slider, new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0,0,5,0), 0, 0));
		add(new JLabel("Speed"), new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(0,0,5,0), 0, 0));
		
		cmdManager.setSpeedListener(new ISpeedListener() {
			
			public void speedUpdated(int speed)
			{
				slider.setValue(speed);
			}
		});
	}
}
