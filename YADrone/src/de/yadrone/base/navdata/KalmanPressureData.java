package de.yadrone.base.navdata;


public class KalmanPressureData {

	private float offset_pressure;
	private float est_z;
	private float est_zdot;
	private float est_bias_PWM;
	private float est_biais_pression;
	private float offset_US;
	private float prediction_US;
	private float cov_alt;
	private float cov_PWM;
	private float cov_vitesse;
	private boolean effet_sol;
	private float somme_inno;
	private boolean rejet_US;
	private float u_multisinus;
	private float gaz_altitude;
	private boolean multisinus;
	private boolean multisinus_debut;

	public KalmanPressureData(float offset_pressure, float est_z, float est_zdot, float est_bias_PWM,
			float est_biais_pression, float offset_US, float prediction_US, float cov_alt, float cov_PWM,
			float cov_vitesse, boolean effet_sol, float somme_inno, boolean rejet_US, float u_multisinus,
			float gaz_altitude, boolean multisinus, boolean multisinus_debut) {
		super();
		this.offset_pressure = offset_pressure;
		this.est_z = est_z;
		this.est_zdot = est_zdot;
		this.est_bias_PWM = est_bias_PWM;
		this.est_biais_pression = est_biais_pression;
		this.offset_US = offset_US;
		this.prediction_US = prediction_US;
		this.cov_alt = cov_alt;
		this.cov_PWM = cov_PWM;
		this.cov_vitesse = cov_vitesse;
		this.effet_sol = effet_sol;
		this.somme_inno = somme_inno;
		this.rejet_US = rejet_US;
		this.u_multisinus = u_multisinus;
		this.gaz_altitude = gaz_altitude;
		this.multisinus = multisinus;
		this.multisinus_debut = multisinus_debut;
	}

	/**
	 * @return the offset_pressure
	 */
	public float getOffsetPressure() {
		return offset_pressure;
	}

	/**
	 * @return the est_z
	 */
	public float getEstZ() {
		return est_z;
	}

	/**
	 * @return the est_zdot
	 */
	public float getEstZdot() {
		return est_zdot;
	}

	/**
	 * @return the est_bias_PWM
	 */
	public float getEstBiasPWM() {
		return est_bias_PWM;
	}

	/**
	 * @return the est_biais_pression
	 */
	public float getEstBiaisPression() {
		return est_biais_pression;
	}

	/**
	 * @return the offset_US
	 */
	public float getOffsetUS() {
		return offset_US;
	}

	/**
	 * @return the prediction_US
	 */
	public float getPredictionUS() {
		return prediction_US;
	}

	/**
	 * @return the cov_alt
	 */
	public float getCovAlt() {
		return cov_alt;
	}

	/**
	 * @return the cov_PWM
	 */
	public float getCovPWM() {
		return cov_PWM;
	}

	/**
	 * @return the cov_vitesse
	 */
	public float getCovVitesse() {
		return cov_vitesse;
	}

	/**
	 * @return the effet_sol
	 */
	public boolean isEffetSol() {
		return effet_sol;
	}

	/**
	 * @return the somme_inno
	 */
	public float getSommeInno() {
		return somme_inno;
	}

	/**
	 * @return the rejet_US
	 */
	public boolean isRejetUS() {
		return rejet_US;
	}

	/**
	 * @return the u_multisinus
	 */
	public float getUMultisinus() {
		return u_multisinus;
	}

	/**
	 * @return the gaz_altitude
	 */
	public float getGazAltitude() {
		return gaz_altitude;
	}

	/**
	 * @return the multisinus
	 */
	public boolean isMultisinus() {
		return multisinus;
	}

	/**
	 * @return the multisinus_debut
	 */
	public boolean isMultisinusDebut() {
		return multisinus_debut;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "KalmanPressureData [offset_pressure=" + offset_pressure + ", est_z=" + est_z + ", est_zdot=" + est_zdot
				+ ", est_bias_PWM=" + est_bias_PWM + ", est_biais_pression=" + est_biais_pression + ", offset_US="
				+ offset_US + ", prediction_US=" + prediction_US + ", cov_alt=" + cov_alt + ", cov_PWM=" + cov_PWM
				+ ", cov_vitesse=" + cov_vitesse + ", effet_sol=" + effet_sol + ", somme_inno=" + somme_inno
				+ ", rejet_US=" + rejet_US + ", u_multisinus=" + u_multisinus + ", gaz_altitude=" + gaz_altitude
				+ ", multisinus=" + multisinus + ", multisinus_debut=" + multisinus_debut + "]";
	}

}
