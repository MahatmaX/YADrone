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
		drone.getCommandManager().takeOff();
		drone.getCommandManager().waitFor(5000);
		drone.getCommandManager().landing();
	}
	
	public void leftRightForwardBackward()
	{
		final CommandManager cmd = drone.getCommandManager();
		final int speed = 30; // percentage of max speed
			
		cmd.takeOff().doFor(5000);
		
		cmd.goLeft(speed).doFor(1000);
		cmd.hover().doFor(2000);
		
		cmd.goRight(speed).doFor(1000);
		cmd.hover().doFor(2000);
		
		cmd.forward(speed).doFor(2000);
		cmd.hover().doFor(1000);
		
		cmd.backward(speed).doFor(2000);
		cmd.hover().doFor(2000);
		
		cmd.landing();
		
		// alternative: asynchronous call
//		cmd.takeOff();
//		cmd.schedule(5000, new Runnable() { // schedule to be executed in 5 secs
//			public void run()
//			{
//				cmd.goLeft(speed);
//				// [...]
//			}			
//		});
	}
}
