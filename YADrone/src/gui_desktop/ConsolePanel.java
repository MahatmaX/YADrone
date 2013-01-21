package gui_desktop;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class ConsolePanel extends JPanel
{
	private JTextArea text;
	
	public ConsolePanel()
	{
		super(new GridBagLayout());
		
		final JCheckBox checkBox = new JCheckBox("Redirect Console", false);
		checkBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				redirectSystemStreams(checkBox.isSelected());
			}
		});
		
		text = new JTextArea("Waiting for State ...");
//		text.setEditable(false);
		text.setFont(new Font("Courier", Font.PLAIN, 10));
		
		add(new JScrollPane(text), new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		add(checkBox, new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
		
	}

	public void focus()
	{
		text.setFocusable(true);
		text.setRequestFocusEnabled(true);
		text.requestFocus();
	}
	
	private void redirectSystemStreams(boolean enableRedirect)
	{
		if (enableRedirect)
		{
			OutputStream out = new OutputStream() {
				public void write(int b) throws IOException
				{
					updateTextArea(String.valueOf((char) b));
				}
	
				public void write(byte[] b, int off, int len) throws IOException
				{
					updateTextArea(new String(b, off, len));
				}
	
				public void write(byte[] b) throws IOException
				{
					write(b, 0, b.length);
				}
			};
			System.setOut(new PrintStream(out, true));
			System.setErr(new PrintStream(out, true));
		}
		else
		{
			System.setOut(System.out);
			System.setErr(System.err);
		}
	}

	private void updateTextArea(final String text)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				ConsolePanel.this.text.append(text);
			}
		});
	}

}
