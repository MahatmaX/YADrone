package de.yadrone.base.navdata;

public class Pressure {
	private int pressure;
	private short temperature;
	private int temperature_meas;
	private int pressure_meas;

	public Pressure(int pressure, int pressure_meas) {
		super();
		this.pressure = pressure;
		this.pressure_meas = pressure_meas;
	}

	/**
	 * @return the pressure
	 */
	public int getValue() {
		return pressure;
	}

	/**
	 * @return the pressure_meas
	 */
	public int getMeasurement() {
		return pressure_meas;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Pressure [pressure=");
		builder.append(pressure);
		builder.append(", temperature=");
		builder.append(temperature);
		builder.append(", temperature_meas=");
		builder.append(temperature_meas);
		builder.append(", pressure_meas=");
		builder.append(pressure_meas);
		builder.append("]");
		return builder.toString();
	}

}
