package de.yadrone.base.navdata;

import java.util.EventListener;


public interface ReferencesListener extends EventListener {

	public void receivedRcReferences(int[] rc_ref);

	public void receivedReferences(ReferencesData d);

}
