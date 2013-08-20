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
package de.yadrone.base.navdata;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.CRC32;


import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.DetectionType;
import de.yadrone.base.manager.AbstractManager;
import de.yadrone.base.utils.ARDroneUtils;

//TODO: refactor parsing code into separate classes but need to think about how to put the listener code
//option: make it one abstract listener, disadvantage: each client has many methods to implement
public class NavDataManager extends AbstractManager {

	private static final int NB_ACCS = 3;
	private static final int NB_GYROS = 3;

	/* number of trackers in width of current picture */
	private static final int NB_CORNER_TRACKERS_WIDTH = 5;
	/* number of trackers in height of current picture */
	private static final int NB_CORNER_TRACKERS_HEIGHT = 4;

	private static final int DEFAULT_NB_TRACKERS_WIDTH = NB_CORNER_TRACKERS_WIDTH + 1;
	private static final int DEFAULT_NB_TRACKERS_HEIGHT = NB_CORNER_TRACKERS_HEIGHT + 1;

	private static final int NB_NAVDATA_DETECTION_RESULTS = 4;

	// source: navdata_common.h
	private static final int NAVDATA_MAX_CUSTOM_TIME_SAVE = 20;
	private static final int MAX_PACKET_SIZE = 2048;

	private CommandManager manager = null;

	private AttitudeListener attitudeListener = null;
	private AltitudeListener altitudeListener = null;
	private StateListener stateListener = null;
	private VelocityListener velocityListener = null;
	private BatteryListener batteryListener = null;
	private TimeListener timeListener = null;
	private VisionListener visionListener = null;
	private MagnetoListener magnetoListener = null;
	private AcceleroListener acceleroListener = null;
	private GyroListener gyroListener = null;
	private UltrasoundListener ultrasoundListener = null;
	private WatchdogListener watchdogListener = null;
	private AdcListener adcListener = null;
	private CounterListener counterListener = null;
	private PressureListener pressureListener = null;
	private TemperatureListener temperatureListener = null;
	private WindListener windListener = null;
	private VideoListener videoListener = null;
	private WifiListener wifiListener = null;
	private Zimmu3000Listener zimmu3000Listener = null;
	private PWMlistener pwmlistener = null;
	private ReferencesListener referencesListener = null;
	private TrimsListener trimsListener = null;

//	private ArrayList<AttitudeListener> attitudeListener = new ArrayList<AttitudeListener>();
//	private ArrayList<AltitudeListener> altitudeListener = new ArrayList<AltitudeListener>();
//	private ArrayList<StateListener> stateListener = new ArrayList<StateListener>();
//	private ArrayList<VelocityListener> velocityListener = new ArrayList<VelocityListener>();
//	private ArrayList<BatteryListener> batteryListener = new ArrayList<BatteryListener>();
//	private ArrayList<TimeListener> timeListener = new ArrayList<TimeListener>();
//	private ArrayList<VisionListener> visionListener = new ArrayList<VisionListener>();
//	private ArrayList<MagnetoListener> magnetoListener = new ArrayList<MagnetoListener>();
//	private ArrayList<AcceleroListener> acceleroListener = new ArrayList<AcceleroListener>();
//	private ArrayList<GyroListener> gyroListener = new ArrayList<GyroListener>();
//	private ArrayList<UltrasoundListener> ultrasoundListener = new ArrayList<UltrasoundListener>();
//	private ArrayList<WatchdogListener> watchdogListener = new ArrayList<WatchdogListener>();
//	private ArrayList<AdcListener> adcListener = new ArrayList<AdcListener>();
//	private ArrayList<CounterListener> counterListener = new ArrayList<CounterListener>();
//	private ArrayList<PressureListener> pressureListener = new ArrayList<PressureListener>();
//	private ArrayList<TemperatureListener> temperatureListener = new ArrayList<TemperatureListener>();
//	private ArrayList<WindListener> windListener = new ArrayList<WindListener>();
//	private ArrayList<VideoListener> videoListener = new ArrayList<VideoListener>();
//	private ArrayList<WifiListener> wifiListener = new ArrayList<WifiListener>();
//	private ArrayList<Zimmu3000Listener> zimmu3000Listener = new ArrayList<Zimmu3000Listener>();
//	private ArrayList<PWMlistener> pwmlistener = new ArrayList<PWMlistener>();
//	private ArrayList<ReferencesListener> referencesListener = new ArrayList<ReferencesListener>();
//	private ArrayList<TrimsListener> trimsListener = new ArrayList<TrimsListener>();
	
	private long lastSequenceNumber = 1;

	private int mask = 0;
	private boolean maskChanged = true;
	private int checksum = 0;

	public NavDataManager(InetAddress inetaddr, CommandManager manager) {
		super(inetaddr);
		this.manager = manager;
	}

	private void setMask(boolean reset, int[] tags) {
		int newmask = 0;
		for (int n = 0; n < tags.length; n++) {
			newmask |= 1 << tags[n];
		}
		if (reset) {
			mask &= ~newmask;
		} else {
			mask |= newmask;
		}
		maskChanged = true;
	}

	public void setAttitudeListener(AttitudeListener attitudeListener) {
		this.attitudeListener = attitudeListener;
		setMask(attitudeListener == null, new int[] { DEMO_TAG, EULER_ANGLES_TAG, WIND_TAG });
	}

	public void setAltitudeListener(AltitudeListener altitudeListener) {
		this.altitudeListener = altitudeListener;
		setMask(altitudeListener == null, new int[] { DEMO_TAG, ALTITUDE_TAG });
	}

	public void setBatteryListener(BatteryListener batteryListener) {
		this.batteryListener = batteryListener;
		setMask(batteryListener == null, new int[] { DEMO_TAG, RAW_MEASURES_TAG });
	}

