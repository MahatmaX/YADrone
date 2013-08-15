package de.yadrone.base.navdata;

import java.util.Arrays;

public class AcceleroPhysData {

	private float accs_temp;
	private float[] phys_accs;
	private int alim3v3;

	public AcceleroPhysData(float accs_temp, float[] phys_accs, int alim3v3) {
		super();
		this.accs_temp = accs_temp;
		this.phys_accs = phys_accs;
		this.alim3v3 = alim3v3;
	}

	/**
	 * @return the accs_temp
	 */
	public float getAccsTemp() {
		return accs_temp;
	}

	/**
	 * @return the phys_accs
	 */
	public float[] getPhysAccs() {
		return phys_accs;
	}

	/**
	 * @return the alim3v3
	 */
	public int getAlim3v3() {
		return alim3v3;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AcceleroPhysData [accs_temp=");
		builder.append(accs_temp);
		builder.append(", phys_accs=");
		builder.append(Arrays.toString(phys_accs));
		builder.append(", alim3v3=");
		builder.append(alim3v3);
		builder.append("]");
		return builder.toString();
	}

}
