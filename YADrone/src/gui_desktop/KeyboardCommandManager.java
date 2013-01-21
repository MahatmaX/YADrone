package gui_desktop;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.shigeodayo.ardrone.ARDrone;

public class KeyboardCommandManager implements KeyListener, ISpeedListener
{
	private ARDrone ardrone;
	private int speed;
	private ISpeedListener listener;
	
	public KeyboardCommandManager(ARDrone ardrone, int defaultSpeed)
	{
		this.ardrone = ardrone;
		this.speed = defaultSpeed;
	}
	
	public int getSpeed()
	{
		return speed;
	}
	
	public void speedUpdated(int speed)
	{
		this.speed = speed;
	}
	
	public void setSpeedListener(ISpeedListener listener)
	{
		this.listener = listener;
	}
	
	public void keyReleased(KeyEvent e)
	{
		System.out.println("Key released: " + e.getKeyChar());

		ardrone.stop();
	}

	public void keyPressed(KeyEvent e)
	{
		System.out.println("Key pressed: " + e.getKeyChar()); //  + " (Enter=" + KeyEvent.VK_ENTER + " Space=" + KeyEvent.VK_SPACE + " S=" + KeyEvent.VK_S + " E=" + KeyEvent.VK_E + ")");

		int key = e.getKeyCode();
		int mod = e.getModifiersEx();

		handleCommand(key, mod);
	}

	public void handleCommand(int key, int mod)
	{
		// just for debugging
		if (key > 0)
		{
			System.out.println("KeyboardCommandManager: Keyboard input is disabled");
			return;
		}
		
		boolean shiftflag = false;
		if ((mod & InputEvent.SHIFT_DOWN_MASK) != 0)
		{
			shiftflag = true;
		}

		switch (key)
		{
			case KeyEvent.VK_ENTER:
				ardrone.takeOff();
				break;
			case KeyEvent.VK_SPACE:
				ardrone.landing();
				break;
			case KeyEvent.VK_S:
				ardrone.stop();
				break;
			case KeyEvent.VK_LEFT:
				if (shiftflag)
				{
					ardrone.spinLeft();
					shiftflag = false;
				}
				else
					ardrone.goLeft(speed);
				break;
			case KeyEvent.VK_RIGHT:
				if (shiftflag)
				{
					ardrone.spinRight();
					shiftflag = false;
				}
				else
					ardrone.goRight(speed);
				break;
			case KeyEvent.VK_UP:
				if (shiftflag)
				{
					ardrone.up(speed);
					shiftflag = false;
				}
				else
					ardrone.forward(speed);
				break;
			case KeyEvent.VK_DOWN:
				if (shiftflag)
				{
					ardrone.down(speed);
					shiftflag = false;
				}
				else
					ardrone.backward(speed);
				break;
			case KeyEvent.VK_1:
				ardrone.setHorizontalCamera();
				// System.out.println("1");
				break;
			case KeyEvent.VK_2:
				ardrone.setHorizontalCameraWithVertical();
				// System.out.println("2");
				break;
			case KeyEvent.VK_3:
				ardrone.setVerticalCamera();
				// System.out.println("3");
				break;
			case KeyEvent.VK_4:
				ardrone.setVerticalCameraWithHorizontal();
				// System.out.println("4");
				break;
			case KeyEvent.VK_5:
				ardrone.toggleCamera();
				// System.out.println("5");
				break;
			case KeyEvent.VK_R:
				ardrone.spinRight();
				break;
			case KeyEvent.VK_L:
				ardrone.spinLeft();
				break;
			case KeyEvent.VK_U:
				ardrone.up();
				break;
			case KeyEvent.VK_D:
				ardrone.down();
				break;
			case KeyEvent.VK_E:
				ardrone.reset();
				break;
			case KeyEvent.VK_PLUS:
				if (speed < 90)
				{
					speedUpdated(speed+1);
					listener.speedUpdated(speed+1);
				}
				break;
			case KeyEvent.VK_MINUS:
				if (speed > 0)
				{
					speedUpdated(speed-1);
					listener.speedUpdated(speed-1);
				}
				break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}
}
