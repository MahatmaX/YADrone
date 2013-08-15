package de.yadrone.base.navdata;

import java.util.Arrays;

public class AdcFrame {
	private int version;
	private byte[] data_frame;

	public AdcFrame(int version, byte[] data_frame) {
		super();
		this.version = version;
		this.data_frame = data_frame;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @return the data_frame
	 */
	public byte[] getData_frame() {
		return data_frame;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AdcData [version=");
		builder.append(version);
		builder.append(", data_frame=");
		builder.append(Arrays.toString(data_frame));
		builder.append("]");
		return builder.toString();
	}

}
