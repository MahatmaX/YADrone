package de.yadrone.apps.paperchase.controller;

import com.google.zxing.Result;

import de.yadrone.apps.paperchase.TagListener;
import de.yadrone.base.IARDrone;

public abstract class PaperChaseAbstractController extends Thread implements TagListener
{
	protected boolean doStop = false;

	protected IARDrone drone;
	
	public PaperChaseAbstractController(IARDrone drone)
	{
		this.drone = drone;
	}

	public abstract void run();
	
	public void onTag(Result result, float orientation)
	{

	}
	
	public void stopController()
	{
		doStop = true;
	}
}
