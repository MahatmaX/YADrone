package de.yadrone.apps.tutorial;


import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.LEDAnimation;
import de.yadrone.base.navdata.AttitudeListener;

public class TutorialMain
{

	public static void main(String[] args)
	{
		IARDrone drone = null;
		try
		{
			// Tutorial Section 1
			drone = new ARDrone();
			drone.start();
			
			// Tutorial Section 2
			new TutorialAttitudeListener(drone);
			
			// Tutorial Section 3
			new TutorialVideoListener(drone);
			
			// Tutorial Section 4
			TutorialCommander commander = new TutorialCommander(drone);
			commander.animateLEDs();
//			commander.takeOffAndLand();
//			commander.leftRightForwardBackward();
		}
		catch (Exception exc)
		{
			exc.printStackTrace();

			if (drone != null)
				drone.stop();

			System.exit(-1);
		}
	}
}
