package de.yadrone.apps.controlcenter.plugins.keyboard;


import java.awt.event.KeyEvent;

import de.yadrone.base.IARDrone;

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
			case KeyEvent.VK_1:
				drone.setHorizontalCamera();
				// System.out.println("1");
				break;
			case KeyEvent.VK_2:
				drone.setHorizontalCameraWithVertical();
				// System.out.println("2");
				break;
			case KeyEvent.VK_3:
				drone.setVerticalCamera();
				// System.out.println("3");
				break;
			case KeyEvent.VK_4:
				drone.setVerticalCameraWithHorizontal();
				// System.out.println("4");
				break;
			case KeyEvent.VK_5:
				drone.toggleCamera();
				// System.out.println("5");
				break;
			case KeyEvent.VK_PLUS:
				drone.setSpeed(drone.getSpeed()+1);
				break;
			case KeyEvent.VK_MINUS:
				drone.setSpeed(drone.getSpeed()-1);
				break;
		}
	}
}
