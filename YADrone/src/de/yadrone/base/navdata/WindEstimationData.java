package de.yadrone.base.navdata;

import java.util.Arrays;

public class WindEstimationData {

	// estimated wind speed [m/s]
	float estimatedSpeed;

	// estimated wind direction in North-East frame [rad] e.g. if wind_angle is
	// pi/4, wind is from South-West to North-East
	float estimatedAngle;

	float[] state;
	float[] magneto;

	public WindEstimationData(float estimatedSpeed, float estimatedAngle, float[] state, float[] magneto) {
		super();
		this.estimatedSpeed = estimatedSpeed;
		this.estimatedAngle = estimatedAngle;
		this.state = state;
		this.magneto = magneto;
	}

	/**
	 * @return the estimatedSpeed
	 */
	public float getEstimatedSpeed() {
		return estimatedSpeed;
	}

	/**
	 * @return the estimatedAngle
	 */
	public float getEstimatedAngle() {
		return estimatedAngle;
	}

	/**
	 * @return the state
	 */
	public float[] getState() {
		return state;
	}

	/**
	 * @return the magneto
	 */
	public float[] getMagneto() {
		return magneto;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WindEstimationData [estimatedSpeed=");
		builder.append(estimatedSpeed);
		builder.append(", estimatedAngle=");
		builder.append(estimatedAngle);
		builder.append(", state=");
		builder.append(Arrays.toString(state));
		builder.append(", magneto=");
		builder.append(Arrays.toString(magneto));
		builder.append("]");
		return builder.toString();
	}

}
