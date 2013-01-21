package com.shigeodayo.ardrone.navdata.javadrone;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.shigeodayo.ardrone.navdata.javadrone.VisionTag.VisionTagType;

public class NavData
{
	public static enum ControlAlgorithm {
		EULER_ANGELS_CONTROL, ANGULAR_SPEED_CONTROL
	}

	public static enum CtrlState {
		DEFAULT, INIT, LANDED, FLYING, HOVERING, TEST, TRANS_TAKEOFF, TRANS_GOTOFIX, TRANS_LANDING;

		public static CtrlState fromInt(int v) throws NavDataFormatException
		{
			switch (v)
			{
				case 0:
					return DEFAULT;
				case 1:
					return INIT;
				case 2:
					return LANDED;
				case 3:
					return FLYING;
				case 4:
					return HOVERING;
				case 5:
					return TEST;
				case 6:
					return TRANS_TAKEOFF;
				case 7:
					return TRANS_GOTOFIX;
				case 8:
					return TRANS_LANDING;
				default:
					throw new NavDataFormatException("Invalid control state " + v);
			}
		}
	}

	public static enum FlyingState {
		FLYING, TAKING_OFF, LANDING, LANDED;

		public static FlyingState fromControlState(CtrlState state)
		{
			switch (state)
			{
				case FLYING:
				case HOVERING:
				case TRANS_GOTOFIX:
					return FlyingState.FLYING;

				case TRANS_TAKEOFF:
					return FlyingState.TAKING_OFF;

				case TRANS_LANDING:
					return FlyingState.LANDING;

				default:
					return FlyingState.LANDED;
			}
		}

	}

	public static enum Mode {
		BOOTSTRAP, DEMO
	}

	public static enum NavDataTag {
		NAVDATA_DEMO_TAG(0), NAVDATA_TIME_TAG(1), NAVDATA_RAW_MEASURES_TAG(2), NAVDATA_PHYS_MEASURES_TAG(3), NAVDATA_GYROS_OFFSETS_TAG(4), NAVDATA_EULER_ANGLES_TAG(5), NAVDATA_REFERENCES_TAG(6), NAVDATA_TRIMS_TAG(7), NAVDATA_RC_REFERENCES_TAG(8), NAVDATA_PWM_TAG(9), NAVDATA_ALTITUDE_TAG(10), NAVDATA_VISION_RAW_TAG(11), NAVDATA_VISION_OF_TAG(12), NAVDATA_VISION_TAG(13), NAVDATA_VISION_PERF_TAG(14), NAVDATA_TRACKERS_SEND_TAG(15), NAVDATA_VISION_DETECT_TAG(16), NAVDATA_WATCHDOG_TAG(17), NAVDATA_ADC_DATA_FRAME_TAG(18), NAVDATA_VIDEO_STREAM_TAG(19), NAVDATA_CKS_TAG(0xFFFF);

		private int value;

		private NavDataTag(int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return value;
		}
	}

	private static final Logger log = Logger.getLogger(NavData.class.getName());

	public static NavData createFromData(ByteBuffer buf, int len) throws NavDataFormatException
	{
//		log.warning("Parsing navdata len=" + len);

		if (ByteOrder.LITTLE_ENDIAN != buf.order())
		{
			buf.order(ByteOrder.LITTLE_ENDIAN);
		}

		NavData data = new NavData();
		data.mode = NavData.Mode.BOOTSTRAP; // Assume we are in bootstrap

		// int offset = 0;

		int header = buf.getInt(0);

		if (header != 0x55667788)
			throw new NavDataFormatException("Error parsing NavData");
		// offset += 4;

		int state = buf.getInt(4);
		// offset += 4;

		parseState(data, state);

		data.sequence = buf.getInt(8);
		// offset += 4;

		// int vision_flag = buf.getInt(offset);
		// offset += 4;

		int offset = 16;

		// Read options
		while (offset < len)
		{
			int option_tag = buf.getShort(offset);
			offset += 2;
			int option_len = buf.getShort(offset);
			offset += 2;

			if (option_len == 0)
				throw new NavDataFormatException("Zero-len option with tag " + option_tag);

//			log.warning("At offset " + (offset - 4) + " found option " + option_tag + " with len=" + option_len);

			if (option_tag == NavDataTag.NAVDATA_DEMO_TAG.getValue())
			{
				parseDemoNavData(data, buf, offset);
				data.mode = NavData.Mode.DEMO;
			}
			else if (option_tag == NavDataTag.NAVDATA_CKS_TAG.getValue())
			{
				// this is last tag. We do not unpack it yet, but we gracefully
				// exit if it has been encountered.
				break; 
			}
			else if (option_tag == NavDataTag.NAVDATA_VISION_DETECT_TAG.getValue())
			{
				List<VisionTag> vtags = parseVisionTags(data, buf, offset);
				if (vtags != null)
					data.setVisionTags(vtags);
			}
			else
			{
//				log.warning("Skipping unknown NavData option with tag=" + option_tag);
			}
			offset = offset + option_len - 4;
		}

		// TODO: calculate checksum
//		log.warning("Got Nav data. mode " + data.mode);

		return data;
	}

