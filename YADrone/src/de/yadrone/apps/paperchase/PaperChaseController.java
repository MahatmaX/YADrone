package de.yadrone.apps.paperchase;

import java.util.ArrayList;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import de.yadrone.base.IARDrone;
import de.yadrone.base.command.LEDAnimation;

public class PaperChaseController extends Thread implements TagListener
{
	private final static int SPEED = 20;
	
	private IARDrone drone;
	
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
//		System.out.println("Stray Around");
//		drone.getCommandManager().forward(SPEED);
//		Thread.currentThread().sleep(100);
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
		
		if (x < (imgCenterX - PaperChase.TOLERANCE))
		{
			System.out.println("Go left");
//			drone.getCommandManager().goLeft(SPEED);
			Thread.currentThread().sleep(100);
		}
		else if (x > (imgCenterX + PaperChase.TOLERANCE))
		{
			System.out.println("Go right");
//			drone.getCommandManager().goRight(SPEED);
			Thread.currentThread().sleep(100);
		}
		else if (y < (imgCenterY - PaperChase.TOLERANCE))
		{
			System.out.println("Go forward");
//			drone.getCommandManager().forward(SPEED);
			Thread.currentThread().sleep(100);
		}
		else if (y > (imgCenterY + PaperChase.TOLERANCE))
		{
			System.out.println("Go backward");
//			drone.getCommandManager().backward(SPEED);
			Thread.currentThread().sleep(100);
		}
		else if ((tagOrientation > 10) && (tagOrientation < 180))
		{
			System.out.println("Spin left");
//			drone.getCommandManager().spinLeft(SPEED);
			Thread.currentThread().sleep(100);
		}
		else if ((tagOrientation < 350) && (tagOrientation > 180))
		{
			System.out.println("Spin right");
			drone.getCommandManager().spinRight(SPEED);
			Thread.currentThread().sleep(100);
		}
		else
		{
			System.out.println("Tag centered");
			drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_GREEN, 10, 5);
			
			tagVisitedList.add(tagText);
		}
	}
}
