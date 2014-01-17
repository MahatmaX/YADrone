package de.yadrone.apps.controlcenter.plugins.keyboard;


import java.awt.event.KeyEvent;

import de.yadrone.base.IARDrone;
import de.yadrone.base.command.FlightAnimation;

public class KeyboardCommandManagerAlternative extends KeyboardCommandManager
{
	public KeyboardCommandManagerAlternative(IARDrone drone)
	{
		super(drone);
	}

	protected void handleCommand(int key, int mod)
	{
		// just for debugging
//		if (key > 0)
//		{
//			System.out.println("KeyboardCommandManager: Keyboard input is disabled");
//			return;
//		}
		
		switch (key)
		{
			case KeyEvent.VK_ENTER:
				drone.takeOff();
				break;
			case KeyEvent.VK_SPACE:
				drone.landing();
				break;
			case KeyEvent.VK_A:
				drone.goLeft();
				break;
			case KeyEvent.VK_D:
				drone.goRight();
				break;
			case KeyEvent.VK_W:
				drone.forward();
				break;
			case KeyEvent.VK_S:
				drone.backward();
				break;
			case KeyEvent.VK_E:
				drone.stop();
				break;
			case KeyEvent.VK_R:
				drone.reset();
				break;
			case KeyEvent.VK_LEFT:
				drone.spinLeft();
				break;
			case KeyEvent.VK_RIGHT:
				drone.spinRight();
				break;
			case KeyEvent.VK_UP:
				drone.up();
				break;
			case KeyEvent.VK_DOWN:
				drone.down();
				break;
			case KeyEvent.VK_Y:
				drone.setHorizontalCamera();
				// System.out.println("1");
				break;
			case KeyEvent.VK_X:
				drone.setHorizontalCameraWithVertical();
				// System.out.println("2");
				break;
			case KeyEvent.VK_C:
				drone.setVerticalCamera();
				// System.out.println("3");
				break;
			case KeyEvent.VK_V:
				drone.setVerticalCameraWithHorizontal();
				// System.out.println("4");
				break;
			case KeyEvent.VK_B:
				drone.toggleCamera();
				// System.out.println("5");
				break;
			case KeyEvent.VK_PLUS:
				drone.setSpeed(drone.getSpeed()+1);
				break;
			case KeyEvent.VK_MINUS:
				drone.setSpeed(drone.getSpeed()-1);
				break;
			case KeyEvent.VK_F1:
				drone.getCommandManager().animate(FlightAnimation.PHI_M30_DEG);
				break;
			case KeyEvent.VK_F2:
				drone.getCommandManager().animate(FlightAnimation.PHI_30_DEG);
				break;
			case KeyEvent.VK_F3:
				drone.getCommandManager().animate(FlightAnimation.THETA_M30_DEG);
				break;
			case KeyEvent.VK_F4:
				drone.getCommandManager().animate(FlightAnimation.THETA_30_DEG);
				break;
			case KeyEvent.VK_F5:
				drone.getCommandManager().animate(FlightAnimation.THETA_20DEG_YAW_200DEG);
				break;
			case KeyEvent.VK_F6:
				drone.getCommandManager().animate(FlightAnimation.THETA_20DEG_YAW_M200DEG);
				break;
			case KeyEvent.VK_F7:
				drone.getCommandManager().animate(FlightAnimation.TURNAROUND);
				break;
			case KeyEvent.VK_F8:
				drone.getCommandManager().animate(FlightAnimation.TURNAROUND_GODOWN);
				break;
			case KeyEvent.VK_F9:
				drone.getCommandManager().animate(FlightAnimation.YAW_SHAKE);
				break;
			case KeyEvent.VK_F10:
				drone.getCommandManager().animate(FlightAnimation.YAW_DANCE);
				break;
			case KeyEvent.VK_1:
				drone.getCommandManager().animate(FlightAnimation.PHI_DANCE);
				break;
			case KeyEvent.VK_2:
				drone.getCommandManager().animate(FlightAnimation.THETA_DANCE);
				break;
			case KeyEvent.VK_3:
				drone.getCommandManager().animate(FlightAnimation.VZ_DANCE);
				break;
			case KeyEvent.VK_4:
				drone.getCommandManager().animate(FlightAnimation.WAVE);
				break;
			case KeyEvent.VK_5:
				drone.getCommandManager().animate(FlightAnimation.PHI_THETA_MIXED);
				break;
			case KeyEvent.VK_6:
				drone.getCommandManager().animate(FlightAnimation.DOUBLE_PHI_THETA_MIXED);
				break;
			case KeyEvent.VK_7:
				drone.getCommandManager().animate(FlightAnimation.FLIP_AHEAD);
				break;
			case KeyEvent.VK_8:
				drone.getCommandManager().animate(FlightAnimation.FLIP_BEHIND);
				break;
			case KeyEvent.VK_9:
				drone.getCommandManager().animate(FlightAnimation.FLIP_LEFT);
				break;
			case KeyEvent.VK_0:
				drone.getCommandManager().animate(FlightAnimation.FLIP_RIGHT);
				break;				
		}
	}
}
