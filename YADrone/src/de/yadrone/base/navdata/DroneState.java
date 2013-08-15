/*
 * Copyright 2010 Cliff L. Biffle.  All Rights Reserved.
 * Use of this source code is governed by a BSD-style license that can be found
 * in the LICENSE file.
 */
package de.yadrone.base.navdata;


public class DroneState {
	//TODO: determine the version Runtime from the Drone
	public static final int ARDRONE10 = 0;
	public static final int ARDRONE20 = 1;
	public static final int VERSION = ARDRONE20;

	private final int state;
	private final int vision;
	
	public DroneState(int state, int vision) {
		this.state = state;
		this.vision = vision;
	}

	public int getStateBits() {
		return state;
	}
	
	public boolean isVisionDefined() {
	    return vision == 1;
	}

	public boolean isFlying() {
	    return (state & (1 << 0)) != 0;
	}

	public boolean isVideoEnabled() {
	    return (state & (1 << 1)) != 0;
	}

	public boolean isVisionEnabled() {
	    return (state & (1 << 2)) != 0;
	}

	public ControlAlgorithm getControlAlgorithm() {
	    return (state & (1 << 3)) != 0 ? ControlAlgorithm.ANGULAR_SPEED_CONTROL
	            : ControlAlgorithm.EULER_ANGELS_CONTROL;
	}

	public boolean isAltitudeControlActive() {
	    return (state & (1 << 4)) != 0;
	}

	public boolean isUserFeedbackOn() { // TODO better name
	    return (state & (1 << 5)) != 0;
	}

	public boolean isControlReceived() {
	    return (state & (1 << 6)) != 0;
	}

	public boolean isTrimReceived() { // ARDRONE 1.0 only?
	    return (state & (1 << 7)) != 0;
	}

	public boolean isCameraReady() { // ARDRONE 2.0 only?
	    return (state & (1 << 7)) != 0; // See SDK2.0, config.h
	}

	public boolean isTrimRunning() { // ARDRONE 1.0 only?
	    return (state & (1 << 8)) != 0;
	}

	public boolean isTravellingMask() { // ARDRONE 2.0 only?
	    return (state & (1 << 8)) != 0; // See SDK2.0, config.h
	}

	public boolean isTrimSucceeded() { // ARDRONE 1.0 only?
	    return (state & (1 << 9)) != 0;
	}

	public boolean isUsbKeyReady() { // ARDRONE 2.0 only?
	    return (state & (1 << 9)) != 0; // See SDK2.0, config.h
	}

	public boolean isNavDataDemoOnly() {
	    return (state & (1 << 10)) != 0;
	}

	public boolean isNavDataBootstrap() {
	    return (state & (1 << 11)) != 0;
	}

	public boolean isMotorsDown() {
	    return (state & (1 << 12)) != 0;
	}

	public boolean isCommunicationLost() { // ARDRONE 2.0 only?
	    return (state & (1 << 13)) != 0; // Communication Lost : (1) com
	                                     // problem, (0) Com is ok
	}

	public boolean isGyrometersDown() { // ARDRONE 1.0 only?
	    return (state & (1 << 14)) != 0;
	}

	public boolean isSoftwareFaultDetected() { // ARDRONE 2.0 only?
	    return (state & (1 << 14)) != 0;
	}

	public boolean isBatteryTooLow() {
	    return (state & (1 << 15)) != 0;
	}

	public boolean isBatteryTooHigh() { // ARDRONE 1.0 only?
	    return (state & (1 << 16)) != 0;
	}

	public boolean isUserEmergencyLanding() { // ARDRONE 2.0 only?
	    return (state & (1 << 16)) != 0;
	}

	public boolean isTimerElapsed() {
	    return (state & (1 << 17)) != 0;
	}

	public boolean isNotEnoughPower() { // ARDRONE 1.0 only?
	    return (state & (1 << 18)) != 0;
	}

	public boolean isMagnetoCalibrationNeeded() { // ARDRONE 2.0 only?
	    return (state & (1 << 18)) != 0;
	}

	public boolean isAngelsOutOufRange() {
	    return (state & (1 << 19)) != 0;
	}

	public boolean isTooMuchWind() {
	    return (state & (1 << 20)) != 0;
	}

	public boolean isUltrasonicSensorDeaf() {
	    return (state & (1 << 21)) != 0;
	}

	public boolean isCutoutSystemDetected() {
	    return (state & (1 << 22)) != 0;
	}

	public boolean isPICVersionNumberOK() {
	    return (state & (1 << 23)) != 0;
	}

	public boolean isATCodedThreadOn() {
	    return (state & (1 << 24)) != 0;
	}

	public boolean isNavDataThreadOn() {
	    return (state & (1 << 25)) != 0;
	}