	public void setTimeListener(TimeListener timeListener) {
		this.timeListener = timeListener;
		setMask(timeListener == null, new int[] { TIME_TAG });
	}

	public void setStateListener(StateListener stateListener) {
		this.stateListener = stateListener;
		setMask(stateListener == null, new int[] { DEMO_TAG });
	}

	public void setVelocityListener(VelocityListener velocityListener) {
		this.velocityListener = velocityListener;
		setMask(velocityListener == null, new int[] { DEMO_TAG });
	}

	public void setVisionListener(VisionListener visionListener) {
		this.visionListener = visionListener;
		setMask(visionListener == null, new int[] { DEMO_TAG, TRACKERS_SEND_TAG, VISION_DETECT_TAG, VISION_OF_TAG,
				VISION_TAG, VISION_PERF_TAG, VISION_RAW_TAG });
	}

	public void setMagnetoListener(MagnetoListener magnetoListener) {
		this.magnetoListener = magnetoListener;
		setMask(magnetoListener == null, new int[] { MAGENTO_TAG });
	}

	public void setAcceleroListener(AcceleroListener acceleroListener) {
		this.acceleroListener = acceleroListener;
		setMask(acceleroListener == null, new int[] { PHYS_MEASURES_TAG, RAW_MEASURES_TAG });
	}

	public void setGyroListener(GyroListener gyroListener) {
		this.gyroListener = gyroListener;
		setMask(gyroListener == null, new int[] { GYROS_OFFSETS_TAG, PHYS_MEASURES_TAG, RAW_MEASURES_TAG });
	}

	public void setUltrasoundListener(UltrasoundListener ultrasoundListener) {
		this.ultrasoundListener = ultrasoundListener;
		setMask(ultrasoundListener == null, new int[] { RAW_MEASURES_TAG });
	}

	public void setAdcListener(AdcListener adcListener) {
		this.adcListener = adcListener;
		setMask(adcListener == null, new int[] { ADC_DATA_FRAME_TAG });
	}

	public void setCounterListener(CounterListener counterListener) {
		this.counterListener = counterListener;
		setMask(counterListener == null, new int[] { GAMES_TAG });
	}

	public void setPressureListener(PressureListener pressureListener) {
		this.pressureListener = pressureListener;
		setMask(pressureListener == null, new int[] { KALMAN_PRESSURE_TAG, PRESSURE_RAW_TAG });
	}

	public void setTemperatureListener(TemperatureListener temperatureListener) {
		this.temperatureListener = temperatureListener;
		setMask(temperatureListener == null, new int[] { PRESSURE_RAW_TAG });
	}

	public void setWindListener(WindListener windListener) {
		this.windListener = windListener;
		setMask(windListener == null, new int[] { WIND_TAG });
	}

	public void setVideoListener(VideoListener videoListener) {
		this.videoListener = videoListener;
		setMask(videoListener == null, new int[] { HDVIDEO_STREAM_TAG, VIDEO_STREAM_TAG });
	}

	public void setWifiListener(WifiListener wifiListener) {
		this.wifiListener = wifiListener;
		setMask(wifiListener == null, new int[] { WIFI_TAG });
	}

	public void setZimmu3000Listener(Zimmu3000Listener zimmu3000Listener) {
		this.zimmu3000Listener = zimmu3000Listener;
		setMask(zimmu3000Listener == null, new int[] { ZIMMU_3000_TAG });
	}

	public void setPWMlistener(PWMlistener pwmlistener) {
		this.pwmlistener = pwmlistener;
		setMask(pwmlistener == null, new int[] { PWM_TAG });
	}

	public void setReferencesListener(ReferencesListener referencesListener) {
		this.referencesListener = referencesListener;
		setMask(referencesListener == null, new int[] { RC_REFERENCES_TAG, REFERENCES_TAG });
	}

	public void setTrimsListener(TrimsListener trimsListener) {
		this.trimsListener = trimsListener;
		setMask(trimsListener == null, new int[] { TRIMS_TAG });
	}

	@Override
	public void run() {
		connect(ARDroneUtils.NAV_PORT);
		ticklePort(ARDroneUtils.NAV_PORT);
		boolean bootstrapping = true;
		boolean controlAck = false;

		DatagramPacket packet = new DatagramPacket(new byte[MAX_PACKET_SIZE], MAX_PACKET_SIZE);
		while (!doStop) {
			try {

				// ticklePort(ARDroneUtils.NAV_PORT);
				socket.receive(packet);
				ByteBuffer buffer = ByteBuffer.wrap(packet.getData(), 0, packet.getLength());

				DroneState s = parse(buffer);

				// according to 7.1.2. of the ARDrone Developer Guide demo
				// mode must be set after exiting bootstrap mode
				// TODO can we receive multiple bootstrap packets?
				if (bootstrapping) {
					controlAck = s.isControlReceived();
					manager.setControlAck(controlAck);
					if (s.isNavDataBootstrap()) {
						// presumably iso setting the demo option we can already ask for the options we want here
						manager.setNavDataDemo(true);
						System.out.println("Navdata Bootstrapped");
					} else {
						System.out.println("Navdata was already bootstrapped");
					}
					bootstrapping = false;
				}

				// detect control Ack change
				boolean newcontrolAck = s.isControlReceived();
				if (newcontrolAck != controlAck) {
					manager.setControlAck(newcontrolAck);
					controlAck = newcontrolAck;
				}

				// TODO should we reset the communication watchdog always?
				if (s.isCommunicationProblemOccurred()) {
					manager.resetCommunicationWatchDog();
				}

				// TODO bootstrapping probably be handled by commandmanager
				if (!bootstrapping && maskChanged) {
					manager.setNavDataDemo(false);
					manager.setNavDataOptions(mask);
					maskChanged = false;
				}
			} catch (SocketTimeoutException t) {
				System.err.println("Navdata reception timeout");
			} catch (Throwable t) {
				// continue whatever goes wrong
				t.printStackTrace();
			}
		}
		close();
		System.out.println("Stopped " + getClass().getSimpleName());
	}

