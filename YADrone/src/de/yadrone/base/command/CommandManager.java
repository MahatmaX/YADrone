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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;


import de.yadrone.base.manager.AbstractManager;
import de.yadrone.base.navdata.CadType;
import de.yadrone.base.utils.ARDroneUtils;

public class CommandManager extends AbstractManager {

	private CommandQueue q;

	private static int seq = 1;

	public CommandManager(InetAddress inetaddr) {
		super(inetaddr);
		this.q = new CommandQueue(100);
		initARDrone();
	}

	public void resetCommunicationWatchDog() {
		q.add(new KeepAliveCommand());
	}

	public void setVideoChannel(VideoChannel c) {
		q.add(new VideoChannelCommand(c));
	}

	public void landing() {
		q.add(new LandCommand());
	}

	public void flatTrim() {
		q.add(new FlatTrimCommand());
	}

	public void manualTrim(float pitch, float roll, float yaw) {
		q.add(new ManualTrimCommand(pitch, roll, yaw));
	}

	public void takeOff() {
		flatTrim();
		q.add(new TakeOffCommand());
	}

	/**
	 * See Developer Guide 6.5:
	 */
	public void emergency() {
		q.add(new EmergencyCommand());
	}

	public void forward(int speed) {
		move(0f, -perc2float(speed), 0f, 0f);
	}

	public void backward(int speed) {
		move(0f, perc2float(speed), 0f, 0f);
	}

	public void spinRight(int speed) {
		move(0f, 0f, 0f, perc2float(speed));
	}

	public void spinLeft(int speed) {
		move(0f, 0f, 0f, -perc2float(speed));
	}

	public void up(int speed) {
		move(0f, 0f, perc2float(speed), 0f);
	}

	public void down(int speed) {
		move(0f, 0f, -perc2float(speed), 0f);
	}

	public void goRight(int speed) {
		move(perc2float(speed), 0f, 0f, 0f);
	}

	public void goLeft(int speed) {
		move(-perc2float(speed), 0f, 0f, 0f);
	}

	public void move(float lrtilt, float fbtilt, float vspeed, float aspeed, float magneto_psi,
			float magneto_psi_accuracy) {
		lrtilt = limit(lrtilt, -1f, 1f);
		fbtilt = limit(fbtilt, -1f, 1f);
		vspeed = limit(vspeed, -1f, 1f);
		aspeed = limit(aspeed, -1f, 1f);
		magneto_psi = limit(magneto_psi, -1f, 1f);
		magneto_psi_accuracy = limit(magneto_psi_accuracy, -1f, 1f);
		q.add(new PCMDMagCommand(false, false, true, lrtilt, fbtilt, vspeed, aspeed, magneto_psi, magneto_psi_accuracy));
	}

	public void move(float lrtilt, float fbtilt, float vspeed, float aspeed) {
		lrtilt = limit(lrtilt, -1f, 1f);
		fbtilt = limit(fbtilt, -1f, 1f);
		vspeed = limit(vspeed, -1f, 1f);
		aspeed = limit(aspeed, -1f, 1f);
		System.out.println("Move lrTilt=" + lrtilt + " fbtilt=" + fbtilt + " vspeed=" + vspeed + " aspeed=" + aspeed);
		q.add(new MoveCommand(false, lrtilt, fbtilt, vspeed, aspeed));
	}

	public void move(int speedX, int speedY, int speedZ, int speedSpin) {
		move(-perc2float(speedY), -perc2float(speedX), -perc2float(speedZ), -perc2float(speedSpin));
	}

	public void freeze() {
		q.add(new FreezeCommand());
	}

	public void hover() {
		q.add(new HoverCommand());
	}
	
	private float perc2float(int speed) {
		return (float) (speed / 100.0f);
	}

	public void setVideoCodecFps(int fps) {
		fps = limit(fps, H264.MIN_FPS, H264.MAX_FPS);
		q.add(new ConfigureCommand("video:codec_fps", fps));
	}

