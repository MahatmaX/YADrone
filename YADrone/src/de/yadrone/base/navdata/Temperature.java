package de.yadrone.base.navdata;

public class Temperature {
	private short temperature;
	private int temperature_meas;

	public Temperature(short temperature, int temperature_meas) {
		super();
		this.temperature = temperature;
		this.temperature_meas = temperature_meas;
	}

	/**
	 * @return the temperature
	 */
	public short getValue() {
		return temperature;
	}

	/**
	 * @return the temperature_meas
	 */
	public int getMeasurement() {
		return temperature_meas;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Temperature [temperature=");
		builder.append(temperature);
		builder.append(", temperature_meas=");
		builder.append(temperature_meas);
		builder.append("]");
		return builder.toString();
	}

}