	public DroneState parse(ByteBuffer b) throws NavDataException {

		b.order(ByteOrder.LITTLE_ENDIAN);
		int magic = b.getInt();
//		checkEqual(0x55667788, magic, "Magic must be correct"); // throws exception, do not know why

		int state = b.getInt();
		long sequence = getUInt32(b);
		int vision = b.getInt();

		// if (sequence <= lastSequenceNumber && sequence != 1) {
		// // TODO sometimes we seem to receive a previous packet, find out why
		// throw new NavDataException("Invalid sequence number received (received=" + sequence + " last="
		// + lastSequenceNumber);
		// }
		lastSequenceNumber = sequence;

		DroneState s = new DroneState(state, vision);
		if (stateListener != null) {
			stateListener.stateChanged(s);
		}

		// parse options
		while (b.position() < b.limit()) {
			int tag = b.getShort() & 0xFFFF;
			int payloadSize = (b.getShort() & 0xFFFF) - 4;
			ByteBuffer optionData = b.slice().order(ByteOrder.LITTLE_ENDIAN);
			optionData.limit(payloadSize);
			parseOption(tag, optionData);
			b.position(b.position() + payloadSize);
		}

		// verify checksum; a bit of a hack: assume checksum = 0 is very unlikely
		if (checksum != 0) {
			checkEqual(getCRC(b, 0, b.limit() - 4), checksum, "Checksum does not match");
			checksum = 0;
		}
		return s;
	}

	private static final int CKS_TAG = -1;
	private static final int DEMO_TAG = 0;
	private static final int TIME_TAG = 1;
	private static final int RAW_MEASURES_TAG = 2;
	private static final int PHYS_MEASURES_TAG = 3;
	private static final int GYROS_OFFSETS_TAG = 4;
	private static final int EULER_ANGLES_TAG = 5;
	private static final int REFERENCES_TAG = 6;
	private static final int TRIMS_TAG = 7;
	private static final int RC_REFERENCES_TAG = 8;
	private static final int PWM_TAG = 9;
	private static final int ALTITUDE_TAG = 10;
	private static final int VISION_RAW_TAG = 11;
	private static final int VISION_OF_TAG = 12;
	private static final int VISION_TAG = 13;
	private static final int VISION_PERF_TAG = 14;
	private static final int TRACKERS_SEND_TAG = 15;
	private static final int VISION_DETECT_TAG = 16;
	private static final int WATCHDOG_TAG = 17;
	private static final int ADC_DATA_FRAME_TAG = 18;
	private static final int VIDEO_STREAM_TAG = 19;
	private static final int GAMES_TAG = 20;
	private static final int PRESSURE_RAW_TAG = 21;
	private static final int MAGENTO_TAG = 22;
	private static final int WIND_TAG = 23;
	private static final int KALMAN_PRESSURE_TAG = 24;
	private static final int HDVIDEO_STREAM_TAG = 25;
	private static final int WIFI_TAG = 26;
	private static final int ZIMMU_3000_TAG = 27;

	private void parseOption(int tag, ByteBuffer optionData) {
		switch (tag) {
		case CKS_TAG:
			parseCksOption(optionData);
			break;
		case DEMO_TAG:
			parseDemoOption(optionData);
			break;
		case TIME_TAG:
			parseTimeOption(optionData);
			break;
		case RAW_MEASURES_TAG:
			parseRawMeasuresOption(optionData);
			break;
		case PHYS_MEASURES_TAG:
			parsePhysMeasuresOption(optionData);
			break;
		case GYROS_OFFSETS_TAG:
			parseGyrosOffsetsOption(optionData);
			break;
		case EULER_ANGLES_TAG:
			parseEulerAnglesOption(optionData);
			break;
		case REFERENCES_TAG:
			parseReferencesOption(optionData);
			break;
		case TRIMS_TAG:
			parseTrimsOption(optionData);
			break;
		case RC_REFERENCES_TAG:
			parseRcReferencesOption(optionData);
			break;
		case PWM_TAG:
			parsePWMOption(optionData);
			break;
		case ALTITUDE_TAG:
			parseAltitudeOption(optionData);
			break;
		case VISION_RAW_TAG:
			parseVisionRawOption(optionData);
			break;
		case VISION_OF_TAG:
			parseVisionOfOption(optionData);
			break;
		case VISION_TAG:
			parseVisionOption(optionData);
			break;
		case VISION_PERF_TAG:
			parseVisionPerfOption(optionData);
			break;
		case TRACKERS_SEND_TAG:
			parseTrackersSendOption(optionData);
			break;
		case VISION_DETECT_TAG:
			parseVisionDetectOption(optionData);
			break;
		case WATCHDOG_TAG:
			parseWatchdogOption(optionData);
			break;
		case ADC_DATA_FRAME_TAG:
			parseAdcDataFrameOption(optionData);
			break;
		case VIDEO_STREAM_TAG:
			parseVideoStreamOption(optionData);
			break;
		case GAMES_TAG:
			parseGamesOption(optionData);
			break;
		case PRESSURE_RAW_TAG:
			parsePressureOption(optionData);
			break;
		case MAGENTO_TAG:
			parseMagnetoDataOption(optionData);
			break;
		case WIND_TAG:
			parseWindOption(optionData);
			break;
		case KALMAN_PRESSURE_TAG:
			parseKalmanPressureOption(optionData);
			break;
		case HDVIDEO_STREAM_TAG:
			parseHDVideoSteamOption(optionData);
			break;
		case WIFI_TAG:
			parseWifiOption(optionData);
			break;
		case ZIMMU_3000_TAG:
			parseZimmu3000Option(optionData);
			break;
		}

	}

