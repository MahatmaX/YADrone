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
package de.yadrone.base.command;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.yadrone.base.exception.CommandException;
import de.yadrone.base.exception.IExceptionListener;
import de.yadrone.base.manager.AbstractManager;
import de.yadrone.base.navdata.CadType;
import de.yadrone.base.utils.ARDroneUtils;

public class CommandManager extends AbstractManager 
{

	public final static String APPLICATION_ID = "aabbccdd";
	public final static String PROFILE_ID = "bbccddee";
	public final static String SESSION_ID = "ccddeeff";
	
	private IExceptionListener excListener;
	private CommandQueue q;
	private Timer timer;

	private static int seq = 1;

	public CommandManager(InetAddress inetaddr, IExceptionListener excListener) {
		super(inetaddr);
		this.q = new CommandQueue(100);
		this.timer = new Timer("YADrone CommandManager Timer");
		this.excListener = excListener;
		initARDrone();
	}

	public void resetCommunicationWatchDog() {
		q.add(new KeepAliveCommand());
	}

	public void setVideoChannel(VideoChannel c) {
		q.add(new VideoChannelCommand(c));
	}

	/**
	 * Wait (sleep) for specified amount of time (same semantics as after() and waitFor() - blocks the calling thread).
	 * This way commands can be executed for a certain amount of time, e.g. fly forward for 2000 ms, then turn right.
	 * @param millis  Number of milliseconds to wait
	 */
	public CommandManager doFor(long millis)
	{
		return waitFor(millis);
	}
	
	/**
	 * Wait (sleep) for specified amount of time (same semantics as doFor() and waitFor() - blocks the calling thread).
	 * This way commands can be executed for a certain amount of time, e.g. fly forward for 2000 ms, then turn right.
	 * @param millis  Number of milliseconds to wait
	 */
	public CommandManager after(long millis)
	{
		return waitFor(millis);
	}
	
	/**
	 * Wait (sleep) for specified amount of time (same semantics as doFor() and after() - blocks the calling thread)
	 * This way commands can be executed for a certain amount of time, e.g. fly forward for 2000 ms, then turn right.
	 * @param millis  Number of milliseconds to wait
	 */
	public CommandManager waitFor(long millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		return this;
	}
	
	/**
	 * Schedule a command set (or arbitrary runnable object) to be executed after a certain amount of time, e.g. fly forward for 2000 ms, then turn right.
	 * In contrast to after(), waitFor() and doFor() this method executes asynchronously, i.e. returns immediately and does not block the calling thread.
	 * @param millis  Number of milliseconds to wait
	 */
	public void schedule(long millis, final Runnable runnable)
	{
		timer.schedule(new TimerTask() {
			public void run()
			{
				new Thread(runnable).start();
			}			
		}, millis);
	}
	
	public CommandManager landing() {
		q.add(new LandCommand());
		return this;
	}

	public CommandManager flatTrim() {
		q.add(new FlatTrimCommand());
		return this;
	}

	public CommandManager manualTrim(float pitch, float roll, float yaw) {
		q.add(new ManualTrimCommand(pitch, roll, yaw));
		return this;
	}

	public CommandManager takeOff() {
		flatTrim();
		q.add(new TakeOffCommand());
		return this;
	}

	/**
	 * See Developer Guide 6.5:
	 */
	public CommandManager emergency() {
		q.add(new EmergencyCommand());
		return this;
	}

	public CommandManager forward(int speed) {
		return move(0f, -perc2float(speed), 0f, 0f);
	}

	public CommandManager backward(int speed) {
		return move(0f, perc2float(speed), 0f, 0f);
	}

	public CommandManager spinRight(int speed) {
		return move(0f, 0f, 0f, perc2float(speed));
	}

	public CommandManager spinLeft(int speed) {
		return move(0f, 0f, 0f, -perc2float(speed));
	}

	public CommandManager up(int speed) {
		return move(0f, 0f, perc2float(speed), 0f);
	}

	public CommandManager down(int speed) {
		return move(0f, 0f, -perc2float(speed), 0f);
	}

	public CommandManager goRight(int speed) {
		return move(perc2float(speed), 0f, 0f, 0f);
	}

	public CommandManager goLeft(int speed) {
		return move(-perc2float(speed), 0f, 0f, 0f);
	}

