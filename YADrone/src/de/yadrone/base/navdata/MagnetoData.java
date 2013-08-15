package de.yadrone.base.navdata;

import java.util.Arrays;

public class MagnetoData {

	private short[] m;
	private float[] mraw;
	private float[] mrectified;
	private float[] m_;
	private float heading_unwrapped;
	private float heading_gyro_unwrapped;
	private float heading_fusion_unwrapped;
	private byte calibration_ok;
	private int state;
	private float radius;
	private float error_mean;
	private float error_var;

	public MagnetoData(short[] m, float[] mraw, float[] mrectified, float[] m_, float heading_unwrapped,
			float heading_gyro_unwrapped, float heading_fusion_unwrapped, byte calibration_ok, int state, float radius,
			float error_mean, float error_var) {
		super();
		this.m = m;
		this.mraw = mraw;
		this.mrectified = mrectified;
		this.m_ = m_;
		this.heading_unwrapped = heading_unwrapped;
		this.heading_gyro_unwrapped = heading_gyro_unwrapped;
		this.heading_fusion_unwrapped = heading_fusion_unwrapped;
		this.calibration_ok = calibration_ok;
		this.state = state;
		this.radius = radius;
		this.error_mean = error_mean;
		this.error_var = error_var;
	}

	/**
	 * @return the m
	 */
	public short[] getM() {
		return m;
	}

	/**
	 * @return the mraw
	 */
	public float[] getMraw() {
		return mraw;
	}

	/**
	 * @return the mrectified
	 */
	public float[] getMrectified() {
		return mrectified;
	}

	/**
	 * @return the m_
	 */
	public float[] getM_() {
		return m_;
	}

	/**
	 * @return the heading_unwrapped
	 */
	public float getHeadingUnwrapped() {
		return heading_unwrapped;
	}

	/**
	 * @return the heading_gyro_unwrapped
	 */
	public float getHeadingGyroUnwrapped() {
		return heading_gyro_unwrapped;
	}

	/**
	 * @return the heading_fusion_unwrapped
	 */
	public float getHeadingFusionUnwrapped() {
		return heading_fusion_unwrapped;
	}

	/**
	 * @return the calibration_ok
	 */
	public byte getCalibrationOk() {
		return calibration_ok;
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/**
	 * @return the radius
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * @return the error_mean
	 */
	public float getErrorMean() {
		return error_mean;
	}

	/**
	 * @return the error_var
	 */
	public float getErrorVar() {
		return error_var;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MagnetoData [m=");
		builder.append(Arrays.toString(m));
		builder.append(", mraw=");
		builder.append(Arrays.toString(mraw));
		builder.append(", mrectified=");
		builder.append(Arrays.toString(mrectified));
		builder.append(", m_=");
		builder.append(Arrays.toString(m_));
		builder.append(", heading_unwrapped=");
		builder.append(heading_unwrapped);
		builder.append(", heading_gyro_unwrapped=");
		builder.append(heading_gyro_unwrapped);
		builder.append(", heading_fusion_unwrapped=");
		builder.append(heading_fusion_unwrapped);
		builder.append(", calibration_ok=");
		builder.append(calibration_ok);
		builder.append(", state=");
		builder.append(state);
		builder.append(", radius=");
		builder.append(radius);
		builder.append(", error_mean=");
		builder.append(error_mean);
		builder.append(", error_var=");
		builder.append(error_var);
		builder.append("]");
		return builder.toString();
	}

}
