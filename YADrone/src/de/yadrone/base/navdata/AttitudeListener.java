/*
 * Copyright 2010 Cliff L. Biffle.  All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */
package de.yadrone.base.navdata;

import java.util.EventListener;

public interface AttitudeListener extends EventListener {
	public void attitudeUpdated(float pitch, float roll, float yaw);

	public void attitudeUpdated(float pitch, float roll);

	public void windCompensation(float pitch, float roll);
}