	/**
	 * Sets the automatic bitrate control of the video stream.
	 */
	public void setVideoBitrateControl(VideoBitRateMode mode) {
		q.add(new ConfigureCommand("video:bitrate_control_mode", mode.ordinal()));
	}

	public void setVideoBitrate(int rate) {
		rate = limit(rate, H264.MIN_BITRATE, H264.MAX_BITRATE);
		q.add(new ConfigureCommand("video:bitrate", rate));
	}

	public void setMaxVideoBitrate(int rate) {
		rate = limit(rate, H264.MIN_BITRATE, H264.MAX_BITRATE);
		q.add(new ConfigureCommand("video:max_bitrate", rate));
	}

	public void setVideoCodec(VideoCodec c) {
		q.add(new ConfigureCommand("video:video_codec", c.getValue()));
	}

	public void setVideoOnUsb(boolean b) {
		q.add(new ConfigureCommand("video:video_on_usb", b));
	}

	public void setVideoData(boolean b) {
		q.add(new ConfigureCommand("general:video_enable", b));
	}

	public void setNavDataDemo(boolean b) {
		q.add(new ConfigureCommand("general:navdata_demo", b));
	}

	public void setNavDataOptions(int mask) {
		q.add(new ConfigureCommand("general:navdata_options", mask));
	}

	public void setLedsAnimation(LEDAnimation anim, float freq, int duration) {
		q.add(new LEDAnimationCommand(anim, freq, duration));
	}

	public void setDetectEnemyWithoutShell(boolean b) {
		q.add(new ConfigureCommand("detect:enemy_colors", (b ? "1" : "0")));
	}

	// TODO Developer guide uses hex representation in the command;
	// is this necessary?
	public void setGroundStripeColors(GroundStripeColor c) {
		q.add(new ConfigureCommand("detect:enemy_colors", c.getValue()));
	}

	public void setEnemyColors(EnemyColor c) {
		q.add(new ConfigureCommand("detect:enemy_colors", c.getValue()));
	}

	/*
	 * Select the detection that should be enabled Note: It is advised to enable the multiple detection mode, and then
	 * configure the detection needed using the other methods
	 * 
	 * NOTE: You should NEVER enable one detection on two different cameras.
	 */
	public void setDetectionType(CadType type) {
		// TODO: push VisionCadType into special ConfigureCommand
		int t = type.ordinal();
		q.add(new ConfigureCommand("detect:detect_type", t));
	}

	/*
	 * Select the detections that should be enabled on a specific camera.
	 * 
	 * 
	 * NOTE: You should NEVER enable one detection on two different cameras.
	 */
	public void setDetectionType(DetectionType dt, VisionTagType[] tagtypes) {
		int mask = VisionTagType.getMask(tagtypes);
		q.add(new ConfigureCommand("detect:" + dt.getCmdSuffix(), mask));
	}

	public void setVisionParameters(int coarse_scale, int nb_pair, int loss_per, int nb_tracker_width,
			int nb_tracker_height, int scale, int trans_max, int max_pair_dist, int noise) {
		q.add(new VisionParametersCommand(coarse_scale, nb_pair, loss_per, nb_tracker_width, nb_tracker_height, scale,
				trans_max, max_pair_dist, noise));
	}

	// TODO find out if still supported in Drone 2.0 and what are the options
	public void setVisionOption(int option) {
		q.add(new VisionOptionCommand(option));
	}

	// TODO find out if still supported in Drone 2.0
	public void setGains(int pq_kp, int r_kp, int r_ki, int ea_kp, int ea_ki, int alt_kp, int alt_ki, int vz_kp,
			int vz_ki, int hovering_kp, int hovering_ki, int hovering_b_kp, int hovering_b_ki) {
		q.add(new GainsCommand(pq_kp, r_kp, r_ki, ea_kp, ea_ki, alt_kp, alt_ki, vz_kp, vz_ki, hovering_kp, hovering_ki,
				hovering_b_kp, hovering_b_ki));
	}