	public CommandManager move(float lrtilt, float fbtilt, float vspeed, float aspeed, float magneto_psi,
			float magneto_psi_accuracy) {
		lrtilt = limit(lrtilt, -1f, 1f);
		fbtilt = limit(fbtilt, -1f, 1f);
		vspeed = limit(vspeed, -1f, 1f);
		aspeed = limit(aspeed, -1f, 1f);
		magneto_psi = limit(magneto_psi, -1f, 1f);
		magneto_psi_accuracy = limit(magneto_psi_accuracy, -1f, 1f);
		q.add(new PCMDMagCommand(false, false, true, lrtilt, fbtilt, vspeed, aspeed, magneto_psi, magneto_psi_accuracy));
		return this;
	}

	public CommandManager move(float lrtilt, float fbtilt, float vspeed, float aspeed) {
		lrtilt = limit(lrtilt, -1f, 1f);
		fbtilt = limit(fbtilt, -1f, 1f);
		vspeed = limit(vspeed, -1f, 1f);
		aspeed = limit(aspeed, -1f, 1f);
//		System.out.println("CommandManager: Move lrTilt=" + lrtilt + " fbtilt=" + fbtilt + " vspeed=" + vspeed + " aspeed=" + aspeed);
		q.add(new MoveCommand(false, lrtilt, fbtilt, vspeed, aspeed));
		return this;
	}

	public CommandManager move(int speedX, int speedY, int speedZ, int speedSpin) {
		return move(-perc2float(speedY), -perc2float(speedX), -perc2float(speedZ), -perc2float(speedSpin));
	}

	public CommandManager freeze() {
		q.add(new FreezeCommand());
		return this;
	}

	public CommandManager hover() {
		q.add(new HoverCommand());
		return this;
	}
	
	private float perc2float(int speed) {
		return (float) (speed / 100.0f);
	}

	private CommandManager setMulticonfiguration()
	{
		q.add(new ConfigureCommand("custom:session_id", SESSION_ID));
		q.add(new ConfigureCommand("custom:profile_id", PROFILE_ID));
		q.add(new ConfigureCommand("custom:application_id", APPLICATION_ID));
		return this;
	}
	
	public CommandManager setConfigurationIds() {
		q.add(new ConfigureIdsCommand(SESSION_ID, PROFILE_ID, APPLICATION_ID));
		return this;
	}
	
	/**
	 * Set the current FPS of the live video codec.
	 * @param fps  frames per second (min=15, max=30)
	 */
	public CommandManager setVideoCodecFps(int fps) {
		fps = limit(fps, H264.MIN_FPS, H264.MAX_FPS);
		q.add(new ConfigureCommand("video:codec_fps", fps));
		return this;
	}

	/**
	 * Sets the automatic bitrate control of the video stream. Enabling this configuration will reduce the bandwidth
	 * used by the video stream under bad Wi-Fi conditions, reducing the commands latency.
	 * @param mode  VideoBitRateMode.DISABLED  Bitrate set to video:max_bitrate
	 *              VideoBitRateMode.DYNAMIC  Video bitrate varies in [250;video:max_bitrate] kbps
	 *              VideoBitRateMode.MANUAL  Video stream bitrate is fixed by the video:bitrate key
	 */
	public CommandManager setVideoBitrateControl(VideoBitRateMode mode) {
		q.add(new ConfigureCommand("video:bitrate_control_mode", mode.ordinal()));
		return this;
	}

	/**
	 * Sets the current bitrate of the video transmission (kilobits per second)
	 * @param rate  bitrate (min=250, max=4000)
	 */
	public CommandManager setVideoBitrate(int rate) {
		rate = limit(rate, H264.MIN_BITRATE, H264.MAX_BITRATE);
		q.add(new ConfigureCommand("video:bitrate", rate));
		return this;
	}

	/**
	 * Sets the maximum bitrate of the video transmission (kilobits per second)
	 * @param rate  bitrate (min=250, max=4000)
	 */
	public CommandManager setMaxVideoBitrate(int rate) {
		rate = limit(rate, H264.MIN_BITRATE, H264.MAX_BITRATE);
		q.add(new ConfigureCommand("video:max_bitrate", rate));
		return this;
	}