	private static List<VisionTag> parseVisionTags(NavData data, ByteBuffer buf, int offset) throws NavDataFormatException
	{
		int nb_detected = buf.getInt(offset);
		offset += 4;

		if (nb_detected != 0)
			log.fine("" + nb_detected + " vision tags detected");

		if (nb_detected == 0)
			return null;

		assert (nb_detected > 0);
		List<VisionTag> res = new ArrayList<VisionTag>(nb_detected);
		for (int i = 0; i < nb_detected; i++)
		{
			int type = buf.getInt(offset + 4 * i);
			int xc = buf.getInt(offset + 4 * i + 1 * 4 * 4);
			int yc = buf.getInt(offset + 4 * i + 2 * 4 * 4);
			int width = buf.getInt(offset + 4 * i + 3 * 4 * 4);
			int height = buf.getInt(offset + 4 * i + 4 * 4 * 4);
			int dist = buf.getInt(offset + 4 * i + 5 * 4 * 4);

			VisionTag vt = new VisionTag(VisionTagType.fromInt(type), new Point(xc, yc), new Dimension(width, height), dist);
			log.fine("Vision#" + i + " " + vt.toString());
			res.add(vt);
		}

		return res;
	}

	private static void parseDemoNavData(NavData data, ByteBuffer buf, int offset) throws NavDataFormatException
	{
		data.ctrl_state = CtrlState.fromInt(buf.getInt(offset) >> 16);
		log.fine("Ctrl State " + data.ctrl_state);

		offset += 4;
		data.battery = buf.getInt(offset);
		offset += 4;
		data.pitch = buf.getFloat(offset) / 1000;
		offset += 4;
		data.roll = buf.getFloat(offset) / 1000;
		offset += 4;
		data.yaw = buf.getFloat(offset) / 1000;
		offset += 4;
		data.altitude = ((float) buf.getInt(offset)) / 1000;
		offset += 4;
		data.vx = buf.getFloat(offset);
		offset += 4;
		data.vy = buf.getFloat(offset);
		offset += 4;
		data.vz = buf.getFloat(offset);
		offset += 4;
	}

	private static void parseState(NavData data, int state)
	{
		data.flying = (state & 1) != 0;
		data.videoEnabled = (state & (1 << 1)) != 0;
		data.visionEnabled = (state & (1 << 2)) != 0;
		data.controlAlgorithm = (state & (1 << 3)) != 0 ? ControlAlgorithm.ANGULAR_SPEED_CONTROL : ControlAlgorithm.EULER_ANGELS_CONTROL;
		data.altitudeControlActive = (state & (1 << 4)) != 0;
		data.userFeedbackOn = (state & (1 << 5)) != 0;
		data.controlReceived = (state & (1 << 6)) != 0;
		data.trimReceived = (state & (1 << 7)) != 0;
		data.trimRunning = (state & (1 << 8)) != 0;
		data.trimSucceeded = (state & (1 << 9)) != 0;
		data.navDataDemoOnly = (state & (1 << 10)) != 0;
		data.navDataBootstrap = (state & (1 << 11)) != 0;
		data.motorsDown = (state & (1 << 12)) != 0;
		// ARDRONE_COM_LOST_MASK = 1U << 13, /*!< Communication Lost : (1) com
		// problem, (0) Com is ok */
		data.gyrometersDown = (state & (1 << 14)) != 0;
		data.batteryTooLow = (state & (1 << 15)) != 0;
		data.batteryTooHigh = (state & (1 << 16)) != 0;
		data.timerElapsed = (state & (1 << 17)) != 0;
		data.notEnoughPower = (state & (1 << 18)) != 0;
		data.angelsOutOufRange = (state & (1 << 19)) != 0;
		data.tooMuchWind = (state & (1 << 20)) != 0;
		data.ultrasonicSensorDeaf = (state & (1 << 21)) != 0;
		data.cutoutSystemDetected = (state & (1 << 22)) != 0;
		data.PICVersionNumberOK = (state & (1 << 23)) != 0;
		data.ATCodedThreadOn = (state & (1 << 24)) != 0;
		data.navDataThreadOn = (state & (1 << 25)) != 0;
		data.videoThreadOn = (state & (1 << 26)) != 0;
		data.acquisitionThreadOn = (state & (1 << 27)) != 0;
		data.controlWatchdogDelayed = (state & (1 << 28)) != 0;
		data.ADCWatchdogDelayed = (state & (1 << 29)) != 0;
		data.communicationProblemOccurred = (state & (1 << 30)) != 0;
		data.emergency = (state & (1 << 31)) != 0;
	}

