package de.yadrone.base.navdata;

public class PWMData {
	private short[] motor;
	private short[] sat_motor;
	private float gaz_feed_forward;
	private float gaz_altitude;
	private float altitude_integral;
	private float vz_ref;
	private int[] u_pry;
	private float yaw_u_I;
	private int[] u_planif_pry;
	private float u_gaz_planif;
	private int[] current_motor;
	private float altitude_prop;
	private float altitude_der;

	public PWMData(short[] motor, short[] sat_motor, float gaz_feed_forward, float gaz_altitude, float altitude_integral,
			float vz_ref, int[] u_pry, float yaw_u_I, int[] u_planif_pry, float u_gaz_planif, int[] current_motor,
			float altitude_prop, float altitude_der) {
		super();
		this.motor = motor;
		this.sat_motor = sat_motor;
		this.gaz_feed_forward = gaz_feed_forward;
		this.gaz_altitude = gaz_altitude;
		this.altitude_integral = altitude_integral;
		this.vz_ref = vz_ref;
		this.u_pry = u_pry;
		this.yaw_u_I = yaw_u_I;
		this.u_planif_pry = u_planif_pry;
		this.u_gaz_planif = u_gaz_planif;
		this.current_motor = current_motor;
		this.altitude_prop = altitude_prop;
		this.altitude_der = altitude_der;
	}

	/**
	 * @return the motor
	 */
	public short[] getMotor() {
		return motor;
	}

	/**
	 * @return the motor2
	 */
	public short[] getSatMotor() {
		return sat_motor;
	}

	/**
	 * @return the gaz_feed_forward
	 */
	public float getGazFeedForward() {
		return gaz_feed_forward;
	}

	/**
	 * @return the gaz_altitude
	 */
	public float getGazAltitude() {
		return gaz_altitude;
	}

	/**
	 * @return the altitude_integral
	 */
	public float getAltitudeIntegral() {
		return altitude_integral;
	}

	/**
	 * @return the vz_ref
	 */
	public float getVzRef() {
		return vz_ref;
	}

	/**
	 * @return the u_pry
	 */
	public int[] getUPRY() {
		return u_pry;
	}

	/**
	 * @return the yaw_u_I
	 */
	public float getYawUI() {
		return yaw_u_I;
	}

	/**
	 * @return the u_planif_pry
	 */
	public int[] getUPlanifPRY() {
		return u_planif_pry;
	}

	/**
	 * @return the u_gaz_planif
	 */
	public float getUGazPlanif() {
		return u_gaz_planif;
	}

	/**
	 * @return the current_motor
	 */
	public int[] getCurrentMotor() {
		return current_motor;
	}

	/**
	 * @return the altitude_prop
	 */
	public float getAltitudeProp() {
		return altitude_prop;
	}

	/**
	 * @return the altitude_der
	 */
	public float getAltitudeDer() {
		return altitude_der;
	}

}
