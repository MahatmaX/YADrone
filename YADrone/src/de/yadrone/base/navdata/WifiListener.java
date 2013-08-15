package de.yadrone.base.navdata;

import java.util.EventListener;

public interface WifiListener extends EventListener {

	public void received(long link_quality);

}
