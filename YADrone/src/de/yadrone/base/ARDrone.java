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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.configuration.ConfigurationManager;
import de.yadrone.base.navdata.NavDataManager;
import de.yadrone.base.video.VideoDecoder;
import de.yadrone.base.video.VideoManager;
import de.yadrone.base.video.xuggler.XugglerDecoder;

public class ARDrone implements IARDrone {

	/** default ip address */
	private static final String IP_ADDRESS = "192.168.1.1";

	private String ipaddr = null;
	private InetAddress inetaddr = null;
	private VideoDecoder videoDecoder = null;

	// managers
	private CommandManager commandManager = null;
	private VideoManager videoManager = null;
	private NavDataManager navdataManager = null;
	private ConfigurationManager configurationManager = null;

	// The speed setting has been moved into this class to allow the commandmanager to stay simple and to be able to do
	// more advanced speed calculations for example based on the actual velocity
	// Should we refactor this into a separate API?
	private int speed = 25;
	private Set<ISpeedListener> speedListener = null;

	/** constructor */
	public ARDrone() {
		this(IP_ADDRESS);
	}

	public ARDrone(String ipaddr) {
		this(ipaddr, new XugglerDecoder());
	}
	
	/**
	 * Create a new instance of a drone's virtual counterpart.
	 * @param ipaddr  The address of the drone, e.g. 192.168.1.1
	 * @param videoDecoder  A decoder instance, e.g. 'new XugglerDecoder' or null, if video shall not be used (e.g. on Android devices)
	 */
	public ARDrone(String ipaddr, VideoDecoder videoDecoder) {
		this.ipaddr = ipaddr;
		this.videoDecoder = videoDecoder;
		this.speedListener = new HashSet<ISpeedListener>();
	}

	public synchronized CommandManager getCommandManager() {
		if (commandManager == null) {
			InetAddress ia = getInetAddress();
			commandManager = new CommandManager(ia);
		}
		return commandManager;
	}

	public synchronized NavDataManager getNavDataManager() {
		if (navdataManager == null) {
			InetAddress ia = getInetAddress();
			CommandManager cm = getCommandManager();
			navdataManager = new NavDataManager(ia, cm);
		}
		return navdataManager;
	}

	public synchronized VideoManager getVideoManager() {
		// videoDecoder may only be null if the corresponding constructor of this class has been called with null
		// this can should be done when working e.g. with Android devices
		if (videoDecoder == null)
			return null;
		
		if (videoManager == null) {
			InetAddress ia = getInetAddress();
			CommandManager cm = getCommandManager();
			videoManager = new VideoManager(ia, cm, videoDecoder);
		}
		return videoManager;
	}

	public synchronized ConfigurationManager getConfigurationManager() {
		if (configurationManager == null) {
			InetAddress ia = getInetAddress();
			CommandManager cm = getCommandManager();
			configurationManager = new ConfigurationManager(ia, cm);
		}
		return configurationManager;
	}

	@Override
	public void stop() {
		freeze();
		landing();
		CommandManager cm = getCommandManager();
		cm.stop();
		ConfigurationManager cfgm = getConfigurationManager();
		cfgm.close();
		NavDataManager nm = getNavDataManager();
		nm.stop();
		VideoManager vm = getVideoManager();
		if (vm != null)
			vm.close();
	}

	@Override
	public void start() {
		CommandManager cm = getCommandManager();
		cm.start();
		ConfigurationManager cfgm = getConfigurationManager();
		cfgm.start();
		NavDataManager nm = getNavDataManager();
		nm.start();
		VideoManager vm = getVideoManager();
		if (vm != null)
			vm.start();
	}

	@Override
	public void setHorizontalCamera() {
		if (commandManager != null)
			commandManager.setVideoChannel(VideoChannel.HORI);
	}

	@Override
	public void setVerticalCamera() {
		if (commandManager != null)
			commandManager.setVideoChannel(VideoChannel.VERT);
	}