	// TODO find out if still supported in Drone 2.0
	public void setRawCapture(boolean picture, boolean video) {
		q.add(new RawCaptureCommand(picture, video));
	}

	public void setEnableCombinedYaw(boolean b) {
		int level = 1;
		if (b) {
			level |= (1 << 2);
		}
		q.add(new ConfigureCommand("control:control_level", level));
	}

	public void setFlyingMode(FlyingMode mode) {
		q.add(new ConfigureCommand("control:flying_mode", mode.ordinal()));
	}

	public void setHoveringRange(int range) {
		q.add(new ConfigureCommand("control:hovering_range", range));
	}

	public void setMaxEulerAngle(float angle) {
		setMaxEulerAngle(Location.CURRENT, angle);
	}

	public void setMaxEulerAngle(Location l, float angle) {
		String command = "control:" + l.getCommandPrefix() + "euler_angle_max";
		q.add(new ConfigureCommand(command, String.valueOf(angle)));
	}

	public void setMaxAltitude(int altitude) {
		setMaxAltitude(Location.CURRENT, altitude);
	}

	public void setMaxAltitude(Location l, int altitude) {
		String command = "control:" + l.getCommandPrefix() + "altitude_max";
		q.add(new ConfigureCommand(command, altitude));
	}

	public void setMinAltitude(int altitude) {
		setMinAltitude(Location.CURRENT, altitude);
	}

	public void setMinAltitude(Location l, int altitude) {
		String command = "control:" + l.getCommandPrefix() + "altitude_min";
		q.add(new ConfigureCommand(command, altitude));
	}

	public void setMaxVz(int speed) {
		setMaxVz(Location.CURRENT, speed);
	}

	public void setMaxVz(Location l, int speed) {
		String command = "control:" + l.getCommandPrefix() + "control_vz_max";
		q.add(new ConfigureCommand(command, speed));
	}

	public void setMaxYaw(int speed) {
		setMaxYaw(Location.CURRENT, speed);
	}

	public void setMaxYaw(Location l, int speed) {
		String command = "control:" + l.getCommandPrefix() + "control_yaw";
		q.add(new ConfigureCommand(command, speed));
	}

	public void setCommand(ATCommand command) {
		q.add(command);
	}

	public void setOutdoor(boolean flying_outdoor, boolean outdoor_hull) {
		System.out.println("CommandManager: setOutdoor(flyingOutdoor,usingOutdoorHull) = " + flying_outdoor + "," + outdoor_hull);
		q.add(new ConfigureCommand("control:outdoor", flying_outdoor));
		q.add(new ConfigureCommand("control:flight_without_shell", outdoor_hull));
	}

	public void setAutonomousFlight(boolean b) {
		q.add(new ConfigureCommand("control:autonomous_flight", b));
	}

	public void setManualTrim(boolean b) {
		q.add(new ConfigureCommand("control:manual_trim", b));
	}

	public void setPhoneTilt(float tilt) {
		q.add(new ConfigureCommand("control:control_iphone_tilt", String.valueOf(tilt)));
	}

	public void animate(FlightAnimation a) {
		q.add(new FlightAnimationCommand(a));
	}

	public void setPosition(double latitude, double longitude, double altitude) {
		q.add(new ConfigureCommand("gps:latitude", latitude));
		q.add(new ConfigureCommand("gps:longitude", longitude));
		q.add(new ConfigureCommand("gps:altitude", altitude));
	}

	public void setUltrasoundFrequency(UltrasoundFrequency f) {
		q.add(new ConfigureCommand("pic:ultrasound_freq", f.getValue()));
	}

	public void setSSIDSinglePlayer(String ssid) {
		q.add(new ConfigureCommand("network:ssid_single_player", ssid));
	}

	public void setSSIDMultiPlayer(String ssid) {
		q.add(new ConfigureCommand("network:ssid_multi_player", ssid));
	}

	public void setWifiMode(WifiMode mode) {
		q.add(new ConfigureCommand("network:wifi_mode", mode.ordinal()));
	}

	public void setOwnerMac(String mac) {
		q.add(new ConfigureCommand("network:owner_mac", mac));
	}