	private void parseCksOption(ByteBuffer b) {
		checksum = b.getInt();
	}

	private void parseZimmu3000Option(ByteBuffer b) {
		if (zimmu3000Listener != null) {

			int vzimmuLSB = b.getInt();
			float vzfind = b.getFloat();

			zimmu3000Listener.received(vzimmuLSB, vzfind);
		}

	}

	private void parseWifiOption(ByteBuffer b) {
		// TODO: verify if link quality stays below Integer.MAX_INT
		if (wifiListener != null) {
			long link_quality = getUInt32(b);

			wifiListener.received(link_quality);
		}

	}

	private void parseHDVideoSteamOption(ByteBuffer b) {
		if (videoListener != null) {
			// assumption: does not exceed Integer.MAX_INT
			HDVideoState hdvideo_state = HDVideoState.fromInt(b.getInt());

			// assumption: does not exceed Integer.MAX_INT
			int storage_fifo_nb_packets = b.getInt();

			// assumption: does not exceed Integer.MAX_INT
			int storage_fifo_size = b.getInt();

			// assumption: USB key size below Integer.MAX_INT kbytes
			// USB key in kbytes - 0 if no key present
			int usbkey_size = b.getInt();

			// assumption: USB key size below Integer.MAX_INT kbytes
			// USB key free space in kbytes - 0 if no key present
			int usbkey_freespace = b.getInt();

			// 'frame_number' PaVE field of the frame starting to be encoded for
			// the
			// HD stream
			int frame_number = b.getInt();

			// remaining time in seconds
			int usbkey_remaining_time = b.getInt();

			HDVideoStreamData d = new HDVideoStreamData(hdvideo_state, storage_fifo_nb_packets, storage_fifo_size,
					usbkey_size, usbkey_freespace, frame_number, usbkey_remaining_time);
			videoListener.receivedHDVideoStreamData(d);
		}
	}

	private void parseKalmanPressureOption(ByteBuffer b) {
		if (pressureListener != null) {

			float offset_pressure = b.getFloat();
			float est_z = b.getFloat();
			float est_zdot = b.getFloat();
			float est_bias_PWM = b.getFloat();
			float est_biais_pression = b.getFloat();
			float offset_US = b.getFloat();
			float prediction_US = b.getFloat();
			float cov_alt = b.getFloat();
			float cov_PWM = b.getFloat();
			float cov_vitesse = b.getFloat();
			boolean effet_sol = getBoolean(b);
			float somme_inno = b.getFloat();
			boolean rejet_US = getBoolean(b);
			float u_multisinus = b.getFloat();
			float gaz_altitude = b.getFloat();
			boolean multisinus = getBoolean(b);
			boolean multisinus_debut = getBoolean(b);

			KalmanPressureData d = new KalmanPressureData(offset_pressure, est_z, est_zdot, est_bias_PWM,
					est_biais_pression, offset_US, prediction_US, cov_alt, cov_PWM, cov_vitesse, effet_sol, somme_inno,
					rejet_US, u_multisinus, gaz_altitude, multisinus, multisinus_debut);
			pressureListener.receivedKalmanPressure(d);
		}
	}

	private void parseWindOption(ByteBuffer b) {
		if (attitudeListener != null || windListener != null) {

			// estimated wind speed [m/s]
			float wind_speed = b.getFloat();

			// estimated wind direction in North-East frame [rad] e.g. if
			// wind_angle
			// is pi/4, wind is from South-West to North-East
			float wind_angle = b.getFloat();

			float wind_compensation_theta = b.getFloat();
			float wind_compensation_phi = b.getFloat();

			float[] state = getFloat(b, 6);

			float[] magneto = getFloat(b, 3);

			if (attitudeListener != null) {
				attitudeListener.windCompensation(wind_compensation_theta, wind_compensation_phi);
			}

			if (windListener != null) {
				WindEstimationData d = new WindEstimationData(wind_speed, wind_angle, state, magneto);
				windListener.receivedEstimation(d);
			}
		}

	}

	private void parsePressureOption(ByteBuffer b) {
		if (pressureListener != null || temperatureListener != null) {

			int up = b.getInt();
			short ut = b.getShort();
			int temperature_meas = b.getInt();
			int pression_meas = b.getInt();

			if (pressureListener != null) {
				Pressure d = new Pressure(up, pression_meas);
				pressureListener.receivedPressure(d);
			}

			if (temperatureListener != null) {
				Temperature d = new Temperature(ut, temperature_meas);
				temperatureListener.receivedTemperature(d);
			}
		}
	}

	private void parseGamesOption(ByteBuffer b) {
		if (counterListener != null) {

			long double_tap_counter = getUInt32(b);
			long finish_line_counter = getUInt32(b);

			Counters d = new Counters(double_tap_counter, finish_line_counter);
			counterListener.update(d);
		}

	}

	private void parseVideoStreamOption(ByteBuffer b) {
		if (videoListener != null) {

			// quantizer reference used to encode frame [1:31]
			// assumption: sign is irrelevant
			byte quant = b.get();

			// frame size (bytes)
			// assumption: does not exceed Integer.MAX_INT
			int frame_size = b.getInt();

			// frame index
			// assumption: does not exceed Integer.MAX_INT
			int frame_number = b.getInt();

			// atmcd ref sequence number
			// assumption: does not exceed Integer.MAX_INT
			int atcmd_ref_seq = b.getInt();

			// mean time between two consecutive atcmd_ref (ms)
			// assumption: does not exceed Integer.MAX_INT
			int atcmd_mean_ref_gap = b.getInt();

			float atcmd_var_ref_gap = b.getInt();

			// estimator of atcmd link quality
			// assumption: does not exceed Integer.MAX_INT
			int atcmd_ref_quality = b.getInt();

			// drone2

			// measured out throughput from the video tcp socket
			// assumption: does not exceed Integer.MAX_INT
			int out_bitrate = b.getInt();

			// last frame size generated by the video encoder
			// assumption: does not exceed Integer.MAX_INT
			int desired_bitrate = b.getInt();

			// misc temporary data
			int[] temp_data = getInt(b, 5);

			// queue usage
			// assumption: does not exceed Integer.MAX_INT
			int tcp_queue_level = b.getInt();
			// assumption: does not exceed Integer.MAX_INT
			int fifo_queue_level = b.getInt();

			VideoStreamData d = new VideoStreamData(quant, frame_size, frame_number, atcmd_ref_seq, atcmd_mean_ref_gap,
					atcmd_var_ref_gap, atcmd_ref_quality, out_bitrate, desired_bitrate, temp_data, tcp_queue_level,
					fifo_queue_level);
			videoListener.receivedVideoStreamData(d);
		}
	}