	@Override
	public void setHorizontalCameraWithVertical() {
		if (commandManager != null)
			commandManager.setVideoChannel(VideoChannel.LARGE_HORI_SMALL_VERT);
	}

	@Override
	public void setVerticalCameraWithHorizontal() {
		if (commandManager != null)
			commandManager.setVideoChannel(VideoChannel.LARGE_VERT_SMALL_HORI);
	}

	@Override
	public void toggleCamera() {
		if (commandManager != null)
			commandManager.setVideoChannel(VideoChannel.NEXT);
	}

	@Override
	public void landing() {
		if (commandManager != null)
			commandManager.landing();
	}

	@Override
	public void takeOff() {
		if (commandManager != null)
			commandManager.takeOff();
	}

	@Override
	public void reset() {
		if (commandManager != null)
			commandManager.emergency();
	}

	@Override
	public void forward() {
		if (commandManager != null)
			commandManager.forward(speed);
	}

	@Override
	public void backward() {
		if (commandManager != null)
			commandManager.backward(speed);
	}

	@Override
	public void spinRight() {
		if (commandManager != null)
			commandManager.spinRight(speed);
	}

	@Override
	public void spinLeft() {
		if (commandManager != null)
			commandManager.spinLeft(speed);
	}

	@Override
	public void up() {
		if (commandManager != null)
			commandManager.up(speed);
	}

	@Override
	public void down() {
		if (commandManager != null)
			commandManager.down(speed);
	}

	@Override
	public void goRight() {
		if (commandManager != null)
			commandManager.goRight(speed);
	}

	@Override
	public void goLeft() {
		if (commandManager != null)
			commandManager.goLeft(speed);
	}

	@Override
	public void freeze() {
		if (commandManager != null)
			commandManager.freeze();
	}

	public void hover() {
		if (commandManager != null)
			commandManager.hover();
	}
	
	@Override
	public void setMaxAltitude(int altitude) {
		if (commandManager != null)
			commandManager.setMaxAltitude(altitude);
	}

	@Override
	public void setMinAltitude(int altitude) {
		if (commandManager != null)
			commandManager.setMinAltitude(altitude);
	}

	@Override
	public void move3D(int speedX, int speedY, int speedZ, int speedSpin) {
		if (commandManager != null)
			commandManager.move(speedX, speedY, speedZ, speedSpin);
	}

	@Override
	public void setSpeed(int speed) {
		if (this.speed != speed)
		{
			this.speed = speed;
			
			// inform listener
			Iterator<ISpeedListener> iter = speedListener.iterator();
			while(iter.hasNext())
			{
				iter.next().speedUpdated(speed);
			}
		}
		else
		{
			this.speed = speed;
		}
	}

	/**
	 * 0.01-1.0 -> 1-100%
	 * 
	 * @return 1-100%
	 */
	@Override
	public int getSpeed() {
		return speed;
	}
	
	public void addSpeedListener(ISpeedListener speedListener)
	{
		this.speedListener.add(speedListener);
	}
	
	public void removeSpeedListener(ISpeedListener speedListener)
	{
		this.speedListener.remove(speedListener);
	}
	
	public interface ISpeedListener
	{
		public void speedUpdated(int speed);		
	}
	
	/**
	 * print error message
	 * 
	 * @param message
	 * @param obj
	 */
	public static void error(String message, Object obj) {
		System.err.println("[" + obj.getClass() + "] " + message);
	}

	private InetAddress getInetAddress() {
		if (inetaddr == null) {
			StringTokenizer st = new StringTokenizer(ipaddr, ".");
			byte[] ipBytes = new byte[4];
			if (st.countTokens() == 4) {
				for (int i = 0; i < 4; i++) {
					ipBytes[i] = (byte) Integer.parseInt(st.nextToken());
				}
			} else {
				error("Incorrect IP address format: " + ipaddr, this);
				return null;
			}
			try {
				inetaddr = InetAddress.getByAddress(ipBytes);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		return inetaddr;
	}

}