	/**
	 * Set the current video codec of the AR.Drone.
	 * Possible codec values for AR.Drone 2.0 are :
	 * MP4_360P_CODEC : Live stream with MPEG4.2 soft encoder. No record stream.
	 * H264_360P_CODEC : Live stream with H264 hardware encoder configured in 360p mode. No record stream.
	 * MP4_360P_H264_720P_CODEC : Live stream with MPEG4.2 soft encoder. Record stream with H264 hardware encoder in 720p mode.
	 * H264_720P_CODEC : Live stream with H264 hardware encoder configured in 720p mode. No record stream.
	 * MP4_360P_H264_360P_CODEC : Live stream with MPEG4.2 soft encoder. Record stream with H264 hardware encoder in 360p mode.
	 * @param c  The video codec to use.
	 */
	public CommandManager setVideoCodec(VideoCodec c) {
		q.add(new ConfigureCommand("video:video_codec", c.getValue()));
		return this;
	}

	/**
	 * If this key is set to "TRUE" and a USB key with >100Mb of freespace is connected, the record
	 * video stream will be recorded on the USB key.
	 * @param b  If TRUE, video stream will be recorded
	 */
	public CommandManager setVideoOnUsb(boolean b) {
		q.add(new ConfigureCommand("video:video_on_usb", b ? "TRUE" : "FALSE"));
		return this;
	}

//	Reserved for future use, The default value is TRUE.
//	public void setVideoData(boolean b) {
//		q.add(new ConfigureCommand("general:video_enable", b));
//	}

	/**
	 * The drone can either send a reduced set of navigation data (navdata) to its clients, or send all the available information
	 * which contain many debugging information that are useless for everyday flights.
	 * @param b  If set to TRUE, a reduced set is sent. If set to FALSE, all the available data are sent.
	 */
	public CommandManager setNavDataDemo(boolean b) {
		q.add(new ConfigureCommand("general:navdata_demo", b));
		return this;
	}

	/**
	 * When using navdata_demo, this configuration allows the application to ask for others navdata packets.
	 * @param mask  I honestly do not know, where values for this mask are defined. Have a look at the User Guide (page 74 in v2.0.1)
	 */
	public CommandManager setNavDataOptions(int mask) {
		q.add(new ConfigureCommand("general:navdata_options", mask));
		return this;
	}

	/**
	 * Animate the LED lights.
	 * @param anim  The animation.
	 * @param freq  It's frequency
	 * @param duration  The duration in seconds
	 */
	public CommandManager setLedsAnimation(LEDAnimation anim, float freq, int duration) {
		q.add(new LEDAnimationCommand(anim, freq, duration));
		return this;
	}

	/**
	 * Activate this in order to detect outdoor hulls. Deactivate to detect indoor hulls.
	 * @param b  TRUE for outdoor, FALSE for indoor hulls
	 */
	public CommandManager setDetectEnemyWithoutShell(boolean b) {
		q.add(new ConfigureCommand("detect:enemy_without_shell", (b ? "1" : "0")));
		return this;
	}

//	Only for ARDrone 1.0 with legacy groundstripe detection.
//	public void setGroundStripeColors(GroundStripeColor c) {
//		q.add(new ConfigureCommand("detect:groundstripe_colors", c.getValue()));
//	}

	/**
	 * The color of the hulls you want to detect.
	 * @param c  Possible values are green (1), yellow (2) and blue (3)
	 */
	public CommandManager setEnemyColors(EnemyColor c) {
		q.add(new ConfigureCommand("detect:enemy_colors", c.getValue()));
		return this;
	}

	/**
	 * Select the detection that should be enabled 
	 * Note: It is advised to enable the multiple detection mode, and then configure the detection needed using the following keys.
	 * 
	 * NOTE: The multiple detection mode allow the selection of different detections on each camera. 
	 * Note that you should NEVER enable two similar detection on both cameras, as this will cause failures in the algorithms
	 */
	public CommandManager setDetectionType(CadType type) {
		// TODO: push VisionCadType into special ConfigureCommand
		int t = type.ordinal();
		q.add(new ConfigureCommand("detect:detect_type", t));
		return this;
	}