	public boolean isVideoThreadOn() {
	    return (state & (1 << 26)) != 0;
	}

	public boolean isAcquisitionThreadOn() {
	    return (state & (1 << 27)) != 0;
	}

	public boolean isControlWatchdogDelayed() {
	    return (state & (1 << 28)) != 0;
	}

	public boolean isADCWatchdogDelayed() {
	    return (state & (1 << 29)) != 0;
	}

	public boolean isCommunicationProblemOccurred() {
	    return (state & (1 << 30)) != 0;
	}

	public boolean isEmergency() {
	    return (state & (1 << 31)) != 0;
	}

	public boolean equals(Object o) {
		if (o == null || o.getClass() != getClass())
			return false;
		return state == ((DroneState) o).state && vision == ((DroneState) o).vision;
	}

	public int hashCode() {
		return 31 * state + 15 * vision;
	}

	public String toString() {
	    StringBuffer sb = new StringBuffer();
	
	    sb.append("isVisionDefined: " + isVisionDefined() + "\n");
	    sb.append("isFlying: " + isFlying() + "\n");
	    sb.append("isVideoEnabled: " + isVideoEnabled() + "\n");
	    sb.append("isVisionEnabled: " + isVisionEnabled() + "\n");
	    sb.append("controlAlgo: " + getControlAlgorithm() + "\n");
	    sb.append("AltitudeControlActive: " + isAltitudeControlActive() + "\n");
	    sb.append("isUserFeedbackOn: " + isUserFeedbackOn() + "\n");
	    sb.append("ControlReceived: " + isControlReceived() + "\n");
	    if (VERSION == ARDRONE20) {
	        sb.append("isCameraReady: " + isCameraReady() + "\n");
	        sb.append("isTravellingMask: " + isTravellingMask() + "\n");
	        sb.append("isUsbKeyReady: " + isUsbKeyReady() + "\n");
	        sb.append("isSoftwareFaultDetected: " + isSoftwareFaultDetected()
	                + "\n");
	        sb.append("isUserEmergencyLanding: " + isUserEmergencyLanding()
	                + "\n");
	        sb.append("isMagnetoCalibrationNeeded: "
	                + isMagnetoCalibrationNeeded() + "\n");
	    } else {
	        sb.append("isTrimReceived: " + isTrimReceived() + "\n");
	        sb.append("isTrimRunning: " + isTrimRunning() + "\n");
	        sb.append("isTrimSucceeded: " + isTrimSucceeded() + "\n");
	        sb.append("isCommunicationLost: " + isCommunicationLost() + "\n");
	        sb.append("isGyrometersDown: " + isGyrometersDown() + "\n");
	        sb.append("isBatteryHigh: " + isBatteryTooHigh() + "\n");
	        sb.append("isNotEnoughPower: " + isNotEnoughPower() + "\n");
	    }
	    sb.append("isNavDataDemoOnly: " + isNavDataDemoOnly() + "\n");
	    sb.append("isNavDataBootstrap: " + isNavDataBootstrap() + "\n");
	    sb.append("isMotorsDown: " + isMotorsDown() + "\n");
	    sb.append("isBatteryLow: " + isBatteryTooLow() + "\n");
	    sb.append("isTimerElapsed: " + isTimerElapsed() + "\n");
	    sb.append("isAngelsOutOufRange: " + isAngelsOutOufRange() + "\n");
	    sb.append("isTooMuchWind: " + isTooMuchWind() + "\n");
	    sb.append("isUltrasonicSensorDeaf: " + isUltrasonicSensorDeaf() + "\n");
	    sb.append("isCutoutSystemDetected: " + isCutoutSystemDetected() + "\n");
	    sb.append("isPICVersionNumberOK: " + isPICVersionNumberOK() + "\n");
	    sb.append("isATCodedThreadOn: " + isATCodedThreadOn() + "\n");
	    sb.append("isNavDataThreadOn: " + isNavDataThreadOn() + "\n");
	    sb.append("isVideoThreadOn: " + isVideoThreadOn() + "\n");
	    sb.append("isAcquisitionThreadOn: " + isAcquisitionThreadOn() + "\n");
	    sb.append("isControlWatchdogDelayed: " + isControlWatchdogDelayed()
	            + "\n");
	    sb.append("isADCWatchdogDelayed: " + isADCWatchdogDelayed() + "\n");
	    sb.append("isCommunicationProblemOccurred: "
	            + isCommunicationProblemOccurred() + "\n");
	    sb.append("IsEmergency: " + isEmergency() + "\n");
	    //for (int n = 0; n < options.length; n++) {
	    //        sb.append(options[n]);           
	    //}
	    return sb.toString();
	}


}