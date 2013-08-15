package de.yadrone.base.navdata;

import java.util.Arrays;

public class AcceleroRawData {

	private int[] raw_accs;
	
	public AcceleroRawData(int[] raw_accs) {
		this.raw_accs = raw_accs; 
	}

	/**
	 * @return the raw_accs
	 */
	public int[] getRawAccs() {
		return raw_accs;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AcceleroRawData [raw_accs=");
		builder.append(Arrays.toString(raw_accs));
		builder.append("]");
		return builder.toString();
	}

}
