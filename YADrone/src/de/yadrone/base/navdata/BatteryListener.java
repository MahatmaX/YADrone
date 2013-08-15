/*
 * Copyright 2010 Cliff L. Biffle.  All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */
package de.yadrone.base.navdata;

import java.util.EventListener;

public interface BatteryListener extends EventListener {
	public void batteryLevelChanged(int percentage);

	public void voltageChanged(int vbat_raw);
}
