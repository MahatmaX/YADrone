package de.yadrone.base.navdata;

import java.util.EventListener;


public interface AcceleroListener extends EventListener {
	public void receivedRawData(AcceleroRawData d);

	public void receivedPhysData(AcceleroPhysData d);
}