	/**
	 * Select the detection that should be enabled 
	 * Note: It is advised to enable the multiple detection mode, and then configure the detection needed using the following keys.
	 * 
	 * NOTE: The multiple detection mode allow the selection of different detections on each camera. 
	 * Note that you should NEVER enable two similar detection on both cameras, as this will cause failures in the algorithms
	 */
	public CommandManager setDetectionType(DetectionType dt, VisionTagType[] tagtypes) {
		int mask = VisionTagType.getMask(tagtypes);
		q.add(new ConfigureCommand("detect:" + dt.getCmdSuffix(), mask));
		return this;
	}

	public CommandManager setVisionParameters(int coarse_scale, int nb_pair, int loss_per, int nb_tracker_width,
			int nb_tracker_height, int scale, int trans_max, int max_pair_dist, int noise) {
		q.add(new VisionParametersCommand(coarse_scale, nb_pair, loss_per, nb_tracker_width, nb_tracker_height, scale,
				trans_max, max_pair_dist, noise));
		return this;
	}

	// TODO find out if still supported in Drone 2.0 and what are the options
	public CommandManager setVisionOption(int option) {
		q.add(new VisionOptionCommand(option));
		return this;
	}

	// TODO find out if still supported in Drone 2.0
	public CommandManager setGains(int pq_kp, int r_kp, int r_ki, int ea_kp, int ea_ki, int alt_kp, int alt_ki, int vz_kp,
			int vz_ki, int hovering_kp, int hovering_ki, int hovering_b_kp, int hovering_b_ki) {
		q.add(new GainsCommand(pq_kp, r_kp, r_ki, ea_kp, ea_ki, alt_kp, alt_ki, vz_kp, vz_ki, hovering_kp, hovering_ki,
				hovering_b_kp, hovering_b_ki));
		return this;
	}

	// TODO find out if still supported in Drone 2.0
	public CommandManager setRawCapture(boolean picture, boolean video) {
		q.add(new RawCaptureCommand(picture, video));
		return this;
	}

	/**
	 * This configuration describes how the drone will interprete the progressive commands from the user.
	 * In the combined yaw mode, the roll commands are used to generate roll+yaw based turns. 
	 * This is intended to be an easier control mode for racing games.
	 * @param b  TRUE, to enable combined raw mode, FALSE to disable.
	 */
	public CommandManager setEnableCombinedYaw(boolean b) {
		int level = 1;
		if (b) {
			level |= (1 << 2);
		}
		q.add(new ConfigureCommand("control:control_level", level));
		return this;
	}

	/**
	 * Since 1.5.1 firmware, the AR.Drone has two different flight modes. The first is the legacy FreeFlight mode, where the
	 * user controls the drone, an a new semi-autonomous mode, called "HOVER_ON_TOP_OF_ROUNDEL", where the
	 * drones will hover on top of a ground tag. This new flying mode was developped for 2011 CES autonomous demonstration.
	 * Since 2.0 and 1.10 firmwares, a third mode, called "HOVER_ON_TOP_OF_ORIENTED_ROUDNEL", was
	 * added. This mode is the same as the previous one, except that the AR.Drone will always face the same direction.
	 * @param mode
	 */
	public CommandManager setFlyingMode(FlyingMode mode) {
		q.add(new ConfigureCommand("control:flying_mode", mode.ordinal()));
		return this;
	}

	/**
	 * This setting is used when CONTROL:flying_mode is set to "HOVER_ON_TOP_OF_(ORIENTED_)ROUNDEL". It
	 * gives the AR.Drone the maximum distance (in millimeters) allowed between the AR.Drone and the oriented roundel.
	 * @param range  maximum distance (in millimeters)
	 */
	public CommandManager setHoveringRange(int range) {
		q.add(new ConfigureCommand("control:hovering_range", range));
		return this;
	}

	/**
	 * Set the maximum bending angle (euler angle).
	 * @param angle  Maximum bending angle for the drone in radians, for both pitch and roll angles.
	 *               This parameter is a positive floating-point value between 0 and 0.52 (ie. 30 deg).
	 */
	public CommandManager setMaxEulerAngle(float angle) {
		setMaxEulerAngle(Location.CURRENT, angle);
		return this;
	}

