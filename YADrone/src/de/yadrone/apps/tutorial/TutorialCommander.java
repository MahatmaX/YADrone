package de.yadrone.apps.tutorial;

import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.LEDAnimation;

public class TutorialCommander
{

	private IARDrone drone;

	public TutorialCommander(IARDrone drone)
	{
		this.drone = drone;
	}

	public void animateLEDs()
	{
		drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_ORANGE, 3, 10);
	}
	
	public void takeOffAndLand()
	{
		try
		{
			drone.getCommandManager().takeOff();
			Thread.sleep(5000);
			drone.getCommandManager().landing();
		}
		catch (InterruptedException e) // may happen because of Thread.sleep()
		{
			e.printStackTrace();
		}
	}
	
	public void leftRightForwardBackward()
	{
		try
		{
			CommandManager cmd = drone.getCommandManager();
			int speed = 30; // percentage of max speed
			
			cmd.takeOff();
			Thread.sleep(5000);
			
			cmd.goLeft(speed);
			Thread.sleep(1000);
			
			cmd.hover();
			Thread.sleep(2000);
			
			cmd.goRight(speed);
			Thread.sleep(1000);
			
			cmd.hover();
			Thread.sleep(2000);
			
			cmd.forward(speed);
			Thread.sleep(1000);
			
			cmd.hover();
			Thread.sleep(2000);
			
			cmd.backward(speed);
			Thread.sleep(1000);
			
			cmd.hover();
			Thread.sleep(2000);
			
			cmd.landing();
		}
		catch (InterruptedException e) // may happen because of Thread.sleep() 
		{
			e.printStackTrace();
		}
	}
}
