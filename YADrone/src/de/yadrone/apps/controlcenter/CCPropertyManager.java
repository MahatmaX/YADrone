package de.yadrone.apps.controlcenter;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class CCPropertyManager extends Properties
{
	private String FILENAME = "controlcenter.properties";
	
	private static CCPropertyManager instance;
	
	private CCPropertyManager()
	{
		super();
		load();
	}
	
	public static CCPropertyManager getInstance()
	{
		if (instance == null)
			instance = new CCPropertyManager();
		return instance;
	}
	
	public boolean isPluginAutoStart(String title)
	{
		return Boolean.parseBoolean(getProperty(title + "_autostart", "false"));
	}
	
	public void setPluginAutoStart(String title, boolean autoStart)
	{
		setProperty(title + "_autostart", autoStart+"");
		store();
	}

	public Dimension getPluginScreenSize(String title)
	{
		String size = getProperty(title + "_size");
		return new Dimension(Integer.parseInt(size.substring(0, size.indexOf("x"))), Integer.parseInt(size.substring(size.indexOf("x") + 1)));
	}
	
	public void setPluginScreenSize(String title, Dimension size)
	{
		setProperty(title + "_size", (int)size.getWidth() + "x" + (int)size.getHeight());
		store();
	}
	
	public Point getPluginScreenLocation(String title)
	{
		String location = getProperty(title + "_location");
		return new Point(Integer.parseInt(location.substring(0, location.indexOf(","))), Integer.parseInt(location.substring(location.indexOf(",") + 1)));
	}
	
	public void setPluginScreenLocation(String title, Point location)
	{
		setProperty(title + "_location", (int)location.getX() + "," + (int)location.getY());
		store();
	}
	
	public boolean isKeyboardCommandManagerAlternative()
	{
		return Boolean.parseBoolean(getProperty("keyboard_command_layout_alternative", "false"));
	}
	
	public void setKeyboardCommandManagerAlternative(boolean isAlternative)
	{
		setProperty("keyboard_command_layout_alternative", isAlternative+"");
		store();
	}
	
	public String getVideoFormat()
	{
		String str = getProperty("video_format", "H.264");
		return str;
	}
	
	public void setVideoFormat(String format)
	{
		setProperty("video_format", format);
		store();
	}
	
	public String getVideoStoragePath()
	{
		try
		{
			File f = new File("./");
			String str = getProperty("video_storage_path", f.getAbsolutePath());
			return str;
		}
		catch(Exception exc)
		{
			return null;
		}
	}
	
	public void setVideoStoragePath(String path)
	{
		try
		{
			File f = new File(path);
			if (!f.exists())
				f.mkdirs();
			
			setProperty("video_storage_path", path);
			store();
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	}
	
	public String getVideoPlayFile() 
	{
		return getProperty("video_play_file", "");
	}
	
	public void setVideoPlayFile(String fileName) 
	{
		setProperty("video_play_file", fileName);
		store();
	}
	
	public boolean isScaleVideo()
	{
		return Boolean.parseBoolean(getProperty("video_scale", "false"));
	}
	
	public void setScaleVideo(boolean scaleVideo)
	{
		setProperty("video_scale", scaleVideo+"");
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