	private void parseAdcDataFrameOption(ByteBuffer b) {
		if (adcListener != null) {
			// assumption: does not exceed Integer.MAX_INT or sign is irrelevant
			int version = b.getInt();

			// assumption: sign is irrelevant
			byte[] data_frame = new byte[32];
			b.get(data_frame);

			AdcFrame d = new AdcFrame(version, data_frame);
			adcListener.receivedFrame(d);
		}
	}

	private void parseWatchdogOption(ByteBuffer b) {
		if (watchdogListener != null) {
			int watchdog = b.getInt();

			watchdogListener.received(watchdog);
		}
	}

	private void parseTrackersSendOption(ByteBuffer b) {
		if (visionListener != null) {

			// trackers[i][j][0]: locked
			// trackers[i][j][1]: point.x
			// trackers[i][j][2]: point.y

			int[][][] trackers = new int[DEFAULT_NB_TRACKERS_WIDTH][DEFAULT_NB_TRACKERS_HEIGHT][3];
			for (int i = 0; i < DEFAULT_NB_TRACKERS_WIDTH; i++) {
				for (int j = 0; j < DEFAULT_NB_TRACKERS_HEIGHT; j++) {
					trackers[i][j][0] = b.getInt();
				}
			}

			for (int i = 0; i < DEFAULT_NB_TRACKERS_WIDTH; i++) {
				for (int j = 0; j < DEFAULT_NB_TRACKERS_HEIGHT; j++) {
					trackers[i][j][1] = b.getInt();
					trackers[i][j][2] = b.getInt();
				}
			}

			// TODO: create Tracker class containing locked + point?
			visionListener.trackersSend(new TrackerData(trackers));
		}
	}

	private void parseVisionPerfOption(ByteBuffer b) {
		if (visionListener != null) {
			float time_szo = b.getFloat();
			float time_corners = b.getFloat();
			float time_compute = b.getFloat();
			float time_tracking = b.getFloat();
			float time_trans = b.getFloat();
			float time_update = b.getFloat();
			float[] time_custom = getFloat(b, NAVDATA_MAX_CUSTOM_TIME_SAVE);

			VisionPerformance d = new VisionPerformance(time_szo, time_corners, time_compute, time_tracking,
					time_trans, time_update, time_custom);
			visionListener.receivedPerformanceData(d);
		}
	}

	private void parseVisionOption(ByteBuffer b) {
		if (visionListener != null) {
			int vision_state = b.getInt();
			int vision_misc = b.getInt();
			float vision_phi_trim = b.getFloat();
			float vision_phi_ref_prop = b.getFloat();
			float vision_theta_trim = b.getFloat();
			float vision_theta_ref_prop = b.getFloat();
			int new_raw_picture = b.getInt();
			float theta_capture = b.getFloat();
			float phi_capture = b.getFloat();
			float psi_capture = b.getFloat();
			int altitude_capture = b.getInt();
			// time in TSECDEC format (see config.h)
			int time_capture = b.getInt();
			int time_capture_seconds = getSeconds(time_capture);
			int time_capture_useconds = getUSeconds(time_capture);
			float[] body_v = getFloat(b, 3);
			float delta_phi = b.getFloat();
			float delta_theta = b.getFloat();
			float delta_psi = b.getFloat();
			int gold_defined = b.getInt();
			int gold_reset = b.getInt();
			float gold_x = b.getFloat();
			float gold_y = b.getFloat();

			VisionData d = new VisionData(vision_state, vision_misc, vision_phi_trim, vision_phi_ref_prop,
					vision_theta_trim, vision_theta_ref_prop, new_raw_picture, theta_capture, phi_capture, psi_capture,
					altitude_capture, time_capture_seconds, time_capture_useconds, body_v, delta_phi, delta_theta,
					delta_psi, gold_defined, gold_reset, gold_x, gold_y);
			visionListener.receivedData(d);
		}

	}

	private void parseVisionOfOption(ByteBuffer b) {
		if (visionListener != null) {
			float[] of_dx = getFloat(b, 5);
			float[] of_dy = getFloat(b, 5);

			visionListener.receivedVisionOf(of_dx, of_dy);
		}

	}

	private void parseVisionRawOption(ByteBuffer b) {
		if (visionListener != null) {
			float[] vision_raw = getFloat(b, 3);

			visionListener.receivedRawData(vision_raw);
		}
	}

	private void parseAltitudeOption(ByteBuffer b) {
		if (altitudeListener != null) {
			int altitude_vision = b.getInt();
			float altitude_vz = b.getFloat();
			int altitude_ref = b.getInt();
			int altitude_raw = b.getInt();

			// TODO: what does obs mean?
			float obs_accZ = b.getFloat();
			float obs_alt = b.getFloat();

			float[] obs_x = getFloat(b, 3);

			int obs_state = b.getInt();

			// TODO: what does vb mean?
			float[] est_vb = getFloat(b, 2);

			int est_state = b.getInt();

			Altitude d = new Altitude(altitude_vision, altitude_vz, altitude_ref, altitude_raw, obs_accZ, obs_alt,
					obs_x, obs_state, est_vb, est_state);
			altitudeListener.receivedExtendedAltitude(d);
		}
	}

