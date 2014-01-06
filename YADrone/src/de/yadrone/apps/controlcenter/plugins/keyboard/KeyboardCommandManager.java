package de.yadrone.apps.controlcenter.plugins.keyboard;


import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import de.yadrone.base.IARDrone;

public class KeyboardCommandManager implements KeyListener
{
	protected IARDrone drone;
	
	public KeyboardCommandManager(IARDrone ardrone)
	{
		this.drone = ardrone;
	}
	
	public void keyReleased(KeyEvent e)
	{
//		System.out.println("Key released: " + e.getKeyChar());

		drone.hover();
	}

	public void keyPressed(KeyEvent e)
	{
//		System.out.println("Key pressed: " + e.getKeyChar()); //  + " (Enter=" + KeyEvent.VK_ENTER + " Space=" + KeyEvent.VK_SPACE + " S=" + KeyEvent.VK_S + " E=" + KeyEvent.VK_E + ")");

		int key = e.getKeyCode();
		int mod = e.getModifiersEx();

		handleCommand(key, mod);
	}

	protected void handleCommand(int key, int mod)
	{
		// just for debugging
//		if (key > 0)
//		{
//			System.out.println("KeyboardCommandManager: Keyboard input is disabled");
//			return;
//		}
		
		boolean shiftflag = false;
		if ((mod & InputEvent.SHIFT_DOWN_MASK) != 0)
		{
			shiftflag = true;
		}

		switch (key)
		{
			case KeyEvent.VK_ENTER:
				drone.takeOff();
				break;
			case KeyEvent.VK_SPACE:
				drone.landing();
				break;
			case KeyEvent.VK_S:
				drone.stop();
				break;
			case KeyEvent.VK_LEFT:
				if (shiftflag)
				{
					drone.spinLeft();
					shiftflag = false;
				}
				else
					drone.goLeft();
				break;
			case KeyEvent.VK_RIGHT:
				if (shiftflag)
				{
					drone.spinRight();
					shiftflag = false;
				}
				else
					drone.goRight();
				break;
			case KeyEvent.VK_UP:
				if (shiftflag)
				{
					drone.up();
					shiftflag = false;
				}
				else
					drone.forward();
				break;
			case KeyEvent.VK_DOWN:
				if (shiftflag)
				{
					drone.down();
					shiftflag = false;
				}
				else
					drone.backward();
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
			case KeyEvent.VK_R:
				drone.spinRight();
				break;
			case KeyEvent.VK_L:
				drone.spinLeft();
				break;
			case KeyEvent.VK_U:
				drone.up();
				break;
			case KeyEvent.VK_D:
				drone.down();
				break;
			case KeyEvent.VK_E:
				drone.reset();
				break;
			case KeyEvent.VK_PLUS:
				drone.setSpeed(drone.getSpeed()+1);
				break;
			case KeyEvent.VK_MINUS:
				drone.setSpeed(drone.getSpeed()-1);
				break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}
}