	/**
	 * Set the maximum bending angle (euler angle).
	 * @param l
	 * @param angle  Maximum bending angle for the drone in radians, for both pitch and roll angles.
	 *               This parameter is a positive floating-point value between 0 and 0.52 (ie. 30 deg).
	 */
	public CommandManager setMaxEulerAngle(Location l, float angle) {
		angle = limit(angle, 0f, 0.52f);
		System.out.println("CommandManager: setMaxEulerAngle (bendingAngle): " + angle + " rad");
		String command = "control:" + l.getCommandPrefix() + "euler_angle_max";
		q.add(new ConfigureCommand(command, String.valueOf(angle)));
		return this;
	}

	/**
	 * Set a maximum altitude for the drone.
	 * @param altitude  Altitude in millimeters (max. 100000 = 100m)
	 */
	public CommandManager setMaxAltitude(int altitude) {
		setMaxAltitude(Location.CURRENT, altitude);
		return this;
	}

	/**
	 * Set a maximum altitude for the drone.
	 * @param l
	 * @param altitude  Altitude in millimeters (max. 100000 = 100m)
	 */
	public CommandManager setMaxAltitude(Location l, int altitude) {
		altitude = limit(altitude, 0, 100000);
		System.out.println("CommandManager: setMaxAltitude: " + altitude + " mm");
		String command = "control:" + l.getCommandPrefix() + "altitude_max";
		q.add(new ConfigureCommand(command, altitude));
		return this;
	}

	/**
	 * Set a minimum altitude for the drone.
	 * @param altitude  Altitude in millimeters
	 */
	public CommandManager setMinAltitude(int altitude) {
		setMinAltitude(Location.CURRENT, altitude);
		return this;
	}

	/**
	 * Set a minimum altitude for the drone.
	 * @param l
	 * @param altitude  Altitude in millimeters
	 */
	public CommandManager setMinAltitude(Location l, int altitude) {
		altitude = limit(altitude, 0, 100000);
		String command = "control:" + l.getCommandPrefix() + "altitude_min";
		q.add(new ConfigureCommand(command, altitude));
		return this;
	}

	/**
	 * Set the maximum vertical speed of the drone.
	 * @param speed  Maximum vertical speed of the AR.Drone, in milimeters per second.
	 *               Recommanded values goes from 200 to 2000. Others values may cause instability.
	 */
	public CommandManager setMaxVz(int speed) {
		setMaxVz(Location.CURRENT, speed);
		return this;
	}

	/**
	 * Set the maximum vertical speed of the drone.
	 * @param l
	 * @param speed  Maximum vertical speed of the AR.Drone, in milimeters per second.
	 *               Recommanded values goes from 200 to 2000. Others values may cause instability.
	 */
	public CommandManager setMaxVz(Location l, int speed) {
		speed = limit(speed, 0, 2000);
		System.out.println("CommandManager: setMaxVz (verticalSpeed): " + speed + " mm");
		String command = "control:" + l.getCommandPrefix() + "control_vz_max";
		q.add(new ConfigureCommand(command, speed));
		return this;
	}

	/**
	 * Set the maximum yaw speed of the AR.Drone, in radians per second.
	 * @param speed  Maximum yaw speed of the AR.Drone, in radians per second.
     *               Recommended values go from 40/s to 350/s (approx 0.7rad/s to 6.11rad/s). Others values may cause instability.
	 */
	public CommandManager setMaxYaw(float speed) {
		setMaxYaw(Location.CURRENT, speed);
		return this;
	}

	/**
	 * Set the maximum yaw speed of the AR.Drone, in radians per second.
	 * @param l
	 * @param speed  Maximum yaw speed of the AR.Drone, in radians per second.
     *               Recommended values go from 40/s to 350/s (approx 0.7rad/s to 6.11rad/s). Others values may cause instability.
	 */
	public CommandManager setMaxYaw(Location l, float speed) {
		speed = limit(speed, 0.7f, 6.11f);
		String command = "control:" + l.getCommandPrefix() + "control_yaw";
		q.add(new ConfigureCommand(command, speed));
		return this;
	}

	public CommandManager setCommand(ATCommand command) {
		q.add(command);
		return this;
	}