	private void parsePWMOption(ByteBuffer b) {
		if (pwmlistener != null) {
			short[] motor = getUInt8(b, 4);
			short[] sat_motor = getUInt8(b, 4);
			float gaz_feed_forward = b.getFloat();
			float gaz_altitude = b.getFloat();
			float altitude_integral = b.getFloat();
			float vz_ref = b.getFloat();
			// pry = pitch roll yaw; bit lazy yes :-)
			int[] u_pry = getInt(b, 3);
			float yaw_u_I = b.getFloat();
			// pry = pitch roll yaw; bit lazy yes :-)
			int[] u_planif_pry = getInt(b, 3);
			float u_gaz_planif = b.getFloat();
			int current_motor[] = getUInt16(b, 4);
			// WARNING: new navdata (FC 26/07/2011)
			float altitude_prop = b.getFloat();
			float altitude_der = b.getFloat();

			PWMData d = new PWMData(motor, sat_motor, gaz_feed_forward, gaz_altitude, altitude_integral, vz_ref, u_pry,
					yaw_u_I, u_planif_pry, u_gaz_planif, current_motor, altitude_prop, altitude_der);
			pwmlistener.received(d);
		}
	}

	private void parseRcReferencesOption(ByteBuffer b) {
		if (referencesListener != null) {
			int[] rc_ref = getInt(b, 5);
			referencesListener.receivedRcReferences(rc_ref);
		}
	}

	private void parseTrimsOption(ByteBuffer b) {
		if (trimsListener != null) {
			float angular_rates_trim_r = b.getFloat();
			float euler_angles_trim_theta = b.getFloat();
			float euler_angles_trim_phi = b.getFloat();

			trimsListener.receivedTrimData(angular_rates_trim_r, euler_angles_trim_theta, euler_angles_trim_phi);
		}
	}

	private void parseReferencesOption(ByteBuffer b) {
		if (referencesListener != null) {
			int ref_theta = b.getInt();
			int ref_phi = b.getInt();
			int ref_theta_I = b.getInt();
			int ref_phi_I = b.getInt();
			int ref_pitch = b.getInt();
			int ref_roll = b.getInt();
			int ref_yaw = b.getInt();
			int ref_psi = b.getInt();

			float[] v_ref = getFloat(b, 2);
			float theta_mod = b.getFloat();
			float phi_mod = b.getFloat();
			float[] k_v = getFloat(b, 2);
			// assumption: k_mode does not exceed Integer.MAX_INT
			int k_mode = b.getInt();

			float ui_time = b.getFloat();
			float ui_theta = b.getFloat();
			float ui_phi = b.getFloat();
			float ui_psi = b.getFloat();
			float ui_psi_accuracy = b.getFloat();
			int ui_seq = b.getInt();

			ReferencesData d = new ReferencesData(ref_theta, ref_phi, ref_theta_I, ref_phi_I, ref_pitch, ref_roll,
					ref_yaw, ref_psi, v_ref, theta_mod, phi_mod, k_v, k_mode, ui_time, ui_theta, ui_phi, ui_psi,
					ui_psi_accuracy, ui_seq);
			referencesListener.receivedReferences(d);
		}

	}

	private void parseRawMeasuresOption(ByteBuffer b) {
		if (batteryListener != null || acceleroListener != null || gyroListener != null || ultrasoundListener != null) {
			// see http://blog.perquin.com/blog/ar-drone-navboard/
			// speculative: Raw data (10-bit) of the accelerometers multiplied by 4
			int[] raw_accs = getUInt16(b, NB_ACCS);

			// see http://blog.perquin.com/blog/ar-drone-navboard/
			// speculative: Raw data for the gyros, 12-bit A/D converted voltage of the gyros. X,Y=IDG, Z=Epson
			short[] raw_gyros = getShort(b, NB_GYROS);

			// see http://blog.perquin.com/blog/ar-drone-navboard/
			// speculative: 4.5x Raw data (IDG), gyro values (x/y) with another resolution (see IDG-500 datasheet)
			short[] raw_gyros_110 = getShort(b, 2);

			// Assumption: value well below Integer.MAX_VALUE
			// battery voltage raw (mV)
			int vbat_raw = b.getInt();

			// see http://blog.perquin.com/blog/ar-drone-navboard/
			// probably: Array with starts of echos (8 array values @ 25Hz, 9 values @ 22.22Hz)
			int us_echo_start = getUInt16(b);

			// see http://blog.perquin.com/blog/ar-drone-navboard/
			// probably: array with ends of echos (8 array values @ 25Hz, 9 values @ 22.22Hz)
			int us_echo_end = getUInt16(b);

			// see http://blog.perquin.com/blog/ar-drone-navboard/
			// Ultrasonic parameter. speculative: echo number starting with 0. max value 3758. examples: 0,1,2,3,4,5,6,7
			// ; 0,1,2,3,4,86,6,9
			int us_association_echo = getUInt16(b);

			// see http://blog.perquin.com/blog/ar-drone-navboard/
			// Ultrasonic parameter. speculative: No clear pattern
			int us_distance_echo = getUInt16(b);

			// see http://blog.perquin.com/blog/ar-drone-navboard/
			// Ultrasonic parameter. Counts up from 0 to approx 24346 in 192 sample cycles of which 12 cylces have value
			// 0
			int us_cycle_time = getUInt16(b);

			// see http://blog.perquin.com/blog/ar-drone-navboard/
			// Ultrasonic parameter. Value between 0 and 4000, no clear pattern. 192 sample cycles of which 12 cycles
			// have value 0
			int us_cycle_value = getUInt16(b);

			// see http://blog.perquin.com/blog/ar-drone-navboard/
			// Ultrasonic parameter. Counts down from 4000 to 0 in 192 sample cycles of which 12 cycles have value 0
			int us_cycle_ref = getUInt16(b);
			int flag_echo_ini = getUInt16(b);
			int nb_echo = getUInt16(b);
			long sum_echo = getUInt32(b);
			int alt_temp_raw = b.getInt();
			short gradient = b.getShort();

			if (batteryListener != null) {
				batteryListener.voltageChanged(vbat_raw);
			}

			if (acceleroListener != null) {
				AcceleroRawData d = new AcceleroRawData(raw_accs);
				acceleroListener.receivedRawData(d);
			}

			if (gyroListener != null) {
				GyroRawData d = new GyroRawData(raw_gyros, raw_gyros_110);
				gyroListener.receivedRawData(d);
			}

			if (ultrasoundListener != null) {
				UltrasoundData d = new UltrasoundData(us_echo_start, us_echo_end, us_association_echo,
						us_distance_echo, us_cycle_time, us_cycle_value, us_cycle_ref, flag_echo_ini, nb_echo,
						sum_echo, alt_temp_raw, gradient);
				ultrasoundListener.receivedRawData(d);
			}
		}
	}

