/*
 *
  Copyright (c) <2011>, <Shigeo Yoshida>
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
package de.yadrone.base;


import de.yadrone.base.ARDrone.ISpeedListener;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.configuration.ConfigurationManager;
import de.yadrone.base.navdata.NavDataManager;
import de.yadrone.base.video.VideoManager;



public interface IARDrone {

	public CommandManager getCommandManager();
	public NavDataManager getNavDataManager();
	public VideoManager getVideoManager();
	public ConfigurationManager getConfigurationManager();
	
	
	//TODO: cleanup, if kept, factory method should be created for ARDroneInterface
	
	public void start();
	public void stop();
	
	//camera
	public void setHorizontalCamera();//setFrontCameraStreaming()
	public void setVerticalCamera();//setBellyCameraStreaming()
	public void setHorizontalCameraWithVertical();//setFrontCameraWithSmallBellyStreaming()
	public void setVerticalCameraWithHorizontal();//setBellyCameraWithSmallFrontStreaming()
	public void toggleCamera();
	
	//control command
	public void landing();
	public void takeOff();
	public void reset();
	public void forward();
	public void backward();
	public void spinRight();
	public void spinLeft();
	public void up();
	public void down();
	public void goRight();
	public void goLeft();
	public void freeze();
	public void hover();
	
	//getter
	public int getSpeed();
	public void setSpeed(int speed);
	public void addSpeedListener(ISpeedListener speedListener);
	public void removeSpeedListener(ISpeedListener speedListener);
	
	//set max/min altitude
	public void setMaxAltitude(int altitude);
	public void setMinAltitude(int altitude);
	
	public void move3D(int speedX, int speedY, int speedZ, int speedSpin);
}
