package de.yadrone.apps.paperchase;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import de.yadrone.apps.controlcenter.plugins.keyboard.KeyboardCommandManager;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.LEDAnimation;

public class PaperChaseController extends Thread implements TagListener
{
	private final static int SPEED = 5;
	private final static int SLEEP = 500;
	
	private IARDrone drone;
	
	private KeyboardCommandManager keyboardCommandManager;
	
	/* This lsit holds tag-IDs for all tags which have successfully been visited */
	private ArrayList<String> tagVisitedList = new ArrayList<String>();
	
	private Result tag;
	private float tagOrientation;
	
	public PaperChaseController(IARDrone drone)
	{
		this.drone = drone;
	}
	
	public void run()
	{
        keyboardCommandManager = new KeyboardCommandManager(drone);
		
		// CommandManager handles (keyboard) input and dispatches events to the drone		
		System.out.println("PaperChaseController: grab the whole keyboard input from now on ...");
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(keyEventDispatcher);
        
		while(true)
		{
			try
			{
				if ((tag != null) && (System.currentTimeMillis() - tag.getTimestamp() > 500)) // reset if too old (and not updated)
					tag = null;
				
				if ((tag == null) || hasTagBeenVisited())
				{
					strayAround();
				}
				else if (!isTagCentered()) // tag visible, but not centered
				{
					centerTag();
				}
				else
				{
					System.out.println("I do not know what to do ...");
				}
			}
			catch(Exception exc)
			{
				exc.printStackTrace();
			}
		}
	}

	public void onTag(Result result, float orientation)
	{
		if (result == null) // ToDo: do not call if no tag is present
			return;
		
		System.out.println("Tag found");

		tag = result;
		tagOrientation = orientation;
	}
	
	
	private boolean isTagCentered()
	{
		if (tag == null)
			return false;
		
		// a tag is centered if it is
		// 1. if "Point 1" (on the tag the upper left point) is near the center of the camera  
		// 2. orientation is between 350 and 10 degrees
		
		int imgCenterX = PaperChase.IMAGE_WIDTH / 2;
		int imgCenterY = PaperChase.IMAGE_HEIGHT / 2;
		
		ResultPoint[] points = tag.getResultPoints();
		boolean isCentered = ((points[1].getX() > (imgCenterX - PaperChase.TOLERANCE)) &&
			(points[1].getX() < (imgCenterX + PaperChase.TOLERANCE)) &&
			(points[1].getY() > (imgCenterY - PaperChase.TOLERANCE)) &&
			(points[1].getY() < (imgCenterY + PaperChase.TOLERANCE)));

		boolean isOriented = ((tagOrientation < 10) || (tagOrientation > 350));
			
		System.out.println("Tag centered ? " + isCentered + " Tag oriented ? " + isOriented);
		
		return isCentered && isOriented;
	}
	
	private boolean hasTagBeenVisited()
	{
		synchronized(tag)
		{
			for (int i=0; i < tagVisitedList.size(); i++)
			{
				if (tag.getText().equals(tagVisitedList.get(i)))
					return true;
			}
		}
		
		return false;
	}
	
	private void strayAround() throws InterruptedException
	{
//		int direction = new Random().nextInt() % 4;
//		switch(direction)
//		{
//			case 0 : drone.getCommandManager().forward(SPEED); System.out.println("Stray Around: FORWARD"); break;
//			case 1 : drone.getCommandManager().backward(SPEED); System.out.println("Stray Around: BACKWARD");break;
//			case 2 : drone.getCommandManager().goLeft(SPEED); System.out.println("Stray Around: LEFT"); break;
//			case 3 : drone.getCommandManager().goRight(SPEED); System.out.println("Stray Around: RIGHT");break;
//		}
		
		Thread.currentThread().sleep(SLEEP);
	}
	
	private void centerTag() throws InterruptedException
	{
		String tagText;
		ResultPoint[] points;
		
		synchronized(tag)
		{
			points = tag.getResultPoints();	
			tagText = tag.getText();
		}
		
		int imgCenterX = PaperChase.IMAGE_WIDTH / 2;
		int imgCenterY = PaperChase.IMAGE_HEIGHT / 2;
		
		float x = points[1].getX();
		float y = points[1].getY();
		
//		if (x < (imgCenterX - PaperChase.TOLERANCE))
//		{
//			System.out.println("Go left");
//			drone.getCommandManager().goLeft(SPEED);
//			Thread.currentThread().sleep(SLEEP);
//		}
//		else if (x > (imgCenterX + PaperChase.TOLERANCE))
//		{
//			System.out.println("Go right");
//			drone.getCommandManager().goRight(SPEED);
//			Thread.currentThread().sleep(SLEEP);
//		}
//		else if (y < (imgCenterY - PaperChase.TOLERANCE))
//		{
//			System.out.println("Go forward");
//			drone.getCommandManager().forward(SPEED);
//			Thread.currentThread().sleep(SLEEP);
//		}
//		else if (y > (imgCenterY + PaperChase.TOLERANCE))
//		{
//			System.out.println("Go backward");
//			drone.getCommandManager().backward(SPEED);
//			Thread.currentThread().sleep(SLEEP);
//		}
//		else 
			if ((tagOrientation > 10) && (tagOrientation < 180))
		{
			System.out.println("Spin left");
			drone.getCommandManager().spinLeft(SPEED);
			Thread.currentThread().sleep(SLEEP);
		}
		else if ((tagOrientation < 350) && (tagOrientation > 180))
		{
			System.out.println("Spin right");
			drone.getCommandManager().spinRight(SPEED);
			Thread.currentThread().sleep(SLEEP);
		}
		else
		{
			System.out.println("Tag centered");
			drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_GREEN, 10, 5);
			
			tagVisitedList.add(tagText);
		}
	}
	
	private KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher() {
    	
    	public boolean dispatchKeyEvent(KeyEvent e)
		{
			if (e.getID() == KeyEvent.KEY_PRESSED) 
			{
                keyboardCommandManager.keyPressed(e);
            } 
			else if (e.getID() == KeyEvent.KEY_RELEASED) 
            {
                keyboardCommandManager.keyReleased(e);
            }
            return false;
		}
	};
}
