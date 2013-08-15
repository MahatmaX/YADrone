package de.yadrone.base.navdata;

import java.util.Arrays;

public class Altitude {

	private int altitude_vision;
	private float altitude_vz;
	private int altitude_ref;
	private int altitude_raw;
	private float obs_accZ;
	private float obs_alt;
	private float[] obs_x;
	private int obs_state;
	private float[] est_vb;
	private int est_state;

	public Altitude(int altitude_vision, float altitude_vz, int altitude_ref, int altitude_raw, float obs_accZ,
			float obs_alt, float[] obs_x, int obs_state, float[] est_vb, int est_state) {
		super();
		this.altitude_vision = altitude_vision;
		this.altitude_vz = altitude_vz;
		this.altitude_ref = altitude_ref;
		this.altitude_raw = altitude_raw;
		this.obs_accZ = obs_accZ;
		this.obs_alt = obs_alt;
		this.obs_x = obs_x;
		this.obs_state = obs_state;
		this.est_vb = est_vb;
		this.est_state = est_state;
	}

	/**
	 * @return the altitude_vision
	 */
	public int getVision() {
		return altitude_vision;
	}

	/**
	 * @return the altitude_vz
	 */
	public float getZVelocity() {
		return altitude_vz;
	}

	/**
	 * @return the altitude_ref
	 */
	public int getRef() {
		return altitude_ref;
	}

	/**
	 * @return the altitude_raw
	 */
	public int getRaw() {
		return altitude_raw;
	}

	/**
	 * @return the obs_accZ
	 */
	public float getObsAccZ() {
		return obs_accZ;
	}

	/**
	 * @return the obs_alt
	 */
	public float getObsAlt() {
		return obs_alt;
	}

	/**
	 * @return the obs_x
	 */
	public float[] getObsX() {
		return obs_x;
	}

	/**
	 * @return the obs_state
	 */
	public int getObsState() {
		return obs_state;
	}

	/**
	 * @return the est_vb
	 */
	public float[] getEstVb() {
		return est_vb;
	}

	/**
	 * @return the est_state
	 */
	public int getEstState() {

		return est_state;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Altitude [altitude_vision=");
		builder.append(altitude_vision);
		builder.append(", altitude_vz=");
		builder.append(altitude_vz);
		builder.append(", altitude_ref=");
		builder.append(altitude_ref);
		builder.append(", altitude_raw=");
		builder.append(altitude_raw);
		builder.append(", obs_accZ=");
		builder.append(obs_accZ);
		builder.append(", obs_alt=");
		builder.append(obs_alt);
		builder.append(", obs_x=");
		builder.append(Arrays.toString(obs_x));
		builder.append(", obs_state=");
		builder.append(obs_state);
		builder.append(", est_vb=");
		builder.append(Arrays.toString(est_vb));
		builder.append(", est_state=");
		builder.append(est_state);
		builder.append("]");
		return builder.toString();
	}

}