	public void startRecordingNavData(String dirname) {
		q.add(new ConfigureCommand("userbox:userbox_cmd", String.valueOf(UserBox.START.ordinal()) + "," + dirname));
	}

	public void cancelRecordingNavData() {
		q.add(new ConfigureCommand("userbox:userbox_cmd", UserBox.CANCEL.ordinal()));
	}

	public void stopRecording() {
		q.add(new ConfigureCommand("userbox:userbox_cmd", UserBox.STOP.ordinal()));
	}

	private static final SimpleDateFormat USERBOXFORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);

	public void startRecordingPictures(int delay, int nshots) {
		Date d = new Date();
		final String label = USERBOXFORMAT.format(d);
		q.add(new ConfigureCommand("userbox:userbox_cmd", String.valueOf(UserBox.SCREENSHOT.ordinal()) + ","
				+ String.valueOf(delay) + "," + String.valueOf(nshots) + "," + label));
	}

	public URL[] getRecordedNavDataURLs() throws IOException {
		URLRetriever r = new URLRetriever(inetaddr, "anonymous", "");
		return r.getURLs(new NavDataFileFilter());
	}

	private static class URLRetriever {
		private String user;
		private String pass;
		private InetAddress address;

		public URLRetriever(InetAddress address, String user, String pass) {
			this.address = address;
			this.user = user;
			this.pass = pass;
		}

		public String getUserBoxDir() {
			return "/boxes/";
		}

		public URL[] getURLs(FTPFileFilter filter) throws IOException {
			FTPClient ftp = login();

			ArrayList<URL> urls = new ArrayList<URL>();
			FTPFile[] dirs = ftp.listFiles("", new UserboxFileFilter());
			for (FTPFile dir : dirs) {
				FTPFile[] files = ftp.listFiles(getUserBoxDir() + dir.getName(), filter);
				for (FTPFile file : files) {
					try {
						URL url = new URL("ftp://" + user + ":" + pass + "@" + address.getHostName() + getUserBoxDir()
								+ dir.getName() + File.separator + file.getName());
						System.out.println("PICTURE: " + url);
						urls.add(url);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}

			logout(ftp);

			return urls.toArray(new URL[urls.size()]);
		}

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

		private FTPClient login() throws SocketException, IOException {
			FTPClient ftp = new FTPClient();
			ftp.connect(address);

			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				throw new IOException("FTP server refused connection.");
			}

			ftp.enterLocalPassiveMode();
			ftp.login(user, pass);

			return ftp;
		}

		private void logout(FTPClient ftp) throws IOException {
			ftp.logout();
		}

	}

//	public Bitmap[] getRecordedPictures() throws IOException {
//		URLRetriever r = new URLRetriever(inetaddr, "anonymous", "");
//		return r.getBitmaps();
//	}

	public URL[] getRecordedPictureURLs() throws IOException {
		URLRetriever r = new URLRetriever(inetaddr, "anonymous", "");
		return r.getURLs(new JPEGFileFilter());
	}

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
		System.out.println("Started " + getClass().getSimpleName());
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
			} catch (InterruptedException e) {
				doStop = true;
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		close();
		System.out.println("doStop() called ? " + doStop + " ... Stopped " + getClass().getSimpleName());
	}

	private void initARDrone() {
		// pmode parameter and first misc parameter are related
		sendPMode(2);
		sendMisc(2, 20, 2000, 3000);
		freeze();
		landing();
		
		setOutdoor(false, false);
		setMaxEulerAngle(0.25f);
	}

	private synchronized void sendCommand(ATCommand c) throws InterruptedException, IOException {
		if (!(c instanceof KeepAliveCommand)) {
			System.out.println("Send Command: " + c.getCommandString(seq));
		}
		byte[] buffer = c.getPacket(seq++);
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

	public void setControlAck(boolean b) {
		synchronized (controlAckLock) {
			controlAck = b;
			controlAckLock.notifyAll();
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
			}
		}
	}

}
