package de.yadrone.android;

import android.app.Application;

import com.shigeodayo.ardrone.ARDrone;

public class YADroneApplication extends Application
{
	/**
	 * The drone is kept in the application context so that all activities use the same drone instance
	 */
	private ARDrone drone;
	
	public void onCreate()
	{
		drone = new ARDrone("192.168.1.1");
	}

	public ARDrone getARDrone()
	{
		return drone;
	}
}
