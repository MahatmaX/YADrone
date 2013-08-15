package de.yadrone.base.navdata;

import java.util.EventListener;

public interface TrimsListener extends EventListener {

	public void receivedTrimData(float angular_rates_trim_r, float euler_angles_trim_theta, float euler_angles_trim_phi);

}
