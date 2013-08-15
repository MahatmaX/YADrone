package de.yadrone.base.navdata;

import java.util.Arrays;

public class GyroPhysData {

	private float[] phys_gyros;
	private int alim3v3;
	private int vrefEpson;
	private int vrefIDG;

	int gyro_temp;

	public GyroPhysData(int gyro_temp, float[] phys_gyros, int alim3v3, int vrefEpson, int vrefIDG) {
		this.gyro_temp = gyro_temp;
		this.phys_gyros = phys_gyros;
		this.alim3v3 = alim3v3;
		this.vrefEpson = vrefEpson;
		this.vrefIDG = vrefIDG;
	}

	/**
	 * @return the phys_gyros
	 */
	public float[] getPhysGyros() {
		return phys_gyros;
	}

	/**
	 * @return the alim3v3
	 */
	public int getAlim3v3() {
		return alim3v3;
	}

	/**
	 * @return the vrefEpson
	 */
	public int getVrefEpson() {
		return vrefEpson;
	}

	/**
	 * @return the vrefIDG
	 */
	public int getVrefIDG() {
		return vrefIDG;
	}

	/**
	 * @return the gyro_temp
	 */
	public int getGyroTemp() {
		return gyro_temp;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GyroPhysData [phys_gyros=");
		builder.append(Arrays.toString(phys_gyros));
		builder.append(", alim3v3=");
		builder.append(alim3v3);
		builder.append(", vrefEpson=");
		builder.append(vrefEpson);
		builder.append(", vrefIDG=");
		builder.append(vrefIDG);
		builder.append(", gyro_temp=");
		builder.append(gyro_temp);
		builder.append("]");
		return builder.toString();
	}

}
