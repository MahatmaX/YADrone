package de.yadrone.base.navdata;

import java.util.Arrays;

public class VisionPerformance {
	private float time_szo;
	private float time_corners;
	private float time_compute;
	private float time_tracking;
	private float time_trans;
	private float time_update;
	private float[] time_custom;

	public VisionPerformance(float time_szo, float time_corners, float time_compute, float time_tracking,
			float time_trans, float time_update, float[] time_custom) {
		super();
		this.time_szo = time_szo;
		this.time_corners = time_corners;
		this.time_compute = time_compute;
		this.time_tracking = time_tracking;
		this.time_trans = time_trans;
		this.time_update = time_update;
		this.time_custom = time_custom;
	}

	/**
	 * @return the time_szo
	 */
	public float getTimeSzo() {
		return time_szo;
	}

	/**
	 * @return the time_corners
	 */
	public float getTimeCorners() {
		return time_corners;
	}

	/**
	 * @return the time_compute
	 */
	public float getTimeCompute() {
		return time_compute;
	}

	/**
	 * @return the time_tracking
	 */
	public float getTimeTracking() {
		return time_tracking;
	}

	/**
	 * @return the time_trans
	 */
	public float getTimeTrans() {
		return time_trans;
	}

	/**
	 * @return the time_update
	 */
	public float getTimeUpdate() {
		return time_update;
	}

	/**
	 * @return the time_custom
	 */
	public float[] getTimeCustom() {
		return time_custom;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VisionPerormance [time_szo=");
		builder.append(time_szo);
		builder.append(", time_corners=");
		builder.append(time_corners);
		builder.append(", time_compute=");
		builder.append(time_compute);
		builder.append(", time_tracking=");
		builder.append(time_tracking);
		builder.append(", time_trans=");
		builder.append(time_trans);
		builder.append(", time_update=");
		builder.append(time_update);
		builder.append(", time_custom=");
		builder.append(Arrays.toString(time_custom));
		builder.append("]");
		return builder.toString();
	}

}