	private void parsePhysMeasuresOption(ByteBuffer b) {
		if (acceleroListener != null || gyroListener != null) {
			float accs_temp = b.getFloat();
			int gyro_temp = getUInt16(b);

			float[] phys_accs = getFloat(b, NB_ACCS);

			float[] phys_gyros = getFloat(b, NB_GYROS);

			// 3.3volt alim [LSB]
			// TODO: check if LSB indeed means 1 byte
			// assumption alim relates to both sensors
			int alim3V3 = b.getInt() & 0xFF;

			// ref volt Epson gyro [LSB]
			// TODO: check if LSB indeed means 1 byte
			int vrefEpson = b.getInt() & 0xFF;

			// ref volt IDG gyro [LSB]
			// TODO: check if LSB indeed means 1 byte
			int vrefIDG = b.getInt() & 0xFF;

			if (acceleroListener != null) {
				AcceleroPhysData d = new AcceleroPhysData(accs_temp, phys_accs, alim3V3);
				acceleroListener.receivedPhysData(d);
			}

			if (gyroListener != null) {
				GyroPhysData d = new GyroPhysData(gyro_temp, phys_gyros, alim3V3, vrefEpson, vrefIDG);
				gyroListener.receivedPhysData(d);
			}
		}

	}

	private void parseGyrosOffsetsOption(ByteBuffer b) {
		if (gyroListener != null) {
			float[] offset_g = getFloat(b, NB_GYROS);

			gyroListener.receivedOffsets(offset_g);
		}
	}

	private void parseEulerAnglesOption(ByteBuffer b) {
		if (attitudeListener != null) {
			float theta_a = b.getFloat();
			float phi_a = b.getFloat();

			attitudeListener.attitudeUpdated(theta_a, phi_a);
		}

	}

	private void parseDemoOption(ByteBuffer b) {
		if (stateListener != null || batteryListener != null || attitudeListener != null || altitudeListener != null
				|| velocityListener != null || visionListener != null) {
			int controlState = b.getInt();

			// batteryPercentage is <=100 so sign is not an issue
			int batteryPercentage = b.getInt();

			float theta = b.getFloat();
			float phi = b.getFloat();
			float psi = b.getFloat();

			int altitude = b.getInt();

			float v[] = getFloat(b, 3);

			@SuppressWarnings("unused")
			long num_frames = getUInt32(b);
			/* Deprecated ! Don't use ! */
			@SuppressWarnings("unused")
			float detection_camera_rot[] = getFloat(b, 9);
			/* Deprecated ! Don't use ! */
			@SuppressWarnings("unused")
			float detection_camera_trans[] = getFloat(b, 3);
			/* Deprecated ! Don't use ! */
			@SuppressWarnings("unused")
			long detection_tag_index = getUInt32(b);

			int detection_camera_type = b.getInt();

			/* Deprecated ! Don't use ! */
			@SuppressWarnings("unused")
			float drone_camera_rot[] = getFloat(b, 9);
			/* Deprecated ! Don't use ! */
			@SuppressWarnings("unused")
			float drone_camera_trans[] = getFloat(b, 3);

			if (visionListener != null && detection_camera_type != 0) {
				visionListener.typeDetected(detection_camera_type);
			}

			if (stateListener != null) {
				stateListener.controlStateChanged(ControlState.fromInt(controlState >> 16));
			}

			if (batteryListener != null) {
				batteryListener.batteryLevelChanged(batteryPercentage);
			}

			if (attitudeListener != null) {
				attitudeListener.attitudeUpdated(theta, phi, psi);
			}

			if (altitudeListener != null) {
				altitudeListener.receivedAltitude(altitude);
			}

			if (velocityListener != null) {
				velocityListener.velocityChanged(v[0], v[1], v[2]);
			}
		}
	}

	private void parseTimeOption(ByteBuffer b) {
		if (timeListener != null) {
			int time = b.getInt();

			int useconds = getUSeconds(time);
			int seconds = getSeconds(time);

			timeListener.timeReceived(seconds, useconds);
		}
	}

	/**
	 * @param time
	 * @return
	 */
	private int getSeconds(int time) {
		int seconds = (time >>> 11);
		return seconds;
	}

	/**
	 * @param time
	 * @return
	 */
	private int getUSeconds(int time) {
		int useconds = (time & (0xFFFFFFFF >>> 11));
		return useconds;
	}