	public String toDetailString()
	{
		NavData data = this;
		StringBuffer sb = new StringBuffer();

		sb.append("IsFlying: " + data.isFlying() + "\n");
		sb.append("IsVideoEnabled: " + data.isVideoEnabled() + "\n");
		sb.append("IsVisionEnabled: " + data.isVisionEnabled() + "\n");
		sb.append("controlAlgo: " + data.getControlAlgorithm() + "\n");
		sb.append("AltitudeControlActive: " + data.isAltitudeControlActive() + "\n");
		sb.append("IsUserFeedbackOn: " + data.isUserFeedbackOn() + "\n");
		sb.append("ControlReceived: " + data.isVideoEnabled() + "\n");
		sb.append("IsTrimReceived: " + data.isTrimReceived() + "\n");
		sb.append("IsTrimRunning: " + data.isTrimRunning() + "\n");
		sb.append("IsTrimSucceeded: " + data.isTrimSucceeded() + "\n");
		sb.append("IsNavDataDemoOnly: " + data.isNavDataDemoOnly() + "\n");
		sb.append("IsNavDataBootstrap: " + data.isNavDataBootstrap() + "\n");
		sb.append("IsMotorsDown: " + data.isMotorsDown() + "\n");
		sb.append("IsGyrometersDown: " + data.isGyrometersDown() + "\n");
		sb.append("IsBatteryLow: " + data.isBatteryTooLow() + "\n");
		sb.append("IsBatteryHigh: " + data.isBatteryTooHigh() + "\n");
		sb.append("IsTimerElapsed: " + data.isTimerElapsed() + "\n");
		sb.append("isNotEnoughPower: " + data.isNotEnoughPower() + "\n");
		sb.append("isAngelsOutOufRange: " + data.isAngelsOutOufRange() + "\n");
		sb.append("isTooMuchWind: " + data.isTooMuchWind() + "\n");
		sb.append("isUltrasonicSensorDeaf: " + data.isUltrasonicSensorDeaf() + "\n");
		sb.append("isCutoutSystemDetected: " + data.isCutoutSystemDetected() + "\n");
		sb.append("isPICVersionNumberOK: " + data.isPICVersionNumberOK() + "\n");
		sb.append("isATCodedThreadOn: " + data.isATCodedThreadOn() + "\n");
		sb.append("isNavDataThreadOn: " + data.isNavDataThreadOn() + "\n");
		sb.append("isVideoThreadOn: " + data.isVideoThreadOn() + "\n");
		sb.append("isAcquisitionThreadOn: " + data.isAcquisitionThreadOn() + "\n");
		sb.append("isControlWatchdogDelayed: " + data.isControlWatchdogDelayed() + "\n");
		sb.append("isADCWatchdogDelayed: " + data.isADCWatchdogDelayed() + "\n");
		sb.append("isCommunicationProblemOccurred: " + data.isCommunicationProblemOccurred() + "\n");
		sb.append("IsEmergency: " + data.isEmergency() + "\n");
		sb.append("CtrlState: " + data.getControlState() + "\n");
		sb.append("Battery: " + data.getBattery() + "\n");
		sb.append("Altitude: " + data.getAltitude() + "\n");
		sb.append("Pitch: " + data.getPitch() + "\n");
		sb.append("Roll: " + data.getRoll() + "\n");
		sb.append("Yaw: " + data.getYaw() + "\n");
		sb.append("X velocity: " + data.getVx() + "\n");
		sb.append("Y velocity: " + data.getLongitude() + "\n");
		sb.append("Z velocity: " + data.getVz() + "\n");
		sb.append("Vision Tags: " + data.getVisionTags() + "\n");

		return sb.toString();
	}

	public String toString()
	{
		return toDetailString() + "================================";
	}
	
	protected Mode mode;

	// state flags
	protected boolean flying;
	protected boolean videoEnabled;
	protected boolean visionEnabled;
	protected ControlAlgorithm controlAlgorithm;
	protected boolean altitudeControlActive;
	protected boolean userFeedbackOn; // /TODO better
										// name
	protected boolean controlReceived;
	protected boolean trimReceived;
	protected boolean trimRunning;
	protected boolean trimSucceeded;
	protected boolean navDataDemoOnly;
	protected boolean navDataBootstrap;
	protected boolean motorsDown;
	protected boolean gyrometersDown;
	protected boolean batteryTooLow;
	protected boolean batteryTooHigh;
	protected boolean timerElapsed;
	protected boolean notEnoughPower;
	protected boolean angelsOutOufRange;
	protected boolean tooMuchWind;
	protected boolean ultrasonicSensorDeaf;
	protected boolean cutoutSystemDetected;
	protected boolean PICVersionNumberOK;
	protected boolean ATCodedThreadOn;
	protected boolean navDataThreadOn;
	protected boolean videoThreadOn;
	protected boolean acquisitionThreadOn;
	protected boolean controlWatchdogDelayed;
	protected boolean ADCWatchdogDelayed;
	protected boolean communicationProblemOccurred;
	protected boolean emergency;

