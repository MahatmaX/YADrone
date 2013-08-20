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
			drone = new ARDrone();

			// prepare the CommandManager to send AT commands to the drone
			drone.start();

			System.out.println("Successfully connected to the drone");
			
			drone.getNavDataManager().addAttitudeListener(new AttitudeListener() {
				
				public void attitudeUpdated(float pitch, float roll, float yaw)
				{
//			    	System.out.println("Pitch: " + pitch + " Roll: " + roll + " Yaw: " + yaw);
				}

				public void attitudeUpdated(float pitch, float roll) { }
				public void windCompensation(float pitch, float roll) { }
			});
			
			new TutorialVideoListener(drone);
			
			drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_ORANGE, 3, 10);
			
//			System.out.println("Takeoff");
//			drone.getCommandManager().takeOff();
//			Thread.sleep(5000);
//			System.out.println("Spin left");
//			drone.getCommandManager().spinLeft(50);
//			Thread.sleep(5000);
//			System.out.println("Spin right");
//			drone.getCommandManager().spinRight(50);
//			Thread.sleep(5000);
//			System.out.println("Land");
//			drone.getCommandManager().landing();
//			Thread.sleep(5000);
//			System.out.println("Emergency");
//			drone.getCommandManager().emergency();
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