	private void parseVisionDetectOption(ByteBuffer b) {
		if (visionListener != null) {
			int ndetected = b.getInt();

			if (ndetected > 0) {

				int type[] = getInt(b, NB_NAVDATA_DETECTION_RESULTS);

				// assumption: values are well below Integer.MAX_VALUE, so sign is no issue
				int xc[] = getInt(b, NB_NAVDATA_DETECTION_RESULTS);

				// assumption: values are well below Integer.MAX_VALUE, so sign is no issue
				int yc[] = getInt(b, NB_NAVDATA_DETECTION_RESULTS);

				// assumption: values are well below Integer.MAX_VALUE, so sign is no issue
				int width[] = getInt(b, NB_NAVDATA_DETECTION_RESULTS);

				// assumption: values are well below Integer.MAX_VALUE, so sign is no issue
				int height[] = getInt(b, NB_NAVDATA_DETECTION_RESULTS);

				// assumption: values are well below Integer.MAX_VALUE, so sign is no issue
				int dist[] = getInt(b, NB_NAVDATA_DETECTION_RESULTS);

				float[] orientation_angle = getFloat(b, NB_NAVDATA_DETECTION_RESULTS);

				// could extend Bytebuffer to read matrix types
				float[][][] rotation = new float[NB_NAVDATA_DETECTION_RESULTS][3][3];
				for (int i = 0; i < NB_NAVDATA_DETECTION_RESULTS; i++) {
					for (int r = 0; r < 3; r++) {
						for (int c = 0; c < 3; c++) {
							rotation[i][r][c] = b.getFloat();
						}
					}
				}

				// could extend Bytebuffer to read vector types
				float[][] translation = new float[NB_NAVDATA_DETECTION_RESULTS][3];
				for (int i = 0; i < NB_NAVDATA_DETECTION_RESULTS; i++) {
					for (int r = 0; r < 3; r++) {
						translation[i][r] = b.getFloat();
					}
				}

				// assumption: values are well below Integer.MAX_VALUE, so sign is
				// no
				// issue
				int camera_source[] = getInt(b, NB_NAVDATA_DETECTION_RESULTS);

				VisionTag[] tags = new VisionTag[ndetected];
				for (int i = 0; i < ndetected; i++) {
					// TODO: does this also contain a mask if not multiple detect?
					VisionTag tag = new VisionTag(type[i], xc[i], yc[i], width[i], height[i], dist[i],
							orientation_angle[i], rotation[i], translation[i], DetectionType.fromInt(camera_source[i]));
					tags[i] = tag;
				}

				visionListener.tagsDetected(tags);
			}
		}
	}

	private void parseMagnetoDataOption(ByteBuffer b) {
		if (magnetoListener != null) {
			short m[] = getShort(b, 3);

			float[] mraw = getFloat(b, 3);

			float mrectified[] = getFloat(b, 3);

			float m_[] = getFloat(b, 3);

			float heading_unwrapped = b.getFloat();
			float heading_gyro_unwrapped = b.getFloat();
			float heading_fusion_unwrapped = b.getFloat();
			byte calibration_ok = b.get();
			int state = b.getInt(); // TODO: encoding?
			float radius = b.getFloat();
			float error_mean = b.getFloat();
			float error_var = b.getFloat();

			MagnetoData md = new MagnetoData(m, mraw, mrectified, m_, heading_unwrapped, heading_gyro_unwrapped,
					heading_fusion_unwrapped, calibration_ok, state, radius, error_mean, error_var);
			magnetoListener.received(md);
		}
	}

	/**
	 * @param b
	 * @param n
	 * @return
	 */
	private float[] getFloat(ByteBuffer b, int n) {
		float f[] = new float[n];
		for (int k = 0; k < f.length; k++) {
			f[k] = b.getFloat();
		}
		return f;
	}

	/**
	 * @param b
	 * @param n
	 * @return
	 */
	private int[] getInt(ByteBuffer b, int n) {
		int i[] = new int[n];
		for (int k = 0; k < i.length; k++) {
			i[k] = b.getInt();
		}
		return i;
	}

	/**
	 * @param b
	 * @param n
	 * @return
	 */
	private short[] getShort(ByteBuffer b, int n) {
		short s[] = new short[n];
		for (int k = 0; k < s.length; k++) {
			s[k] = b.getShort();
		}
		return s;
	}

	private int[] getUInt16(ByteBuffer b, int n) {
		int i[] = new int[n];
		for (int k = 0; k < i.length; k++) {
			i[k] = getUInt16(b);
		}
		return i;
	}

	private short[] getUInt8(ByteBuffer b, int n) {
		short s[] = new short[n];
		for (int k = 0; k < s.length; k++) {
			s[k] = getUInt8(b);
		}
		return s;
	}

	private boolean getBoolean(ByteBuffer b) {
		return (b.getInt() == 1);
	}

	/*
	 * Since Java does not have unsigned bytes, all uint8 are converted to signed shorts
	 */
	private short getUInt8(ByteBuffer b) {
		return (short) (b.get() & 0xFF);
	}

	/*
	 * Since Java does not have unsigned shorts, all uint16 are converted to signed integers
	 */
	private int getUInt16(ByteBuffer b) {
		return (b.getShort() & 0xFFFF);
	}

	/*
	 * Since Java does not have unsigned ints, all uint32 are converted to signed longs
	 */
	private long getUInt32(ByteBuffer b) {
		return (b.getInt() & 0xFFFFFFFFL);
	}

	private void checkEqual(int expected, int actual, String message) throws NavDataException {
		if (expected != actual) {
			throw new NavDataException(message + " : expected " + expected + ", was " + actual);
		}
	}

	private int getCRC(byte[] b, int offset, int length) {
		CRC32 cks = new CRC32();
		cks.update(b, offset, length);
		return (int) (cks.getValue() & 0xFFFFFFFFL);
	}

	private int getCRC(ByteBuffer b, int offset, int length) {
		return getCRC(b.array(), b.arrayOffset() + offset, length);
	}

}
