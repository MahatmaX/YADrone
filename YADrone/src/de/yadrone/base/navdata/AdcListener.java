package de.yadrone.base.navdata;

import java.util.EventListener;


public interface AdcListener extends EventListener {

	public void receivedFrame(AdcFrame d);

}
