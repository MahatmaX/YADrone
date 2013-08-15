package de.yadrone.base.navdata;

import java.util.EventListener;

public interface TimeListener extends EventListener {

	public void timeReceived(int seconds, int useconds);

}
