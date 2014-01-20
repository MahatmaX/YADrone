package de.yadrone.apps.controlcenter.plugins.remotelog;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import org.apache.commons.net.telnet.TelnetClient;

import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.IARDrone;

public class RemoteLogPanel extends JPanel implements ICCPlugin
{
	private TelnetClient telnet;
	private JTextArea text;

	public RemoteLogPanel()
	{
		super(new GridBagLayout());

		text = new JTextArea("Waiting for remote log ...");
		// text.setEditable(false);
		text.setFont(new Font("Courier", Font.PLAIN, 10));

		DefaultCaret caret = (DefaultCaret)text.getCaret(); // auto scroll
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		add(new JScrollPane(text), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

	}

	public void telnetConnect()
    {
        new Thread(new Runnable() {

			public void run()
			{
				try
				{
					telnet = new TelnetClient();

		        	telnet.connect("192.168.1.1");
				
		        	new Thread(new Runnable()
		        	{
						public void run()
						{
				        	InputStream instr = telnet.getInputStream();
	
				            byte[] buff = new byte[1024];
				            int ret_read = 0;
	
				            try
				            {
					            do
					            {
					                ret_read = instr.read(buff);
					                if(ret_read > 0)
					                {
					                	final String str = new String(buff, 0, ret_read);
					                	text.append(str);
					                	text.setCaretPosition(text.getDocument().getLength());
					                }
					            }
					            while (ret_read >= 0);
				            }
				            catch(Exception exc)
				            {
				            	exc.printStackTrace();
				            }
						}
		        	}).start();
	        	
		        	telnet.getOutputStream().write("tail -f /data/syslog.bin \r\n".getBytes());
		        	telnet.getOutputStream().flush();
	        	
				}
		        catch (Exception e)
		        {
		            System.err.println("Exception while reading socket:" + e.getMessage());
		        }
			}
        	
        }).start();
    }

	public void activate(IARDrone drone)
	{
		telnetConnect();
	}

	public void deactivate()
	{
		try
		{
			telnet.disconnect();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	public String getTitle()
	{
		return "Logging Console (remote)";
	}

	public String getDescription()
	{
		return "Displays the drone's onboard log-file (syslog.bin).";
	}

	public boolean isVisual()
	{
		return true;
	}

	public Dimension getScreenSize()
	{
		return new Dimension(600, 300);
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