	// Common nav data
	protected int sequence;

	// Demo nav data
	protected CtrlState ctrl_state;
	protected int battery;
	protected float altitude;
	protected float pitch;
	protected float roll;
	protected float yaw;
	protected float vx;
	protected float vy;
	protected float vz;

	// Vision tags data
	protected List<VisionTag> vision_tags;

	/**
	 * 
	 * @return value in meters
	 */
	public float getAltitude()
	{
		return altitude;
	}

	public int getBattery()
	{
		return battery;
	}

	public ControlAlgorithm getControlAlgorithm()
	{
		return controlAlgorithm;
	}

	public CtrlState getControlState()
	{
		return ctrl_state;
	}

	public float getLongitude()
	{
		return vy;
	}

	public Mode getMode()
	{
		return mode;
	}

	/**
	 * 
	 * @return value in degrees
	 */
	public float getPitch()
	{
		return pitch;
	}

	/**
	 * 
	 * @return value in degrees
	 */
	public float getRoll()
	{
		return roll;
	}

	public int getSequence()
	{
		return sequence;
	}

	public float getVx()
	{
		return vx;
	}

	public float getVz()
	{
		return vz;
	}

	/**
	 * 
	 * @return value in degrees
	 */
	public float getYaw()
	{
		return yaw;
	}

	public boolean isAcquisitionThreadOn()
	{
		return acquisitionThreadOn;
	}

	public boolean isADCWatchdogDelayed()
	{
		return ADCWatchdogDelayed;
	}

	public boolean isAltitudeControlActive()
	{
		return altitudeControlActive;
	}

	public boolean isAngelsOutOufRange()
	{
		return angelsOutOufRange;
	}

	public boolean isATCodedThreadOn()
	{
		return ATCodedThreadOn;
	}

	public boolean isBatteryTooHigh()
	{
		return batteryTooHigh;
	}

	public boolean isBatteryTooLow()
	{
		return batteryTooLow;
	}

	public boolean isCommunicationProblemOccurred()
	{
		return communicationProblemOccurred;
	}

	public boolean isControlReceived()
	{
		return controlReceived;
	}

	public boolean isControlWatchdogDelayed()
	{
		return controlWatchdogDelayed;
	}

	public boolean isCutoutSystemDetected()
	{
		return cutoutSystemDetected;
	}

	public boolean isEmergency()
	{
		return emergency;
	}

	public boolean isFlying()
	{
		return flying;
	}

	public boolean isGyrometersDown()
	{
		return gyrometersDown;
	}

	public boolean isMotorsDown()
	{
		return motorsDown;
	}

	public boolean isNavDataBootstrap()
	{
		return navDataBootstrap;
	}

	public boolean isNavDataDemoOnly()
	{
		return navDataDemoOnly;
	}

	public boolean isNavDataThreadOn()
	{
		return navDataThreadOn;
	}

	public boolean isNotEnoughPower()
	{
		return notEnoughPower;
	}

	public boolean isPICVersionNumberOK()
	{
		return PICVersionNumberOK;
	}

	public boolean isTimerElapsed()
	{
		return timerElapsed;
	}

	public boolean isTooMuchWind()
	{
		return tooMuchWind;
	}

	public boolean isTrimReceived()
	{
		return trimReceived;
	}

	public boolean isTrimRunning()
	{
		return trimRunning;
	}

	public boolean isTrimSucceeded()
	{
		return trimSucceeded;
	}

	public boolean isUltrasonicSensorDeaf()
	{
		return ultrasonicSensorDeaf;
	}

	public boolean isUserFeedbackOn()
	{
		return userFeedbackOn;
	}

	public boolean isVideoEnabled()
	{
		return videoEnabled;
	}

	public boolean isVideoThreadOn()
	{
		return videoThreadOn;
	}

	public boolean isVisionEnabled()
	{
		return visionEnabled;
	}

	public FlyingState getFlyingState()
	{
		return FlyingState.fromControlState(ctrl_state);
	}

	public List<VisionTag> getVisionTags()
	{
		return vision_tags;
	}

	public void setVisionTags(List<VisionTag> vision_tags)
	{
		this.vision_tags = vision_tags;
	}

}
