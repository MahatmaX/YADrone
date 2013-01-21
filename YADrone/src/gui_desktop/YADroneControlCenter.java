/*
 * Copyright (c) <2011>, <Shigeo Yoshida>
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
The names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gui_desktop;

import com.shigeodayo.ardrone.ARDrone;


/**
 * sample program for Java application
 * @author shigeo
 *
 */
public class YADroneControlCenter
{	
	private ARDrone ardrone=null;
	
	public YADroneControlCenter(){
		initialize();
	}
	
	private void initialize(){
		try
		{
			ardrone=new ARDrone("192.168.1.1");
			System.out.println("connect drone controller");
			ardrone.connect();
			System.out.println("connect drone navdata");
			ardrone.connectNav();
			System.out.println("connect drone video");
			ardrone.connectVideo();
			System.out.println("start drone");
			ardrone.start();
			
			ardrone.setMaxAltitude(5000); // max 5 meters
			new CCFrame(ardrone);
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
			
			if (ardrone != null)
				ardrone.disconnect();
			System.exit(-1);
		}
	}
		
	public static void main(String args[]){
		new YADroneControlCenter();
	}
}