	/**
	 * This settings tells the control loop if the AR.Drone is flying outside with or without it outdoor hull.
	 * @param flying_outdoor  TRUE, if flying outdoor. FALSE, if flying indoor
	 * @param outdoor_hull  TRUE, if outdoor shell is used. FALSE, if indoor shell is used.
	 */
	public CommandManager setOutdoor(boolean flying_outdoor, boolean outdoor_hull) {
		System.out.println("CommandManager: setOutdoor(flyingOutdoor,usingOutdoorHull) = " + flying_outdoor + "," + outdoor_hull);
		q.add(new ConfigureCommand("control:outdoor", flying_outdoor));
		q.add(new ConfigureCommand("control:flight_without_shell", outdoor_hull));
		return this;
	}

//	@Deprecated
//	public void setAutonomousFlight(boolean b) {
//		q.add(new ConfigureCommand("control:autonomous_flight", b));
//	}

//	Shoud not be used with commercial AR.Drones
//	public void setManualTrim(boolean b) {
//		q.add(new ConfigureCommand("control:manual_trim", b));
//	}

//	Why should we offer this in Java ?
//	public void setPhoneTilt(float tilt) {
//		q.add(new ConfigureCommand("control:control_iphone_tilt", String.valueOf(tilt)));
//	}

	public CommandManager animate(FlightAnimation a) {
		q.add(new FlightAnimationCommand(a));
		return this;
	}

	public CommandManager setPosition(double latitude, double longitude, double altitude) {
		q.add(new ConfigureCommand("gps:latitude", latitude));
		q.add(new ConfigureCommand("gps:longitude", longitude));
		q.add(new ConfigureCommand("gps:altitude", altitude));
		return this;
	}

	/**
	 * Set the frequency of the ultrasound measures for altitude. Using two different frequencies can reduce significantly the
	 * ultrasound perturbations between two AR.Drones.
	 * @param f  Only two frequencies are availaible : 22:22 and 25 Hz.
	 */
	public CommandManager setUltrasoundFrequency(UltrasoundFrequency f) {
		q.add(new ConfigureCommand("pic:ultrasound_freq", f.getValue()));
		return this;
	}

	/**
	 * The AR.Drone SSID. Changes are applied on reboot.
	 * @param ssid  The new SSID, e.g. "myArdroneNetwork"
	 */
	public CommandManager setSSIDSinglePlayer(String ssid) {
		q.add(new ConfigureCommand("network:ssid_single_player", ssid));
		return this;
	}

	/**
	 * The AR.Drone SSID for multi player. Currently unused.
	 * @param ssid  The new SSID, e.g. "myArdroneNetwork"
	 */
	public CommandManager setSSIDMultiPlayer(String ssid) {
		q.add(new ConfigureCommand("network:ssid_multi_player", ssid));
		return this;
	}

	/**
	 * Change the mode of the Wi-Fi network. 
	 * Note : This value should not be changed for users applications.
	 * Possible values are :
	 * 0 : The drone is the access point of the network
	 * 1 : The drone creates (or join) the network in Ad-Hoc mode
	 * 2 : The drone tries to join the network as a station
	 * @param mode
	 */
	public CommandManager setWifiMode(WifiMode mode) {
		q.add(new ConfigureCommand("network:wifi_mode", mode.ordinal()));
		return this;
	}

	/**
	 * Set the MAC address paired with the AR.Drone. Set to "00:00:00:00:00:00" to unpair the AR.Drone.
	 * @param mac  The new MAC address.
	 */
	public CommandManager setOwnerMac(String mac) {
		q.add(new ConfigureCommand("network:owner_mac", mac));
		return this;
	}

	public CommandManager startRecordingNavData(String dirname) {
		q.add(new ConfigureCommand("userbox:userbox_cmd", String.valueOf(UserBox.START.ordinal()) + "," + dirname));
		return this;
	}

	public CommandManager cancelRecordingNavData() {
		q.add(new ConfigureCommand("userbox:userbox_cmd", UserBox.CANCEL.ordinal()));
		return this;
	}

	public CommandManager stopRecordingNavData() {
		q.add(new ConfigureCommand("userbox:userbox_cmd", UserBox.STOP.ordinal()));
		return this;
	}

	private static final SimpleDateFormat USERBOXFORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);

	public CommandManager startRecordingPictures(int delay, int nshots) {
		Date d = new Date();
		final String label = USERBOXFORMAT.format(d);
		q.add(new ConfigureCommand("userbox:userbox_cmd", String.valueOf(UserBox.SCREENSHOT.ordinal()) + ","
				+ String.valueOf(delay) + "," + String.valueOf(nshots) + "," + label));
		return this;
	}

