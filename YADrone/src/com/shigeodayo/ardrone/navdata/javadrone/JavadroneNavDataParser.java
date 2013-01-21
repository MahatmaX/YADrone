/*
 * Copyright 2010 Cliff L. Biffle.  All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */

package com.shigeodayo.ardrone.navdata.javadrone;

import java.nio.ByteBuffer;

import com.shigeodayo.ardrone.navdata.NavDataException;


public class JavadroneNavDataParser {
	private NavDataListener navDataListener;
	
	long lastSequenceNumber=1;
	
	//set listeners
	public void setNavDataListener(NavDataListener navDataListener){
		this.navDataListener=navDataListener;
	}
		
	public void parseNavData(ByteBuffer buffer) throws NavDataException{
		
		dispatch(buffer, buffer.remaining());
	}
	
	private void dispatch(ByteBuffer optionData, int length)
	{
		try
		{
			NavData navData = NavData.createFromData(optionData, length);
			
			if(navDataListener!=null){
				navDataListener.navDataUpdated(navData);
			}

		}
		catch (NavDataFormatException e)
		{
			e.printStackTrace();
		}
	}
}