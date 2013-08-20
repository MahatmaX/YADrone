package de.yadrone.apps.tutorial;

import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.AttitudeListener;

public class TutorialAttitudeListener
{

	public TutorialAttitudeListener(IARDrone drone)
	{
		drone.getNavDataManager().addAttitudeListener(new AttitudeListener() {
			
			public void attitudeUpdated(float pitch, float roll, float yaw)
			{
		    	System.out.println("Pitch: " + pitch + " Roll: " + roll + " Yaw: " + yaw);
			}

			public void attitudeUpdated(float pitch, float roll) { }
			public void windCompensation(float pitch, float roll) { }
		});
	}

}