//	public URL[] getRecordedNavDataURLs() throws IOException {
//		URLRetriever r = new URLRetriever(inetaddr, "anonymous", "");
//		return r.getURLs(new NavDataFileFilter());
//	}
//
//	private static class URLRetriever {
//		private String user;
//		private String pass;
//		private InetAddress address;
//
//		public URLRetriever(InetAddress address, String user, String pass) {
//			this.address = address;
//			this.user = user;
//			this.pass = pass;
//		}
//
//		public String getUserBoxDir() {
//			return "/boxes/";
//		}
//
//		public URL[] getURLs(FTPFileFilter filter) throws IOException {
//			FTPClient ftp = login();
//
//			ArrayList<URL> urls = new ArrayList<URL>();
//			FTPFile[] dirs = ftp.listFiles("", new UserboxFileFilter());
//			for (FTPFile dir : dirs) {
//				FTPFile[] files = ftp.listFiles(getUserBoxDir() + dir.getName(), filter);
//				for (FTPFile file : files) {
//					try {
//						URL url = new URL("ftp://" + user + ":" + pass + "@" + address.getHostName() + getUserBoxDir()
//								+ dir.getName() + File.separator + file.getName());
//						System.out.println("PICTURE: " + url);
//						urls.add(url);
//					} catch (MalformedURLException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//
//			logout(ftp);
//
//			return urls.toArray(new URL[urls.size()]);
//		}
//
//		public Bitmap[] getBitmaps() throws IOException {
//			FTPClient ftp = login();
//
//			ArrayList<Bitmap> bmps = new ArrayList<Bitmap>();
//			FTPFile[] dirs = ftp.listFiles(getUserBoxDir(), new UserboxFileFilter());
//			for (FTPFile dir : dirs) {
//				FTPFile[] files = ftp.listFiles(getUserBoxDir() + dir.getName(), new JPEGFileFilter());
//				for (FTPFile file : files) {
//					try {
//						InputStream is = ftp.retrieveFileStream(getUserBoxDir() + dir.getName() + File.separator
//								+ file.getName());
//						Bitmap bmp = BitmapFactory.decodeStream(is);
//						bmps.add(bmp);
//						is.close();
//						ftp.completePendingCommand();
//					} catch (MalformedURLException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//
//			logout(ftp);
//
//			return bmps.toArray(new Bitmap[bmps.size()]);
//		}
//
//		private FTPClient login() throws SocketException, IOException {
//			FTPClient ftp = new FTPClient();
//			ftp.connect(address);
//
//			int reply = ftp.getReplyCode();
//			if (!FTPReply.isPositiveCompletion(reply)) {
//				ftp.disconnect();
//				throw new IOException("FTP server refused connection.");
//			}
//
//			ftp.enterLocalPassiveMode();
//			ftp.login(user, pass);
//
//			return ftp;
//		}
//
//		private void logout(FTPClient ftp) throws IOException {
//			ftp.logout();
//		}
//
//	}
//
//	public Bitmap[] getRecordedPictures() throws IOException {
//		URLRetriever r = new URLRetriever(inetaddr, "anonymous", "");
//		return r.getBitmaps();
//	}
//
//	public URL[] getRecordedPictureURLs() throws IOException {
//		URLRetriever r = new URLRetriever(inetaddr, "anonymous", "");
//		return r.getURLs(new JPEGFileFilter());
//	}

	// AT*MISC undocumented, but needed to initialize
	// see https://github.com/bklang/ARbDrone/wiki/UndocumentedCommands
	private void sendMisc(int p1, int p2, int p3, int p4) {
		q.add(new MiscCommand(p1, p2, p3, p4));
	}

	// AT*PMODE undocumented, but needed to initialize
	// see https://github.com/bklang/ARbDrone/wiki/UndocumentedCommands
	private void sendPMode(int mode) {
		q.add(new PMODECommand(mode));
	}

	/**
	 * Some assumptions:
	 * <ul>
	 * <li>sticky commands do not need confirmation
	 * <li>there can be only one sticky command active
	 * <li>one sticky command replaced the previous one by definition
	 * <li>sticky commands do not need acknowledgement
	 * </ul>
	 */
	@Override
	public void run() {
		connect(ARDroneUtils.PORT);
		ATCommand c;
		ATCommand cs = null;
		final ATCommand cAck = new ResetControlAckCommand();
		final ATCommand cAlive = new KeepAliveCommand();
		long t0 = 0;
		while (!doStop) {
			try {
				long dt;
				if (cs == null) {
					// we need to reset the watchdog within 50ms
					dt = 40;
				} else {
					// if there is a sticky command, we can wait until we need to deliver it.
					long t = System.currentTimeMillis();
					dt = t - t0;
				}
				c = q.poll(dt, TimeUnit.MILLISECONDS);
				// System.out.println(c);
				if (c == null) {
					if (cs == null) {
						c = cAlive;
					} else {
						c = cs;
						t0 = System.currentTimeMillis();
					}
				} else {
					if (c.isSticky()) {
						// sticky commands replace previous sticky
						cs = c;
						t0 = System.currentTimeMillis();
					} else if (c.clearSticky()) {
						// only some commands can clear sticky commands
						cs = null;
					}
				}
				if (c.needControlAck()) {
					waitForControlAck(false);
					sendCommand(c);
					waitForControlAck(true);
					sendCommand(cAck);
				} else {
					sendCommand(c);
				}
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
				doStop = true;
				excListener.exeptionOccurred(new CommandException(e));
			} 
			catch (Throwable t) {
				t.printStackTrace();
				excListener.exeptionOccurred(new CommandException(t));
			}
		}
		close();
		timer.cancel();
		System.out.println("doStop() called ? " + doStop + " ... Stopped " + getClass().getSimpleName());
	}

	private CommandManager initARDrone() 
	{
		new Thread(new Runnable() {
			public void run()
			{
				setMulticonfiguration();
			}			
		}).start();;
		
		waitFor(5000);
		
		// pmode parameter and first misc parameter are related
		sendPMode(2);
		sendMisc(2, 20, 2000, 3000);
		freeze();
		landing();
		
		setOutdoor(false, false);
		setMaxAltitude(10000);
		setMaxVz(1000);
		setMaxEulerAngle(0.25f);
		return this;
	}

	private synchronized void sendCommand(ATCommand c) throws InterruptedException, IOException {
		if (!(c instanceof KeepAliveCommand)) {
			 System.out.println("CommandManager: send " + c.getCommandString(seq));
		}
		
		String config = "AT*CONFIG_IDS=" + (seq++) + ",\"" + CommandManager.SESSION_ID + "\",\"" + CommandManager.PROFILE_ID +"\",\"" + CommandManager.APPLICATION_ID + "\"" + "\r"; // AT*CONFIG_IDS=5,"aabbccdd","bbccddee","ccddeeff"
		byte[] configPrefix = config.getBytes("ASCII");
		
		byte[] command = c.getPacket(seq++);
		
		byte[] buffer = new byte[configPrefix.length + command.length];
		System.arraycopy(configPrefix, 0, buffer, 0, configPrefix.length);
		System.arraycopy(command, 0, buffer, configPrefix.length, command.length);
		
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inetaddr, ARDroneUtils.PORT);
		socket.send(packet);
	}

	private int limit(int i, int min, int max) {
		return (i > max ? max : (i < min ? min : i));
	}

	private float limit(float f, float min, float max) {
		return (f > max ? max : (f < min ? min : f));
	}

	/*
	 * TODO consider to refactor controlAck handling into a separate class shared by NavDataManager and CommandManager
	 */
	private Object controlAckLock = new Object();
	private boolean controlAck = false;

	public CommandManager setControlAck(boolean b) {
		synchronized (controlAckLock) {
			controlAck = b;
			controlAckLock.notifyAll();
			return this;
		}
	}

	private void waitForControlAck(boolean b) throws InterruptedException {
		if (controlAck != b) {
			int n = 1;
			synchronized (controlAckLock) {
				while (n > 0 && controlAck != b) {
					controlAckLock.wait(50);
					n--;
				}
			}
			if (n == 0 && controlAck != b) {
				System.err.println("Control ack timeout " + String.valueOf(b));
				excListener.exeptionOccurred(new CommandException(new RuntimeException("Control ACK timeout")));
			}
		}
	}

}
